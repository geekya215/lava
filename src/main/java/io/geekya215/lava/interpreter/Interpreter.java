package io.geekya215.lava.interpreter;


import io.geekya215.lava.common.Option;
import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.parser.Expr;
import io.geekya215.lava.tokenizer.Keywords;
import io.geekya215.lava.tokenizer.Operators;
import io.geekya215.lava.tokenizer.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;


public final class Interpreter {
    private static final Env nativeEnv = new Env(Option.none(), new HashMap<>());
    private static final Env runtimeEnv;

    public static final class BuiltinValue {
        public static final Expr TRUE = new Expr.Atom(new Token.Symbol("#t"));
        public static final Expr FALSE = new Expr.Atom(new Token.Symbol("#f"));
        public static final Expr NIL = new Expr.Vec(List.of());
    }

    static {
        runtimeEnv = Env.extend(nativeEnv);
    }

    public static Expr eval(Expr expr) {
        return eval(expr, runtimeEnv);
    }

    public static Expr eval(Expr expr, Env env) {
        return switch (expr) {
            case Expr.Quote(Expr quote) -> quote;
            case Expr.Atom(Token tok) -> {
                if (tok instanceof Token.Symbol(String symbolName)) {
                    yield env.get(symbolName)
                            .getOrThrow(() -> new EvalException("attempted to access undefined variable: " + symbolName));
                } else {
                    // Todo
                    // how to display other tokens?
                    yield expr;
                }
            }
            case Expr.Vec(List<Expr> exprs) -> {
                if (exprs.isEmpty()) {
                    yield new Expr.Vec(new ArrayList<>());
                }

                Expr head = exprs.getFirst();
                List<Expr> rest = exprs.subList(1, exprs.size());
                yield switch (head) {
                    case Expr.Atom(Token.Keyword(Keywords keywords)) -> switch (keywords) {
                        case Keywords.DEF _ -> evalDef(rest, env);
                        case Keywords.CONS _ -> evalCons(rest, env);
                        case Keywords.CAR _ -> evalCar(rest, env);
                        case Keywords.CDR _ -> evalCdr(rest, env);
                        case Keywords.LIST _ -> evalList(rest, env);
                        case Keywords.IF _ -> evalIf(rest, env);
                        case Keywords.COND _ -> evalCond(rest, env);
                        case Keywords.QUOTE _ -> evalQuote(rest, env);
                        case Keywords.FN _ -> evalFn(rest, env);
                        case Keywords.PROG _ -> evalProg(rest, env);
                        case Keywords.EQ _ -> evalEq(rest, env);
                        case Keywords.EVAL _ -> evalEval(rest, env);

                        default -> throw new EvalException("Unexpected keyword: " + keywords);
                    };
                    case Expr.Atom(Token.Operator(Operators operators)) -> switch (operators) {
                        case Operators.Plus _ -> applyArithmetic(rest, (a, b) -> a + b, env);
                        case Operators.Minus _ -> applyArithmetic(rest, (a, b) -> a - b, env);
                        case Operators.Mul _ -> applyArithmetic(rest, (a, b) -> a * b, env);
                        case Operators.Div _ -> applyArithmetic(rest, (a, b) -> a / b, env);
                        case Operators.Mod _ -> applyArithmetic(rest, (a, b) -> a % b, env);
                        case Operators.LeftShift _ -> applyArithmetic(rest, (a, b) -> a << b, env);
                        case Operators.RightShift _ -> applyArithmetic(rest, (a, b) -> a >> b, env);

                        case Operators.Gt _ -> applyComparator(rest, (a, b) -> a > b, env);
                        case Operators.GtEq _ -> applyComparator(rest, (a, b) -> a >= b, env);
                        case Operators.Lt _ -> applyComparator(rest, (a, b) -> a < b, env);
                        case Operators.LtEq _ -> applyComparator(rest, (a, b) -> a <= b, env);
                        case Operators.Eq _ -> applyComparator(rest, (a, b) -> a == b, env);
                        case Operators.NotEq _ -> applyComparator(rest, (a, b) -> a != b, env);

                        default -> throw new EvalException("Unsupported operator: " + operators);
                    };
                    case Expr.Atom(Token.Symbol(String fnName)) -> {
                        Expr fn = env.get(fnName)
                                .getOrThrow(() -> new EvalException("attempted to call undefined function: " + fnName));
                        yield applyFn(fnName, fn, rest, env);
                    }
                    default -> {
                        Expr fn = eval(head, env);
                        List<Expr> args = rest.stream().map(e -> eval(e, env)).toList();
                        yield applyFn("[dynamic]", fn, args, env);
                    }
                };
            }
            default -> throw new EvalException("Unexpected value: " + expr);
        };
    }

    private static Expr evalEval(List<Expr> args, Env env) {
        if (args.size() == 1) {
            return eval(eval(args.getFirst(), env), env);
        } else {
            throw new EvalException("invalid usage of 'eval'");
        }
    }

    private static Expr evalEq(List<Expr> args, Env env) {
        if (args.size() == 2) {
            Expr left = eval(args.getFirst(), env);
            Expr right = eval(args.getLast(), env);
            return left.equals(right) ? BuiltinValue.TRUE : BuiltinValue.FALSE;
        } else {
            throw new EvalException("invalid usage of 'eq'");
        }
    }

    private static Expr evalProg(List<Expr> args, Env env) {
        Expr res = new Expr.Unit();
        for (Expr arg : args) {
            res = eval(arg, env);
        }
        return res;
    }

    private static Expr applyArithmetic(List<Expr> args, BinaryOperator<Integer> func, Env env) {
        Integer result = args.stream().map(e -> eval(e, env)).map(e -> switch (e) {
            case Expr.Atom(Token.Number(String n)) -> Integer.parseInt(n);
            default -> throw new EvalException("expected number value, got " + e);
        }).reduce(func).orElse(0);
        return new Expr.Atom(new Token.Number(result.toString()));
    }

    private static Expr applyComparator(List<Expr> args, BiPredicate<Integer, Integer> predicate, Env env) {
        if (args.size() == 2
                && eval(args.getFirst(), env) instanceof Expr.Atom(Token.Number(String left))
                && eval(args.getLast(), env) instanceof Expr.Atom(Token.Number(String right))) {
            return predicate.test(Integer.parseInt(left), Integer.parseInt(right)) ? BuiltinValue.TRUE : BuiltinValue.FALSE;
        } else {
            throw new EvalException("expected 2 args of number type");
        }
    }

    private static Expr evalFn(List<Expr> args, Env env) {
        if (args.size() == 2 && args.getFirst() instanceof Expr.Vec(List<Expr> params)) {
            List<String> paramsList = params.stream().map(param -> {
                if (param instanceof Expr.Atom(Token.Symbol(String s))) {
                    return s;
                } else {
                    throw new EvalException("expected symbol value, got " + param);
                }
            }).toList();
            return new Expr.FN(paramsList, args.getLast(), env);
        } else {
            throw new EvalException("invalid usage of 'fn'");
        }
    }

    private static Expr applyFn(String fnName, Expr fn, List<Expr> args, Env env) {
        return switch (fn) {
            case Expr.FN(List<String> params, Expr body, Env closure) -> {
                List<Expr> substitutedArgs = args.stream().map(e -> eval(e, env)).toList();
                Env newEnv = Env.extend(closure);
                if (params.size() == substitutedArgs.size()) {
                    for (int i = 0; i < params.size(); i++) {
                        newEnv.set(params.get(i), substitutedArgs.get(i));
                    }
                    yield eval(body, newEnv);
                } else {
                    throw new EvalException("called function "
                            + fnName
                            + " with "
                            + args.size()
                            + " argument(s), expected "
                            + params.size()
                            + " argument(s)");
                }
            }
            default -> throw new EvalException("expected function, but got " + fn);
        };
    }

    private static Expr evalCons(List<Expr> args, Env env) {
        if (args.size() == 2 && eval(args.getLast(), env) instanceof Expr.Vec(List<Expr> exprs)) {
            var res = new ArrayList<>(exprs);
            res.addFirst(eval(args.getFirst(), env));
            return new Expr.Vec(res);
        }
        throw new EvalException("invalid use of 'cons'");
    }

    private static Expr evalCar(List<Expr> args, Env env) {
        if (args.size() == 1 && eval(args.getFirst(), env) instanceof Expr.Vec(List<Expr> exprs)) {
            return exprs.isEmpty() ? BuiltinValue.NIL : exprs.getFirst();
        }
        throw new EvalException("cannot use 'car' on non-list value");
    }

    private static Expr evalCdr(List<Expr> args, Env env) {
        if (args.size() == 1 && eval(args.getFirst(), env) instanceof Expr.Vec(List<Expr> exprs)) {
            return exprs.isEmpty() ? BuiltinValue.NIL : new Expr.Vec(exprs.subList(1, exprs.size()));
        }
        throw new EvalException("cannot use 'cdr' on non-list value");
    }

    private static Expr evalQuote(List<Expr> args, Env env) {
        if (args.size() == 1) {
//            return new Expr.Quote(args.getFirst());
            return args.getFirst();
        } else {
            throw new EvalException("invalid usage of 'quote'");
        }
    }

    private static Expr evalList(List<Expr> args, Env env) {
        return new Expr.Vec(args.stream().map(e -> eval(e, env)).toList());
    }

    private static Expr evalDef(List<Expr> args, Env env) {
        if (args.size() == 2 && args.getFirst() instanceof Expr.Atom(Token.Symbol(String s))) {
            Expr value = eval(args.get(1), env);
            env.set(s, value);
            return new Expr.Unit();
        } else {
            throw new EvalException("invalid usage of 'def'");
        }
    }

    private static Expr evalIf(List<Expr> args, Env env) {
        if (args.size() == 3) {
            Expr test = args.get(0);
            Expr conseq = args.get(1);
            Expr alt = args.get(2);
            Expr expr = eval(test, env) == BuiltinValue.TRUE ? conseq : alt;
            return eval(expr, env);

        } else {
            throw new EvalException("invalid usage of 'if'");
        }
    }

    private static Expr evalCond(List<Expr> args, Env env) {
        for (Expr arg : args) {
            if (arg instanceof Expr.Vec(List<Expr> branch) && branch.size() == 2) {
                Expr condition = branch.getFirst();
                Expr then = branch.getLast();
                if (eval(condition, env) == BuiltinValue.TRUE || condition instanceof Expr.Atom(Token.Keyword(Keywords.ELSE _))) {
                    return eval(then, env);
                }
            } else {
                throw new EvalException("invalid usage of 'cond'");
            }
        }
        return new Expr.Unit();
    }
}
