package io.geekya215.lava.tokenizer;

import org.jetbrains.annotations.NotNull;

public sealed interface Token
        permits Token.Annotation, Token.Comment, Token.EOF, Token.Keyword, Token.LeftParen, Token.Number, Token.Operator,
        Token.QuasiQuote, Token.Quote, Token.RightParen, Token.SpaceChar, Token.Symbol, Token.Underscore, Token.Unquote,
        Token.UnquoteSplicing {

    record LeftParen() implements Token {
        @Override
        public String toString() {
            return "(";
        }
    }

    record RightParen() implements Token {
        @Override
        public String toString() {
            return ")";
        }
    }

    record Quote() implements Token {
        @Override
        public String toString() {
            return "'";
        }
    }

    record QuasiQuote() implements Token {
        @Override
        public String toString() {
            return "`";
        }
    }

    record Unquote() implements Token {
        @Override
        public String toString() {
            return ",";
        }
    }

    record UnquoteSplicing() implements Token {
        @Override
        public String toString() {
            return ",@";
        }
    }

    record Operator(@NotNull Operators operators) implements Token {
        @Override
        public String toString() {
            return operators.toString();
        }
    }

    record Keyword(@NotNull Keywords keywords) implements Token {
        @Override
        public String toString() {
            return keywords.toString();
        }
    }

    record Annotation(@NotNull Annotations annotations) implements Token {
        @Override
        public String toString() {
            return annotations.toString();
        }
    }

    record Symbol(@NotNull String value) implements Token {
        @Override
        public String toString() {
            return value;
        }
    }

    record Number(@NotNull String value) implements Token {
        @Override
        public String toString() {
            return value;
        }
    }

    record SpaceChar(@NotNull WhiteSpace ws) implements Token {
        @Override
        public String toString() {
            return ws.toString();
        }
    }

    record Underscore() implements Token {
        @Override
        public String toString() {
            return "_";
        }
    }

    record Comment(@NotNull String s) implements Token {
        @Override
        public String toString() {
            return ";" + s;
        }
    }

    record EOF() implements Token {
        @Override
        public String toString() {
            return "EOF";
        }
    }
}
