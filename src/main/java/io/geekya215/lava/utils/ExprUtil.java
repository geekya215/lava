package io.geekya215.lava.utils;

import io.geekya215.lava.Constants;
import io.geekya215.lava.adt.Expr;
import io.geekya215.lava.exception.EvalException;

import java.util.Objects;

public final class ExprUtil {
    public static Boolean matchSymbol(Expr e, String v) {
        return e instanceof Expr.Symbol s && Objects.equals(s.value(), v);
    }

    public static Boolean matchList(Expr e, Integer size) {
        return e instanceof Expr.List l && Objects.equals(l.value().size(), size);
    }

    public static Boolean testExpr(Expr e) {
        return !switch (e) {
            case Expr.Symbol s && Objects.equals(s.value(), "#f") -> true;
            case Expr.List l && l.value().isEmpty() -> true;
            default -> false;
        };
    }

    public static String unwrapSymbol(Expr e) throws EvalException {
        return switch (e) {
            case Expr.Symbol s -> s.value();
            default -> throw new EvalException("expected symbol value, got " + e);
        };
    }

    public static Integer unwrapNumber(Expr e) throws EvalException {
        return switch (e) {
            case Expr.Number n -> n.value();
            default -> throw new EvalException("expected number value, got " + e);
        };
    }

    public static Expr getBooleanSymbol(Boolean b) {
        return b ? Constants.TRUE : Constants.FALSE;
    }
}
