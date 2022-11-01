package io.geekya215.lava.utils;

import io.geekya215.lava.adt.Expr;

import java.util.ArrayList;
import java.util.List;

public final class Utils {
    private Utils() {
    }

    public static String preprocessInput(String input) {
        return (input + "\n")
                .replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ")
                .replaceAll("'", " ' ")
                .replaceAll(";;[^\\n\\r]*?(?:[\\n\\r])", "");
    }

    public static List<Expr> deepCopyExprList(List<Expr> src) {
        var res = new ArrayList<Expr>();
        for (Expr cur : src) {
            if (cur instanceof Expr.List list) {
                res.add(new Expr.List(deepCopyExprList(list.value())));
            } else {
                res.add(cur);
            }
        }
        return res;
    }
}
