package io.geekya215.lava.parser;

import io.geekya215.lava.common.Signature;
import io.geekya215.lava.interpreter.Env;
import io.geekya215.lava.tokenizer.Annotations;
import io.geekya215.lava.tokenizer.Token;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed interface Expr extends Signature
        permits Expr.Annotation, Expr.Atom, Expr.FN, Expr.MACRO, Expr.QuasiQuote, Expr.Quote, Expr.Splicing, Expr.Unit,
        Expr.Unquote, Expr.UnquoteSplicing, Expr.Vec {
    record Vec(List<Expr> exprs) implements Expr {
        @Override
        public String toString() {
            return "(" + exprs.stream().map(Objects::toString).collect(Collectors.joining(" ")) + ")";
        }

        @Override
        public String descriptor() {
            return "[" + exprs.stream().map(Signature::descriptor).collect(Collectors.joining(", ")) + "]";
        }
    }

    record Quote(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "'" + expr.toString();
        }

        @Override
        public String descriptor() {
            return "Quote{" + expr.descriptor() + "}";
        }
    }

    record QuasiQuote(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "`" + expr;
        }

        @Override
        public String descriptor() {
            return "QuasiQuote{" + expr.descriptor() + "}";
        }
    }

    record Unquote(Expr expr) implements Expr {
        @Override
        public String toString() {
            return "," + expr;
        }

        @Override
        public String descriptor() {
            return "Unquote{" + expr.descriptor() + "}";
        }
    }

    record UnquoteSplicing(Expr expr) implements Expr {
        @Override
        public String toString() {
            return ",@" + expr;
        }

        @Override
        public String descriptor() {
            return "Unquote Splicing{" + expr.descriptor() + "}";
        }
    }

    record Atom(Token tok) implements Expr {
        @Override
        public String toString() {
            return tok.toString();
        }

        @Override
        public String descriptor() {
            return tok.toString() + ": " + tok.getClass().getSimpleName();
        }
    }

    record FN(List<String> params, Expr body, Env closure) implements Expr {
        // Todo
        // closure to string will stack overflow
        @Override
        public String toString() {
            return "(fn (" + String.join(" ", params) + ") " + body.toString() + ")";
        }

        @Override
        public String descriptor() {
            return "FN{" +
                    "params=" + params +
                    ", body=" + body.descriptor() +
                    "}";
        }
    }

    record MACRO(List<Expr> params, Expr body) implements Expr {
        @Override
        public String toString() {
            return "(macro (" + params.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ") " + body.toString() + ")";
        }

        @Override
        public String descriptor() {
            return "MACRO{" +
                    "params=" + params +
                    ", body=" + body.descriptor() +
                    '}';
        }
    }

    record Annotation(Annotations annotations, String name) implements Expr {
        @Override
        public String toString() {
            return "&" + annotations + " " + name;
        }

        @Override
        public String descriptor() {
            return "Annotation{" + name + ": " + annotations + "}";
        }
    }

    record Splicing(List<Expr> exprs) implements Expr {
        @Override
        public String toString() {
            return exprs.stream().map(Objects::toString).collect(Collectors.joining(" "));
        }

        @Override
        public String descriptor() {
            return "Splicing{" + exprs + '}';
        }
    }

    record Unit() implements Expr {
        @Override
        public String toString() {
            return "";
        }

        @Override
        public String descriptor() {
            return "<Unit>";
        }
    }
}
