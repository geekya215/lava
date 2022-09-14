package io.geekya215.lava.expr;

public record DefineExpr(String id, Expr var) implements Expr {
}
