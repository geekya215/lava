import exception.EmptyException;
import exception.EvalException;
import exception.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Eval {
    public static Type evalBinaryOp(List<Type> list, Env env) throws EvalException {
        if (list.size() != 3) {
            throw new EvalException("Invalid number of arguments for infix operator");
        }
        var op = list.get(0);
        var left = evalType(list.get(1), env);
        var right = evalType(list.get(2), env);

        var left_val = switch (left) {
            case Type.Integer i -> i.val();
            default -> throw new EvalException("Left operand must be an integer " + left);
        };

        var right_val = switch (right) {
            case Type.Integer i -> i.val();
            default -> throw new EvalException("Right operand must be an integer " + right);
        };

        return switch (op) {
            case Type.Symbol sym -> switch (sym.val()) {
                case "+" -> new Type.Integer(left_val + right_val);
                case "-" -> new Type.Integer(left_val - right_val);
                case "*" -> new Type.Integer(left_val * right_val);
                case "/" -> new Type.Integer(left_val / right_val);
                case "<" -> new Type.Bool(left_val < right_val);
                case ">" -> new Type.Bool(left_val > right_val);
                case "=" -> new Type.Bool(left_val == right_val);
                case "!=" -> new Type.Bool(left_val != right_val);
                default -> throw new EvalException("Invalid infix operator: " + sym);
            };
            default -> throw new EvalException("Operator must be a symbol");
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
                var body = lamb.body();
                for (int i = 0; i < params.size(); i++) {
                    var val = evalType(list.get(i + 1), env);
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
        var head = list.get(0);
        return switch (head) {
            case Type.Symbol sym -> switch (sym.val()) {
                case "+", "-", "*", "/", "<", ">", "=", "!=" -> evalBinaryOp(list, env);
                case "define" -> evalDefine(list, env);
                case "if" -> evalIf(list, env);
                case "lambda" -> evalFunctionDefinition(list);
                default -> evalFunctionCall(sym.val(), list, env);
            };
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
    }

    public static Type evalType(Type type, Env env) throws EvalException {
        return switch (type) {
            case Type.Integer i -> i;
            case Type.Bool b -> b;
            case Type.List l -> evalList(l.val(), env);
            case Type.Symbol sym -> evalSymbol(sym.val(), env);
            case Type.Lambda __ -> new Type.Unit();
            case Type.Unit u -> u;
        };
    }

    public static Type eval(String input, Env env) throws EvalException, EmptyException, ParseException {
        Type type = Parser.parse(Lexer.tokenize(input));
        return evalType(type, env);
    }
}
