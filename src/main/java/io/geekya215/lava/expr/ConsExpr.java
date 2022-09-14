package io.geekya215.lava.expr;

public record ConsExpr(Expr car, Expr cdr) implements Expr {
}
