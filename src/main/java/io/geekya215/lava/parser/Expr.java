package io.geekya215.lava.parser;

import io.geekya215.lava.interpreter.Env;
import io.geekya215.lava.tokenizer.Token;

import java.util.List;

public sealed interface Expr permits Expr.Atom, Expr.FN, Expr.MACRO, Expr.QuasiQuote, Expr.Quote, Expr.Unit,
        Expr.Unquote, Expr.Vec {
    record Vec(List<Expr> exprs) implements Expr {
        @Override
        public String toString() {
            return exprs.toString();
        }
    }

    record Quote(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "Quote{" + expr + "}";
        }
    }

    record QuasiQuote(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "QuasiQuote{" + expr + "}";
        }
    }

    record Unquote(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "Unquote{" + expr + "}";
        }
    }

    record Atom(Token tok) implements Expr {
        @Override
        public String toString() {
            return tok.toString() + ": " + tok.getClass().getSimpleName();
        }
    }

    record FN(List<String> params, Expr body, Env closure) implements Expr {
        // Todo
        // closure to string will stack overflow
        @Override
        public String toString() {
            return "FN{" +
                    "params=" + params +
                    ", body=" + body +
                    "}";
        }
    }

    record MACRO(List<String> params, Expr body) implements Expr {
        @Override
        public String toString() {
            return "MACRO{" +
                    "params=" + params +
                    ", body=" + body +
                    '}';
        }
    }

    record Unit() implements Expr {
        @Override
        public String toString() {
            return "<Unit>";
        }
    }
}
