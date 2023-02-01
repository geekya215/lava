package io.geekya215.lava;


import java.util.function.Function;
import java.util.stream.Collectors;

public sealed interface Expr permits
    Expr.Number, Expr.Symbol, Expr.Quote, Expr.List, Expr.Lambda, Expr.NativeFunc, Expr.Macro {
    record Number(Integer value) implements Expr {
        @Override
        public String toString() {
            return value.toString();
        }
    }

    record Symbol(String value) implements Expr {
        @Override
        public String toString() {
            return value;
        }
    }

    record Quote(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "'" + expr;
        }
    }

    record List(java.util.List<Expr> value) implements Expr {
        @Override
        public String toString() {
            return "(" + value.stream().map(Object::toString).collect(Collectors.joining(" ")) + ")";
        }
    }

    record Lambda(java.util.List<String> params, Expr body, Env closure) implements Expr {
        @Override
        public String toString() {
            var formattedParams = String.join(" ", params);
            return "(lambda (" + formattedParams + ") " + body.toString() + ")";
        }
    }

    record NativeFunc(String name, Function<java.util.List<Expr>, Expr> func, Env env) implements Expr {
        @Override
        public String toString() {
            return "<native " + name + ">";
        }
    }

    record Macro(java.util.List<String> params, Expr body, Env closure) implements Expr {
        @Override
        public String toString() {
            var formattedParams = String.join(" ", params);
            return "(marco (" + formattedParams + ") " + body.toString() + ")";
        }
    }
}
