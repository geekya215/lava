package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.expr.*;

public class Interpreter {
    public static Expr eval(Expr expr) throws EvalException {
        return switch (expr) {
            case IntegerExpr integerExpr -> integerExpr;
            case BoolExpr boolExpr -> boolExpr;
            case PlusExpr plusExpr -> {
                var left = eval(plusExpr.left());
                var right = eval(plusExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() + _r.value());
                } else {
                    throw new EvalException("invalid type for plus expr");
                }
            }
            case MinusExpr minusExpr -> {
                var left = eval(minusExpr.left());
                var right = eval(minusExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() - _r.value());
                } else {
                    throw new EvalException("invalid type for minus expr");
                }
            }
            case MulExpr mulExpr -> {
                var left = eval(mulExpr.left());
                var right = eval(mulExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() * _r.value());
                } else {
                    throw new EvalException("invalid type for mul expr");
                }
            }
            case DivExpr divExpr -> {
                var left = eval(divExpr.left());
                var right = eval(divExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() / _r.value());
                } else {
                    throw new EvalException("invalid type for div expr");
                }
            }
            case EqExpr eqExpr -> {
                var left = eval(eqExpr.left());
                var right = eval(eqExpr.right());
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
                var left = eval(ltExpr.left());
                var right = eval(ltExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() < _r.value());
                } else {
                    throw new EvalException("invalid type for lt expr");
                }
            }
            case GtExpr gtExpr -> {
                var left = eval(gtExpr.left());
                var right = eval(gtExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() > _r.value());
                } else {
                    throw new EvalException("invalid type for gt expr");
                }
            }
            case LtEqExpr ltEqExpr -> {
                var left = eval(ltEqExpr.left());
                var right = eval(ltEqExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() <= _r.value());
                } else {
                    throw new EvalException("invalid type for lteq expr");
                }
            }
            case GtEqExpr gtEqExpr -> {
                var left = eval(gtEqExpr.left());
                var right = eval(gtEqExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new BoolExpr(_l.value() >= _r.value());
                } else {
                    throw new EvalException("invalid type for gteq expr");
                }
            }
            default -> throw new EvalException("invalid expr for eval");
        };
    }
}