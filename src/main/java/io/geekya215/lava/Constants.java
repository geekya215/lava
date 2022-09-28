package io.geekya215.lava;

public final class Constants {
    public static final Expr TRUE = new Expr.Symbol("#t");
    public static final Expr FALSE = new Expr.Symbol("#f");

    public static final String PROMPT = "lava>";
    public static final String INDICATOR = "=> ";

    private Constants() {
    }
}
