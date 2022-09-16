package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

                if (head instanceof Expr.Symbol sym && Objects.equals(sym.value(), "if")) {
                    if (size == 4) {
                        var predicate = _list.get(1);
                        var conseq = _list.get(2);
                        var alt = _list.get(3);
                        yield predicateExpr(eval(predicate, env)) ? eval(conseq, env) : eval(alt, env);
                    } else {
                        throw new EvalException("invalid usage of 'if'");
                    }
                }

                if (head instanceof Expr.Symbol sym && Objects.equals(sym.value(), "define")) {
                    if (size == 3 && _list.get(1) instanceof Expr.Symbol _sym) {
                        env.set(_sym.value(), eval(_list.get(2), env));
                        yield new Expr.Symbol("#f");
                    } else {
                        throw new EvalException("invalid usage of 'define'");
                    }
                }

                if (head instanceof Expr.Symbol sym && Objects.equals(sym.value(), "lambda")) {
                    if (size == 3 && _list.get(1) instanceof Expr.List params) {
                        var _params = params.value().stream().map(Interpreter::unwrapSymbol).toList();
                        var body = _list.get(2);
                        yield new Expr.Lambda(_params, body, Env.extend(env));
                    } else {
                        throw new EvalException("invalid usage of 'lambda'");
                    }
                }

                if (head instanceof Expr.Symbol sym && Objects.equals(sym.value(), "quote")) {
                    if (size == 2) {
                        yield _list.get(1);
                    } else {
                        throw new EvalException("invalid usage of 'quote'");
                    }
                }

                if (head instanceof Expr.Symbol sym) {
                    var args = _list.subList(1, size).stream().map(e -> eval(e, env)).toList();
                    yield callByName(sym.value(), args, env);
                }

                var lambda = eval(head, env);
                var args = _list.subList(1, size).stream().map(e -> eval(e, env)).toList();
                yield application(lambda, "[dynamic]", args);
            }
            default -> throw new EvalException("unknown list form");
        };
    }

    private static Expr callByName(String name, List<Expr> args, Env env) throws EvalException {
        var lambda = env.get(name);
        if (lambda.isPresent()) {
            return application(lambda.get(), name, args);
        } else {
            throw new EvalException("attempted to call undefined function: " + name);
        }
    }

    private static Expr application(Expr expr, String name, List<Expr> args) throws EvalException {
        return switch (expr) {
            case Expr.Lambda lambda -> {
                var params = lambda.params();
                var body = lambda.body();
                var newEnv = Env.extend(lambda.env());
                if (params.size() == args.size()) {
                    for (int i = 0; i < params.size(); i++) {
                        newEnv.set(params.get(i), args.get(i));
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
                yield func.apply(args);
            }
            default -> throw new EvalException("expected function, got " + expr);
        };
    }

    private static Boolean predicateExpr(Expr expr) {
        return !switch (expr) {
            case Expr.Symbol symbol && Objects.equals(symbol.value(), "#f") -> true;
            case Expr.List list && list.value().isEmpty() -> true;
            default -> false;
        };
    }

    private static Expr getBooleanSymbol(Boolean test) {
        return test ? new Expr.Symbol("#t") : new Expr.Symbol("#f");
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

        standardEnv.set("#t", new Expr.Symbol("#t"));
        standardEnv.set("#f", new Expr.Symbol("#f"));

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
            if (args.size() == 2 && args.get(0) instanceof Expr.Number left && args.get(1) instanceof Expr.Number right) {
                return getBooleanSymbol(Objects.equals(left.value(), right.value()));
            }
            if (args.size() == 2 && args.get(0) instanceof Expr.Symbol left && args.get(1) instanceof Expr.Symbol right) {
                return getBooleanSymbol(Objects.equals(left.value(), right.value()));
            }
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
                _list.add(0, args.get(0));
                return new Expr.List(_list);
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
