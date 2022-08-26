import exception.*;

import java.util.ArrayList;
import java.util.List;

public class Eval {
    private static Type evalUnaryOperator(List<Type> list, Env env) throws EvalException {
        if (list.size() != 2) {
            throw new UnaryOperatorException("invalid number of arguments for unary operator");
        }

        var op = list.get(0);
        var operand = evalType(list.get(1), env);

        return switch (op) {
            case Type.UnaryOperator uop -> switch (uop.val()) {
                case "neg" -> {
                    if (operand instanceof Type.Integer i) {
                        yield new Type.Integer(-i.val());
                    } else {
                        throw new UnaryOperatorException("neg", Type.Integer.class, operand.getClass());
                    }
                }
                case "not" -> {
                    if (operand instanceof Type.Bool b) {
                        yield new Type.Bool(!b.val());
                    } else {
                        throw new UnaryOperatorException("not", Type.Bool.class, operand.getClass());
                    }
                }
                default -> throw new UnaryOperatorException("invalid unary operator: " + uop);
            };
            default -> throw new UnaryOperatorException("unary operator must be a symbol");
        };
    }

    public static Type evalBinaryOperator(List<Type> list, Env env) throws EvalException {
        if (list.size() != 3) {
            throw new BinaryOperatorException("invalid number of arguments for binary operator");
        }
        var op = list.get(0);
        var left = evalType(list.get(1), env);
        var right = evalType(list.get(2), env);

        return switch (op) {
            case Type.BinaryOperator bop -> switch (bop.val()) {
                case "+" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Integer(l.val() + r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "+",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case "-" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Integer(l.val() - r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "-",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case "*" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Integer(l.val() * r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "*",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case "/" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Integer(l.val() / r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "/",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case "<" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Bool(l.val() < r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "<",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case ">" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Bool(l.val() > r.val());
                    } else {
                        throw new BinaryOperatorException(
                            ">",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case "<=" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Bool(l.val() <= r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "<=",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case ">=" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Bool(l.val() >= r.val());
                    } else {
                        throw new BinaryOperatorException(
                            ">=",
                            Type.Integer.class,
                            Type.Integer.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case "=" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Bool(l.val() == r.val());
                    } else if (left instanceof Type.Bool l && right instanceof Type.Bool r) {
                        yield new Type.Bool(l.val() == r.val());
                    } else {
                        String t = "invalid types for = operator:\n\texpected (= `%s` `%s`) or (= `%s` `%s`), found (= `%s` `%s`)";
                        throw new BinaryOperatorException(String.format(t,
                            Type.Integer.class.getSimpleName(),
                            Type.Integer.class.getSimpleName(),
                            Type.Bool.class.getSimpleName(),
                            Type.Bool.class.getSimpleName(),
                            left.getClass().getSimpleName(),
                            right.getClass().getSimpleName()));
                    }
                }
                case "!=" -> {
                    if (left instanceof Type.Integer l && right instanceof Type.Integer r) {
                        yield new Type.Bool(l.val() != r.val());
                    } else if (left instanceof Type.Bool l && right instanceof Type.Bool r) {
                        yield new Type.Bool(l.val() != r.val());
                    } else {
                        String t = "invalid types for != operator:\n\texpected (!= `%s` `%s`) or (!= `%s` `%s`), found (!= `%s` `%s`)";
                        throw new BinaryOperatorException(String.format(t,
                            Type.Integer.class.getSimpleName(),
                            Type.Integer.class.getSimpleName(),
                            Type.Bool.class.getSimpleName(),
                            Type.Bool.class.getSimpleName(),
                            left.getClass().getSimpleName(),
                            right.getClass().getSimpleName()));
                    }
                }
                case "and" -> {
                    if (left instanceof Type.Bool l && right instanceof Type.Bool r) {
                        yield new Type.Bool(l.val() && r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "and",
                            Type.Bool.class,
                            Type.Bool.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                case "or" -> {
                    if (left instanceof Type.Bool l && right instanceof Type.Bool r) {
                        yield new Type.Bool(l.val() || r.val());
                    } else {
                        throw new BinaryOperatorException(
                            "or",
                            Type.Bool.class,
                            Type.Bool.class,
                            left.getClass(),
                            right.getClass());
                    }
                }
                default -> throw new BinaryOperatorException("Invalid binary operator: " + bop);
            };
            default -> throw new BinaryOperatorException("binary operator must be a symbol");
        };
    }

    public static Type evalDefine(List<Type> list, Env env) throws EvalException {
        if (list.size() != 3) {
            throw new EvalException("Invalid number of arguments for define");
        }

        var identifier = switch (list.get(1)) {
            case Type.Symbol sym -> sym.val();
            default -> throw new EvalException("Invalid define");
        };

        var val = evalType(list.get(2), env);
        env.set(identifier, val);

        return new Type.Unit();
    }

    public static Type evalIf(List<Type> list, Env env) throws EvalException {
        if (list.size() != 4) {
            throw new EvalException("Invalid number of arguments for if statement");
        }

        var cond = switch (evalType(list.get(1), env)) {
            case Type.Bool b -> b.val();
            default -> throw new EvalException("Condition must be a boolean");
        };

        if (cond) {
            return evalType(list.get(2), env);
        } else {
            return evalType(list.get(3), env);
        }
    }

    public static Type evalFunctionDefinition(List<Type> list) throws EvalException {
        var params = switch (list.get(1)) {
            case Type.List l -> {
                var p = new ArrayList<String>();
                for (var param : l.val()) {
                    switch (param) {
                        case Type.Symbol sym -> p.add(sym.val());
                        default -> throw new EvalException("Invalid lambda parameter");
                    }
                }
                yield p;
            }
            default -> throw new EvalException("Invalid lambda");
        };

        var body = switch (list.get(2)) {
            case Type.List l -> l.val();
            default -> throw new EvalException("Invalid lambda");
        };

        return new Type.Lambda(params, body);
    }

    public static Type evalFunctionCall(String s, List<Type> list, Env env) throws EvalException {
        var lambda = env.get(s);
        if (lambda.isEmpty()) {
            throw new EvalException("Unbound symbol: " + s);
        }

        var func = lambda.get();
        return switch (func) {
            case Type.Lambda lamb -> {
                var new_env = Env.extend(env);
                var params = lamb.params();
                var args = list.subList(1, list.size());
                var body = lamb.body();
                if (params.size() != args.size()) {
                    throw new EvalException(String.format(
                        "the number of arguments not match lambda defination\n\tlambda `%s` expected %d arguments, found %d",
                        s, params.size(), args.size()
                    ));
                }
                for (int i = 0; i < args.size(); i++) {
                    var val = evalType(args.get(i), env);
                    new_env.set(params.get(i), val);
                }
                yield evalType(new Type.List(body), new_env);
            }
            default -> throw new EvalException("Not a lambda: " + s);
        };
    }

    public static Type evalSymbol(String s, Env env) throws EvalException {
        var val = env.get(s);
        if (val.isPresent()) {
            return val.get();
        } else {
            throw new EvalException("Unbound symbol: " + s);
        }
    }

    public static Type evalList(List<Type> list, Env env) throws EvalException {
        try {
            var head = list.get(0);
            return switch (head) {
                case Type.UnaryOperator uop -> evalUnaryOperator(list, env);
                case Type.BinaryOperator bop -> evalBinaryOperator(list, env);
                case Type.Keyword keyword -> evalKeyword(list, env);
                case Type.Symbol sym -> evalFunctionCall(sym.val(), list, env);
                default -> {
                    var new_list = new ArrayList<Type>();
                    for (var type : list) {
                        var res = evalType(type, env);
                        switch (res) {
                            case Type.Unit __ -> {
                            }
                            default -> new_list.add(res);
                        }
                    }
                    yield new Type.List(new_list);
                }
            };
        } catch (IndexOutOfBoundsException e) {
            return new Type.Nil();
        }
    }

    private static Type evalKeyword(List<Type> list, Env env) throws EvalException {
        var head = list.get(0);
        return switch (head) {
            case Type.Keyword keyword -> switch (keyword.val()) {
                case "define" -> evalDefine(list, env);
                case "lambda" -> evalFunctionDefinition(list);
                case "if" -> evalIf(list, env);
                case "null?" -> evalNull(list, env);
                case "car" -> evalCar(list, env);
                case "cdr" -> evalCdr(list, env);
                default -> throw new EvalException("Unknown keyword: " + keyword.val());
            };
            default -> throw new EvalException("Invalid keyword: " + head);
        };
    }

    private static Type evalCar(List<Type> list, Env env) throws EvalException {
        if (list.size() != 2) {
            throw new EvalException("invalid number of arguments for `car`");
        }

        var val = evalType(list.get(1), env);
        return switch (val) {
            case Type.Nil nil -> nil;
            case Type.List l -> switch (l.val().size()) {
                case 0 -> new Type.Nil();
                default -> l.val().get(0);
            };
            default -> throw new EvalException("invalid argument type for `car`");
        };
    }

    private static Type evalCdr(List<Type> list, Env env) throws EvalException {
        if (list.size() != 2) {
            throw new EvalException("invalid number of arguments for `cdr`");
        }
        var val = evalType(list.get(1), env);
        return switch (val) {
            case Type.Nil nil -> nil;
            case Type.List l -> switch (l.val().size()) {
                case 0 -> new Type.Nil();
                default -> new Type.List(l.val().subList(1, l.val().size()));
            };
            default -> throw new EvalException("invalid argument type for `cdr`");
        };
    }

    private static Type evalNull(List<Type> list, Env env) throws EvalException {
        if (list.size() != 2) {
            throw new EvalException("invalid number of arguments for null?");
        }

        var val = evalType(list.get(1), env);
        return switch (val) {
            case Type.Nil nil -> new Type.Bool(true);
            case Type.List l -> new Type.Bool(false);
            default -> throw new EvalException("invalid argument type for null?");
        };
    }

    public static Type evalType(Type type, Env env) throws EvalException {
        return switch (type) {
            case Type.Integer i -> i;
            case Type.Bool b -> b;
            case Type.List l -> evalList(l.val(), env);
            case Type.Symbol sym -> evalSymbol(sym.val(), env);
            case Type.Keyword k -> k;
            case Type.Lambda __ -> new Type.Unit();
            case Type.Unit u -> u;
            case Type.Nil nil -> nil;
            case Type.UnaryOperator uop -> uop;
            case Type.BinaryOperator bop -> bop;
        };
    }

    public static Type eval(String input, Env env) throws EvalException, EmptyException, ParseException {
        Type type = Parser.parse(Lexer.tokenize(input));
        return evalType(type, env);
    }
}
