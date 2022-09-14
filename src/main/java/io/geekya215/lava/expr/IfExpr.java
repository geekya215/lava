package io.geekya215.lava.expr;

public record IfExpr(Expr cond, Expr trueBranch, Expr falseBranch) implements Expr {
}
