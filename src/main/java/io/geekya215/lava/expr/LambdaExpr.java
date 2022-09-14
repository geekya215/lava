package io.geekya215.lava.expr;

import java.util.List;

public record LambdaExpr(List<String> params, Expr body) implements Expr {
}
