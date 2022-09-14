package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.expr.*;

public class Interpreter {
    public static Expr eval(Expr expr, Env env) throws EvalException {
        return switch (expr) {
            case IntegerExpr integerExpr -> integerExpr;
            case BoolExpr boolExpr -> boolExpr;
            case NilExpr nilExpr -> nilExpr;
            case ConsExpr consExpr -> {
                var car = eval(consExpr.car(), env);
                var cdr = eval(consExpr.cdr(), env);
                yield new ConsExpr(car, cdr);
            }
            case QuoteExpr quoteExpr -> quoteExpr.arg();
            case PlusExpr plusExpr -> {
                var left = eval(plusExpr.left(), env);
                var right = eval(plusExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() + _r.value());
                } else {
                    throw new EvalException("invalid type for plus expr");
                }
            }
            case MinusExpr minusExpr -> {
                var left = eval(minusExpr.left(), env);
                var right = eval(minusExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() - _r.value());
                } else {
                    throw new EvalException("invalid type for minus expr");
                }
            }
            case MulExpr mulExpr -> {
                var left = eval(mulExpr.left(), env);
                var right = eval(mulExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() * _r.value());
                } else {
                    throw new EvalException("invalid type for mul expr");
                }
            }
            case DivExpr divExpr -> {
                var left = eval(divExpr.left(), env);
                var right = eval(divExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() / _r.value());
                } else {
                    throw new EvalException("invalid type for div expr");
                }
            }
            case EqExpr eqExpr -> {
                var left = eval(eqExpr.left(), env);
                var right = eval(eqExpr.right(), env);
                // Todo
                // only support integer and bool, will support other types in the future.
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() == _r.value());
                } else if (left instanceof BoolExpr _l && right instanceof BoolExpr _r) {
                    yield new BoolExpr(_l.value() == _r.value());
                } else {
                    throw new EvalException("invalid type for eq expr");
                }
            }
            case LtExpr ltExpr -> {
                var left = eval(ltExpr.left(), env);
                var right = eval(ltExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() < _r.value());
                } else {
                    throw new EvalException("invalid type for lt expr");
                }
            }
            case GtExpr gtExpr -> {
                var left = eval(gtExpr.left(), env);
                var right = eval(gtExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() > _r.value());
                } else {
                    throw new EvalException("invalid type for gt expr");
                }
            }
            case LtEqExpr ltEqExpr -> {
                var left = eval(ltEqExpr.left(), env);
                var right = eval(ltEqExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() <= _r.value());
                } else {
                    throw new EvalException("invalid type for lteq expr");
                }
            }
            case GtEqExpr gtEqExpr -> {
                var left = eval(gtEqExpr.left(), env);
                var right = eval(gtEqExpr.right(), env);
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() >= _r.value());
                } else {
                    throw new EvalException("invalid type for gteq expr");
                }
            }
            case NotExpr notExpr -> {
                var arg = eval(notExpr.arg(), env);
                if (arg instanceof BoolExpr _arg) {
                    yield new BoolExpr(!_arg.value());
                } else {
                    throw new EvalException("invalid type for not expr");
                }
            }
            case AndExpr andExpr -> {
                var left = eval(andExpr.left(), env);
                var right = eval(andExpr.right(), env);
                if (left instanceof BoolExpr _l && right instanceof BoolExpr _r) {
                    yield new BoolExpr(_l.value() && _r.value());
                } else {
                    throw new EvalException("invalid type for and expr");
                }
            }
            case OrExpr orExpr -> {
                var left = eval(orExpr.left(), env);
                var right = eval(orExpr.right(), env);
                if (left instanceof BoolExpr _l && right instanceof BoolExpr _r) {
                    yield new BoolExpr(_l.value() || _r.value());
                } else {
                    throw new EvalException("invalid type for or expr");
                }
            }
            case XorExpr xorExpr -> {
                var left = eval(xorExpr.left(), env);
                var right = eval(xorExpr.right(), env);
                if (left instanceof BoolExpr _l && right instanceof BoolExpr _r) {
                    yield new BoolExpr(_l.value() ^ _r.value());
                } else {
                    throw new EvalException("invalid type for xor expr");
                }
            }
            case CarExpr carExpr -> {
                var arg = eval(carExpr.arg(), env);
                if (arg instanceof ConsExpr _arg) {
                    yield _arg.car();
                } else if (arg instanceof NilExpr _arg) {
                    yield _arg;
                } else {
                    throw new EvalException("invalid type for car expr");
                }
            }
            case CdrExpr cdrExpr -> {
                var arg = eval(cdrExpr.arg(), env);
                if (arg instanceof ConsExpr _arg) {
                    yield _arg.cdr();
                } else if (arg instanceof NilExpr _arg) {
                    yield _arg;
                } else {
                    throw new EvalException("invalid type for car expr");
                }
            }
            case IfExpr ifExpr -> {
                var cond = eval(ifExpr.cond(), env);
                if (cond instanceof BoolExpr _cond) {
                    if (_cond.value()) {
                        yield eval(ifExpr.trueBranch(), env);
                    } else {
                        yield eval(ifExpr.falseBranch(), env);
                    }
                } else {
                    throw new EvalException("invalid type for if expr");
                }
            }
            case DefineExpr defineExpr -> {
                var id = defineExpr.id();
                var var = eval(defineExpr.var(), env);
                env.set(id, var);
                yield var;
            }
            case RefExpr refExpr -> {
                var id = refExpr.id();
                var var = env.get(id);
                if (var.isPresent()) {
                    yield var.get();
                } else {
                    throw new EvalException("attempted to access undefined variable: " + id);
                }
            }
            case AppExpr appExpr -> {
                var fun = eval(appExpr.fun(), env);
                if (fun instanceof LambdaExpr _fun) {
                    var args = appExpr.args();
                    var params = _fun.params();
                    if (args.size() != params.size()) {
                        throw new EvalException(String.format("called function with %d argument(s), expected %d argument(s)", args.size(), params.size()));
                    } else {
                        var newEnv = Env.extend(env);
                        for (int i = 0; i < args.size(); i++) {
                            var arg = eval(args.get(i), env);
                            newEnv.set(params.get(i), arg);
                        }
                        yield eval(_fun.body(), newEnv);
                    }
                } else {
                    throw new EvalException("invalid function call");
                }
            }
            case LambdaExpr lambdaExpr -> lambdaExpr;
            default -> throw new EvalException("invalid expr for eval");
        };
    }
}