package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class Interpreter {
    public static Expr eval(Expr expr, Env env) throws EvalException {
        return switch (expr) {
            case Expr.Quote quote -> quote.expr();
            case Expr.Number number -> number;
            case Expr.Symbol symbol -> {
                var name = symbol.value();
                var var = env.get(name);
                if (var.isPresent()) {
                    yield var.get();
                } else {
                    throw new EvalException("attempted to access undefined variable: " + name);
                }
            }
            case Expr.List list -> {
                var _list = list.value();
                if (_list.isEmpty()) {
                    yield new Expr.List(new ArrayList<>());
                }

                var head = _list.get(0);
                var size = _list.size();

                if (checkKeyword(head, "if")) {
                    if (size == 4) {
                        var predicate = _list.get(1);
                        var conseq = _list.get(2);
                        var alt = _list.get(3);
                        yield predicateExpr(eval(predicate, env)) ? eval(conseq, env) : eval(alt, env);
                    } else {
                        throw new EvalException("invalid usage of 'if'");
                    }
                }

                // Todo
                // can we use stream instead of for loop?
                if (checkKeyword(head, "cond")) {
                    var res = _list.subList(1, _list.size());
                    for (var cond : res) {
                        if (cond instanceof Expr.List _l && _l.value().size() == 2) {
                            var test = _l.value().get(0);
                            var action = _l.value().get(1);
                            if (test instanceof Expr.Symbol _test && Objects.equals(_test.value(), "else")) {
                                yield eval(action, env);
                            }
                            if (predicateExpr(eval(test, env))) {
                                yield eval(action, env);
                            }
                        } else {
                            throw new EvalException("invalid usage of 'cond'");
                        }
                    }
                    yield Constants.FALSE;
                }

                if (checkKeyword(head, "define")) {
                    if (size == 3 && _list.get(1) instanceof Expr.Symbol _sym) {
                        env.set(_sym.value(), eval(_list.get(2), env));
                        yield Constants.FALSE;
                    } else {
                        throw new EvalException("invalid usage of 'define'");
                    }
                }

                if (checkKeyword(head, "lambda")) {
                    if (size == 3 && _list.get(1) instanceof Expr.List params) {
                        var _params = params.value().stream().map(Interpreter::unwrapSymbol).toList();
                        var body = _list.get(2);
                        yield new Expr.Lambda(_params, body, Env.extend(env));
                    } else {
                        throw new EvalException("invalid usage of 'lambda'");
                    }
                }

                if (checkKeyword(head, "quote")) {
                    if (size == 2) {
                        yield _list.get(1);
                    } else {
                        throw new EvalException("invalid usage of 'quote'");
                    }
                }

                if (checkKeyword(head, "defmacro")) {
                    if (size == 4
                        && _list.get(1) instanceof Expr.Symbol _sym
                        && _list.get(2) instanceof Expr.List params
                        && params.value().stream().allMatch(p -> p instanceof Expr.Symbol)) {
                        var body = _list.get(3);
                        var macro = new Expr.Macro(params.value(), body, env);
                        env.set(_sym.value(), macro);
                        yield Constants.FALSE;
                    } else {
                        throw new EvalException("invalid usage of 'defmacro'");
                    }
                }

                if (checkKeyword(head, "atom?")) {
                    if (size == 2) {
                        var arg = eval(_list.get(1), env);
                        if (arg instanceof Expr.Number
                            || arg instanceof Expr.Symbol
                            || (arg instanceof Expr.List _l && _l.value().size() == 0)) {
                            yield Constants.TRUE;
                        } else {
                            yield Constants.FALSE;
                        }
                    } else {
                        throw new EvalException("invalid usage of 'atom?'");
                    }
                }

                // lazy eval strategy
                if (head instanceof Expr.Symbol sym) {
                    var args = _list.subList(1, size);
                    yield callByName(sym.value(), args, env);
                }

                var lambda = eval(head, env);
                var args = _list.subList(1, size).stream().map(e -> eval(e, env)).toList();
                yield application(lambda, "[dynamic]", args, env);
            }
            default -> throw new EvalException("unknown list form");
        };
    }

    private static Expr callByName(String name, List<Expr> args, Env env) throws EvalException {
        var lambda = env.get(name);
        if (lambda.isPresent()) {
            return application(lambda.get(), name, args, env);
        } else {
            throw new EvalException("attempted to call undefined function: " + name);
        }
    }

    private static Expr application(Expr expr, String name, List<Expr> args, Env env) throws EvalException {
        return switch (expr) {
            case Expr.Lambda lambda -> {
                var params = lambda.params();
                var _args = args.stream().map(e -> eval(e, env)).toList();
                var body = lambda.body();
                var newEnv = Env.extend(lambda.env());
                if (params.size() == args.size()) {
                    for (int i = 0; i < params.size(); i++) {
                        newEnv.set(params.get(i), _args.get(i));
                    }
                    yield eval(body, newEnv);
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
                var res = Constants.FALSE;
                var map = new HashMap<Expr, Expr>();

                if (params.size() == args.size()) {
                    for (int i = 0; i < params.size(); i++) {
                        map.put(params.get(i), args.get(i));
                    }
                    if (body instanceof Expr.Symbol && map.containsKey(body)) {
                        res = map.get(body);
                    } else if (body instanceof Expr.List list) {
                        var newList = deepCopy(list.value());
                        expandMacro(map, newList);
                        res = new Expr.List(newList);
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

    private static List<Expr> deepCopy(List<Expr> source) {
        var res = new ArrayList<Expr>();
        for (Expr cur : source) {
            if (cur instanceof Expr.List list) {
                res.add(new Expr.List(deepCopy(list.value())));
            } else {
                res.add(cur);
            }
        }
        return res;
    }

    private static Boolean checkKeyword(Expr e, String keyword) {
        return e instanceof Expr.Symbol s && Objects.equals(s.value(), keyword);
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

    private static Boolean predicateExpr(Expr expr) {
        return !switch (expr) {
            case Expr.Symbol symbol && Objects.equals(symbol.value(), "#f") -> true;
            case Expr.List list && list.value().isEmpty() -> true;
            default -> false;
        };
    }

    private static Expr getBooleanSymbol(Boolean test) {
        return test ? Constants.TRUE : Constants.FALSE;
    }

    private static String unwrapSymbol(Expr expr) throws EvalException {
        return switch (expr) {
            case Expr.Symbol symbol -> symbol.value();
            default -> throw new EvalException("expected symbol value, got " + expr);
        };
    }

    private static Integer unwrapNumber(Expr expr) throws EvalException {
        return switch (expr) {
            case Expr.Number number -> number.value();
            default -> throw new EvalException("expected number value, got " + expr);
        };
    }

    private static Function<List<Expr>, Expr> applyArithmetic(BinaryOperator<Integer> func) {
        return args -> new Expr.Number(
            args.stream().map(Interpreter::unwrapNumber).reduce(func).orElse(0)
        );
    }

    private static Function<List<Expr>, Expr> applyComparator(BiPredicate<Integer, Integer> predicate) {
        return args -> {
            if (args.size() == 2 && args.get(0) instanceof Expr.Number left && args.get(1) instanceof Expr.Number right) {
                return getBooleanSymbol(predicate.test(left.value(), right.value()));
            } else {
                throw new EvalException("expected 2 args of number type");
            }
        };
    }

    public static Env initialStandardEnv() {
        var standardEnv = new Env();

        standardEnv.set("#t", Constants.TRUE);
        standardEnv.set("#f", Constants.FALSE);

        standardEnv.set("+", new Expr.BuiltinLambda(
            "+",
            applyArithmetic((a, b) -> a + b),
            standardEnv));

        standardEnv.set("-", new Expr.BuiltinLambda(
            "-",
            applyArithmetic((a, b) -> a - b)
            , standardEnv));

        standardEnv.set("*", new Expr.BuiltinLambda(
            "*",
            applyArithmetic((a, b) -> a * b)
            , standardEnv));

        standardEnv.set("/", new Expr.BuiltinLambda(
            "/",
            applyArithmetic((a, b) -> a / b)
            , standardEnv));


        standardEnv.set("<", new Expr.BuiltinLambda(
            "<",
            applyComparator((a, b) -> a < b)
            , standardEnv));
        standardEnv.set("<=", new Expr.BuiltinLambda(
            "<=",
            applyComparator((a, b) -> a <= b)
            , standardEnv));
        standardEnv.set(">", new Expr.BuiltinLambda(
            ">",
            applyComparator((a, b) -> a > b)
            , standardEnv));
        standardEnv.set(">=", new Expr.BuiltinLambda(
            ">=",
            applyComparator((a, b) -> a >= b)
            , standardEnv));
        standardEnv.set("=", new Expr.BuiltinLambda("=", args -> {
            if (args.size() == 2) {
                return getBooleanSymbol(Objects.equals(args.get(0), args.get(1)));
            }
            throw new EvalException("expected 2 args");
        }, standardEnv));


        standardEnv.set("car", new Expr.BuiltinLambda("car", args -> {
            if (args.size() == 1 && args.get(0) instanceof Expr.List list) {
                var _list = list.value();
                return _list.isEmpty() ? list : _list.get(0);
            }
            throw new EvalException("cannot use car on non-list value");
        }, standardEnv));
        standardEnv.set("cdr", new Expr.BuiltinLambda("cdr", args -> {
            if (args.size() == 1 && args.get(0) instanceof Expr.List list) {
                var _list = list.value();
                return _list.isEmpty() ? list : new Expr.List(_list.subList(1, _list.size()));
            }
            throw new EvalException("cannot use cdr on non-list value");
        }, standardEnv));
        standardEnv.set("cons", new Expr.BuiltinLambda("cons", args -> {
            if (args.size() == 2 && args.get(1) instanceof Expr.List list) {
                var _list = list.value();
                var res = new ArrayList<>(_list);
                res.add(0, args.get(0));
                return new Expr.List(res);
            }
            throw new EvalException("invalid use of 'cons'");
        }, standardEnv));
        standardEnv.set("list", new Expr.BuiltinLambda("list", Expr.List::new, standardEnv));


        standardEnv.set("begin", new Expr.BuiltinLambda("begin", args -> {
            if (args.isEmpty()) {
                throw new EvalException("invalid use of 'begin'");
            } else {
                return args.get(args.size() - 1);
            }
        }, standardEnv));

        return standardEnv;
    }
}
