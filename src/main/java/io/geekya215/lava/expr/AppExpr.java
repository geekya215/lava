package io.geekya215.lava.expr;

import java.util.List;

public record AppExpr(Expr fun, List<Expr> args) implements Expr {
}
