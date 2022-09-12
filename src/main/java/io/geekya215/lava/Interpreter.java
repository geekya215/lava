package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.expr.*;

public class Interpreter {
    public static Expr eval(Expr expr) throws EvalException {
        return switch (expr) {
            case IntegerExpr integerExpr -> integerExpr;
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
                    throw new EvalException("invalid type for minus expr");
                }
            }
            case DivExpr divExpr -> {
                var left = eval(divExpr.left());
                var right = eval(divExpr.right());
                if (left instanceof IntegerExpr _l && right instanceof IntegerExpr _r) {
                    yield new IntegerExpr(_l.value() / _r.value());
                } else {
                    throw new EvalException("invalid type for minus expr");
                }
            }
            default -> throw new EvalException("invalid type for plus expr");
        };
    }
}