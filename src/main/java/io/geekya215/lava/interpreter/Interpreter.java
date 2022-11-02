package io.geekya215.lava.interpreter;

import io.geekya215.lava.Env;
import io.geekya215.lava.adt.Expr;
import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.utils.ExprUtil;
import io.geekya215.lava.utils.Utils;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static io.geekya215.lava.utils.ExprUtil.matchList;
import static io.geekya215.lava.utils.ExprUtil.matchSymbol;

public class Interpreter {
    private static final Expr TRUE = new Expr.Symbol("#t");
    private static final Expr FALSE = new Expr.Symbol("#f");

    private static final Env builtinEnv = new Env();
    private static final Env runtimeEnv;

    static {
        builtinEnv.set("#t", TRUE);
        builtinEnv.set("#f", FALSE);

        builtinEnv.set(Operators.PLUS, new Expr.BuiltinLambda(
                "+",
                applyArithmetic((a, b) -> a + b),
                builtinEnv));

        builtinEnv.set(Operators.MINUS, new Expr.BuiltinLambda(
                "-",
                applyArithmetic((a, b) -> a - b)
                , builtinEnv));

        builtinEnv.set(Operators.MUL, new Expr.BuiltinLambda(
                "*",
                applyArithmetic((a, b) -> a * b)
                , builtinEnv));

        builtinEnv.set(Operators.DIV, new Expr.BuiltinLambda(
                "/",
                applyArithmetic((a, b) -> a / b)
                , builtinEnv));

        builtinEnv.set(Operators.LT, new Expr.BuiltinLambda(
                "<",
                applyComparator((a, b) -> a < b),
                builtinEnv));

        builtinEnv.set(Operators.GT, new Expr.BuiltinLambda(
                ">",
                applyComparator((a, b) -> a > b),
                builtinEnv));

        builtinEnv.set(Operators.LTEQ, new Expr.BuiltinLambda(
                "<=",
                applyComparator((a, b) -> a <= b),
                builtinEnv));

        builtinEnv.set(Operators.GTEQ, new Expr.BuiltinLambda(
                ">=",
                applyComparator((a, b) -> a >= b),
                builtinEnv));

        builtinEnv.set(Operators.EQ, new Expr.BuiltinLambda(
                "=",
                args -> {
                    if (args.size() == 2) {
                        return Objects.equals(args.get(0), args.get(1)) ? TRUE : FALSE;
                    }
                    throw new EvalException("expected 2 args");
                }, builtinEnv));

        builtinEnv.set(Operators.CAR, new Expr.BuiltinLambda("car", args -> {
            if (args.size() == 1 && args.get(0) instanceof Expr.List l) {
                var _l = l.value();
                return _l.isEmpty() ? l : _l.get(0);
            }
            throw new EvalException("cannot use car on non-list value");
        }, builtinEnv));

        builtinEnv.set(Operators.CDR, new Expr.BuiltinLambda("cdr", args -> {
            if (args.size() == 1 && args.get(0) instanceof Expr.List l) {
                var _l = l.value();
                return _l.isEmpty() ? l : new Expr.List(_l.subList(1, _l.size()));
            }
            throw new EvalException("cannot use cdr on non-list value");
        }, builtinEnv));

        builtinEnv.set(Operators.CONS, new Expr.BuiltinLambda("cons", args -> {
            if (args.size() == 2 && args.get(1) instanceof Expr.List l) {
                var _l = l.value();
                var res = new ArrayList<>(_l);
                res.add(0, args.get(0));
                return new Expr.List(res);
            }
            throw new EvalException("invalid use of 'cons'");
        }, builtinEnv));

        builtinEnv.set(Operators.LIST, new Expr.BuiltinLambda("list", Expr.List::new, builtinEnv));


        builtinEnv.set(Operators.BEGIN, new Expr.BuiltinLambda("begin", args -> {
            if (args.isEmpty()) {
                throw new EvalException("invalid use of 'begin'");
            } else {
                return args.get(args.size() - 1);
            }
        }, builtinEnv));

        runtimeEnv = Env.extend(builtinEnv);
    }

    public static Expr eval(Expr expr) throws EvalException {
        return eval(expr, runtimeEnv);
    }

    private static Expr eval(Expr expr, Env env) throws EvalException {
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
            case Expr.List list -> {
                var _l = list.value();
                if (_l.isEmpty()) {
                    yield new Expr.List(new ArrayList<>());
                }
                var head = _l.get(0);
                var size = _l.size();

                if (matchSymbol(head, Keywords.IF)) {
                    if (size == 4) {
                        var predicate = _l.get(1);
                        var conseq = _l.get(2);
                        var alt = _l.get(3);
                        yield ExprUtil.testExpr(eval(predicate, env)) ? eval(conseq, env) : eval(alt, env);
                    } else {
                        throw new EvalException("invalid usage of 'if'");
                    }
                }

                if (matchSymbol(head, Keywords.COND)) {
                    var conds = _l.subList(1, size);
                    for (var cond : conds) {
                        if (matchList(cond, 2)) {
                            var test = ((Expr.List) cond).value().get(0);
                            var action = ((Expr.List) cond).value().get(1);
                            if (matchSymbol(test, "else")) {
                                yield eval(action, env);
                            }
                            if (ExprUtil.testExpr(eval(test, env))) {
                                yield eval(action, env);
                            }
                        } else {
                            throw new EvalException("invalid usage of 'cond'");
                        }
                    }
                    yield FALSE;
                }

                if (matchSymbol(head, Keywords.DEFINE)) {
                    if (size == 3 && _l.get(1) instanceof Expr.Symbol s) {
                        var v = eval(_l.get(2), env);
                        env.set(s.value(), v);
                        yield v;
                    } else {
                        throw new EvalException("invalid usage of 'define'");
                    }
                }

                if (matchSymbol(head, Keywords.LAMBDA)) {
                    if (size == 3 && _l.get(1) instanceof Expr.List __l) {
                        var params = __l.value().stream().map(ExprUtil::unwrapSymbol).toList();
                        var body = _l.get(2);
                        yield new Expr.Lambda(params, body, Env.extend(env));
                    } else {
                        throw new EvalException("invalid usage of 'lambda'");
                    }
                }

                if (matchSymbol(head, Keywords.QUOTE)) {
                    if (size == 2) {
                        yield _l.get(1);
                    } else {
                        throw new EvalException("invalid usage of 'quote'");
                    }
                }

                if (matchSymbol(head, Keywords.DEFMACRO)) {
                    if (size == 4
                            && _l.get(1) instanceof Expr.Symbol s
                            && _l.get(2) instanceof Expr.List params
                            && params.value().stream().allMatch(p -> p instanceof Expr.Symbol)) {
                        var body = _l.get(3);
                        var macro = new Expr.Macro(params.value(), body, env);
                        env.set(s.value(), macro);
                        yield macro;
                    } else {
                        throw new EvalException("invalid usage of 'defmacro'");
                    }
                }

                if (matchSymbol(head, Keywords.ATOM)) {
                    if (size == 2) {
                        var arg = eval(_l.get(1), env);
                        if (arg instanceof Expr.Number
                                || arg instanceof Expr.Symbol
                                || matchList(arg, 0)) {
                            yield TRUE;
                        } else {
                            yield FALSE;
                        }
                    } else {
                        throw new EvalException("invalid usage of 'atom?'");
                    }
                }

                // lazy eval strategy
                if (head instanceof Expr.Symbol s) {
                    var name = s.value();
                    var callable = env.get(name);
                    if (callable.isPresent()) {
                        yield application(callable.get(), name, _l.subList(1, size), env);
                    } else {
                        throw new EvalException("attempted to call undefined function: " + name);
                    }
                }

                var callable = eval(head, env);
                var args = _l.subList(1, size).stream().map(e -> eval(e, env)).toList();
                yield application(callable, "[dynamic]", args, env);
            }
            default -> throw new EvalException("unknown list form");
        };
    }

    private static Expr application(Expr expr, String name, List<Expr> args, Env env) throws EvalException {
        return switch (expr) {
            case Expr.Lambda lambda -> {
                var params = lambda.params();
                var substitutedArgs = args.stream().map(e -> eval(e, env)).toList();
                var newEnv = Env.extend(lambda.env());
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
            case Expr.BuiltinLambda builtinLambda -> {
                var func = builtinLambda.func();
                yield func.apply(args.stream().map(e -> eval(e, env)).toList());
            }
            case Expr.Macro macro -> {
                var params = macro.params();
                var body = macro.body();
                var map = new HashMap<Expr, Expr>();
                var res = FALSE;

                if (params.size() == args.size()) {
                    for (int i = 0; i < params.size(); i++) {
                        map.put(params.get(i), args.get(i));
                    }
                    if (body instanceof Expr.Symbol && map.containsKey(body)) {
                        res = map.get(body);
                    } else if (body instanceof Expr.List l) {
                        var expand = Utils.deepCopyExprList(l.value());
                        expandMacro(map, expand);
                        res = new Expr.List(expand);
                    }
                    yield eval(res, macro.env());
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
            default -> throw new EvalException("expected function, got " + expr);
        };
    }

    private static void expandMacro(Map<Expr, Expr> map, List<Expr> body) {
        for (int i = 0; i < body.size(); i++) {
            var ele = body.get(i);
            if (ele instanceof Expr.Symbol && map.containsKey(ele)) {
                body.set(i, map.get(ele));
            } else if (ele instanceof Expr.List list) {
                expandMacro(map, list.value());
            }
        }
    }

    private static Function<List<Expr>, Expr> applyArithmetic(BinaryOperator<Integer> func) {
        return args -> new Expr.Number(args.stream().map(ExprUtil::unwrapNumber).reduce(func).orElse(0));
    }

    private static Function<List<Expr>, Expr> applyComparator(BiPredicate<Integer, Integer> predicate) {
        return args -> {
            if (args.size() == 2 && args.get(0) instanceof Expr.Number left && args.get(1) instanceof Expr.Number right) {
                return predicate.test(left.value(), right.value()) ? TRUE : FALSE;
            } else {
                throw new EvalException("expected 2 args of number type");
            }
        };
    }
}
