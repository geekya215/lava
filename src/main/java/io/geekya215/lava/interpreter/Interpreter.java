package io.geekya215.lava.interpreter;


import io.geekya215.lava.Env;
import io.geekya215.lava.Expr;
import io.geekya215.lava.exception.EvalException;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class Interpreter {
    private static final Env nativeEnv = new Env(Optional.empty(), new HashMap<>());
    private static final Env runtimeEnv;

    static {
        nativeEnv.set("#t", BuiltinValue.TRUE);
        nativeEnv.set("#f", BuiltinValue.FALSE);

        nativeEnv.set(NativeFunctions.PLUS, new Expr.NativeFunc("+", applyArithmetic((a, b) -> a + b), nativeEnv));
        nativeEnv.set(NativeFunctions.MINUS, new Expr.NativeFunc("-", applyArithmetic((a, b) -> a - b), nativeEnv));
        nativeEnv.set(NativeFunctions.MUL, new Expr.NativeFunc("*", applyArithmetic((a, b) -> a * b), nativeEnv));
        nativeEnv.set(NativeFunctions.DIV, new Expr.NativeFunc("/", applyArithmetic((a, b) -> a / b), nativeEnv));

        nativeEnv.set(NativeFunctions.GT, new Expr.NativeFunc(">", applyComparator((a, b) -> a > b), nativeEnv));
        nativeEnv.set(NativeFunctions.GTEQ, new Expr.NativeFunc(">=", applyComparator((a, b) -> a >= b), nativeEnv));
        nativeEnv.set(NativeFunctions.LT, new Expr.NativeFunc("<", applyComparator((a, b) -> a < b), nativeEnv));
        nativeEnv.set(NativeFunctions.LTEQ, new Expr.NativeFunc("<=", applyComparator((a, b) -> a <= b), nativeEnv));
        nativeEnv.set(NativeFunctions.EQ, new Expr.NativeFunc("=", args -> {
            if (args.size() == 2) {
                return Objects.equals(args.get(0), args.get(1)) ? BuiltinValue.TRUE : BuiltinValue.FALSE;
            }
            throw new EvalException("expected 2 args for =");
        }, nativeEnv));

        nativeEnv.set(NativeFunctions.CAR, new Expr.NativeFunc("car", args -> {
            if (args.size() == 1 && args.get(0) instanceof Expr.List l) {
                var _l = l.value();
                return _l.isEmpty() ? l : _l.get(0);
            }
            throw new EvalException("cannot use car on non-list value");
        }, nativeEnv));

        nativeEnv.set(NativeFunctions.CDR, new Expr.NativeFunc("cdr", args -> {
            if (args.size() == 1 && args.get(0) instanceof Expr.List l) {
                var _l = l.value();
                return _l.isEmpty() ? l : new Expr.List(_l.subList(1, _l.size()));
            }
            throw new EvalException("cannot use cdr on non-list value");
        }, nativeEnv));

        nativeEnv.set(NativeFunctions.CONS, new Expr.NativeFunc("cons", args -> {
            if (args.size() == 2 && args.get(1) instanceof Expr.List l) {
                var _l = l.value();
                var res = new ArrayList<>(_l);
                res.add(0, args.get(0));
                return new Expr.List(res);
            }
            throw new EvalException("invalid use of 'cons'");
        }, nativeEnv));

        nativeEnv.set(NativeFunctions.LIST, new Expr.NativeFunc("list", Expr.List::new, nativeEnv));

        nativeEnv.set(NativeFunctions.BEGIN, new Expr.NativeFunc("begin", args -> {
            if (args.isEmpty()) {
                throw new EvalException("invalid use of 'begin'");
            } else {
                return args.get(args.size() - 1);
            }
        }, nativeEnv));

        runtimeEnv = Env.extend(nativeEnv);
    }

    public static Expr eval(Expr expr) {
        return eval(expr, runtimeEnv);
    }

    public static Expr eval(Expr expr, Env env) {
        return switch (expr) {
            case Expr.Quote quote -> quote.expr();
            case Expr.Number number -> number;
            case Expr.Symbol symbol -> {
                var name = symbol.value();
                yield env
                    .get(name)
                    .orElseThrow(() -> {
                        throw new EvalException("attempted to access undefined variable: " + name);
                    });
            }
            case Expr.List l -> {
                var list = l.value();
                // Todo
                // change to immutable list
                if (list.isEmpty()) {
                    yield new Expr.List(new ArrayList<>());
                }

                var head = list.get(0);
                var size = list.size();
                var tail = list.subList(1, size);

                // eval keyword
                if (head instanceof Expr.Symbol sym) {
                    var symbolName = sym.value();

                    if (Objects.equals(symbolName, Keywords.IF)) {
                        if (size == 4) {
                            var condition = tail.get(0);
                            var then = tail.get(1);
                            var alt = tail.get(2);
                            yield eval(condition, env) == BuiltinValue.TRUE ? eval(then, env) : eval(alt, env);
                        } else {
                            throw new EvalException("invalid usage of 'if'");
                        }
                    }

                    if (Objects.equals(symbolName, Keywords.COND)) {
                        for (var branch : tail) {
                            if (branch instanceof Expr.List _l && _l.value().size() == 2) {
                                var condition = _l.value().get(0);
                                var then = _l.value().get(1);
                                if (condition instanceof Expr.Symbol s && Objects.equals(s.value(), Keywords.ELSE)) {
                                    yield eval(then, env);
                                }

                                if (eval(condition, env) == BuiltinValue.TRUE) {
                                    yield eval(then, env);
                                }
                            } else {
                                throw new EvalException("invalid usage of 'cond'");
                            }
                        }
                        yield BuiltinValue.FALSE;
                    }

                    if (Objects.equals(symbolName, Keywords.MATCH)) {
                        if (size == 3 && tail.get(1) instanceof Expr.List m) {
                            var e = eval(tail.get(0), env);
                            var patterns = m.value();
                            for (var pattern : patterns) {
                                if (pattern instanceof Expr.List _p && _p.value().size() == 2) {
                                    var p = _p.value();
                                    if (Objects.equals(p.get(0), e)
                                        || (p.get(0) instanceof Expr.Symbol d && Objects.equals(d.value(), Keywords.UNDERSCORE))) {
                                        yield eval(p.get(1), env);
                                    }
                                } else {
                                    throw new EvalException("invalid usage of 'match'");
                                }
                            }
                        } else {
                            throw new EvalException("invalid usage of 'match'");
                        }
                    }

                    if (Objects.equals(symbolName, Keywords.DEFINE)) {
                        if (size == 3 && tail.get(0) instanceof Expr.Symbol s) {
                            var value = eval(tail.get(1), env);
                            env.set(s.value(), value);
                            yield value;
                        } else {
                            throw new EvalException("invalid usage of 'define'");
                        }
                    }

                    if (Objects.equals(symbolName, Keywords.QUOTE)) {
                        if (size == 2) {
                            yield tail.get(0);
                        } else {
                            throw new EvalException("invalid usage of 'quote'");
                        }
                    }

                    if (Objects.equals(symbolName, Keywords.LAMBDA)) {
                        if (size == 3 && tail.get(0) instanceof Expr.List _params) {
                            var params = _params.value().stream().map(p -> {
                                if (p instanceof Expr.Symbol _p) {
                                    return _p.value();
                                } else {
                                    throw new EvalException("expected symbol value, got " + p);
                                }
                            }).toList();
                            var body = tail.get(1);
                            yield new Expr.Lambda(params, body, env);
                        } else {
                            throw new EvalException("invalid usage of 'lambda'");
                        }
                    }

                    if (Objects.equals(symbolName, Keywords.MACRO)) {
                        if (size == 4
                            && tail.get(0) instanceof Expr.Symbol s
                            && tail.get(1) instanceof Expr.List _params
                            && _params.value().stream().allMatch(p -> p instanceof Expr.Symbol)) {
                            var params = _params.value().stream().map(p -> {
                                if (p instanceof Expr.Symbol _p) {
                                    return _p.value();
                                } else {
                                    throw new EvalException("expected symbol value, got " + p);
                                }
                            }).toList();
                            var body = tail.get(2);
                            var macro = new Expr.Macro(params, body, env);
                            env.set(s.value(), macro);
                            yield macro;
                        } else {
                            throw new EvalException("invalid usage of 'macro'");
                        }
                    }

                    // function call by name
                    yield env.get(symbolName)
                        .map(f -> application(f, symbolName, tail, env))
                        .orElseThrow(() -> new EvalException("attempted to call undefined function: " + symbolName));
                }

                // immediately call function or macro expand
                var callable = eval(head, env);
                var args = tail.stream().map(e -> eval(e, env)).toList();
                yield application(callable, "[dynamic]", args, env);
            }
            default -> throw new EvalException("unknown list form");
        };
    }

    private static Expr application(Expr expr, String name, List<Expr> args, Env env) {
        return switch (expr) {
            case Expr.Lambda lambda -> {
                var params = lambda.params();
                var substitutedArgs = args.stream().map(e -> eval(e, env)).toList();
                var newEnv = Env.extend(lambda.closure());
                if (params.size() == substitutedArgs.size()) {
                    for (int i = 0; i < params.size(); i++) {
                        newEnv.set(params.get(i), substitutedArgs.get(i));
                    }
                    yield eval(lambda.body(), newEnv);
                } else {
                    throw new EvalException("called function "
                        + name
                        + " with "
                        + args.size()
                        + " argument(s), expected "
                        + params.size()
                        + " argument(s)");
                }
            }
            case Expr.Macro macro -> {
                var params = macro.params();
                var substitutedArgs = args.stream().map(e -> eval(e, env)).toList();
                var newEnv = Env.extend(macro.closure());
                if (params.size() == substitutedArgs.size()) {
                    for (int i = 0; i < params.size(); i++) {
                        newEnv.set(params.get(i), substitutedArgs.get(i));
                    }
                    var expand = eval(macro.body(), newEnv);
                    yield eval(expand, env);
                } else {
                    throw new EvalException("called macro "
                        + name
                        + " with "
                        + args.size()
                        + " argument(s), expected "
                        + params.size()
                        + " argument(s)");
                }
            }
            case Expr.NativeFunc nativeFunc -> {
                var func = nativeFunc.func();
                yield func.apply(args.stream().map(e -> eval(e, env)).toList());
            }
            default -> throw new EvalException("expected function or macro, got " + expr);
        };
    }

    private static Function<List<Expr>, Expr> applyArithmetic(BinaryOperator<Integer> func) {
        return args -> new Expr.Number(args.stream().map(e -> switch (e) {
            case Expr.Number n -> n.value();
            default -> throw new EvalException("expected number value, got " + e);
        }).reduce(func).orElse(0));
    }

    private static Function<List<Expr>, Expr> applyComparator(BiPredicate<Integer, Integer> predicate) {
        return args -> {
            if (args.size() == 2 && args.get(0) instanceof Expr.Number left && args.get(1) instanceof Expr.Number right) {
                return predicate.test(left.value(), right.value()) ? BuiltinValue.TRUE : BuiltinValue.FALSE;
            } else {
                throw new EvalException("expected 2 args of number type");
            }
        };
    }

    private static final class BuiltinValue {
        private static final Expr TRUE = new Expr.Symbol("#t");
        private static final Expr FALSE = new Expr.Symbol("#f");
    }
}
