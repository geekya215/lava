package io.geekya215.lava.adt;

public sealed interface Token permits
    Token.Symbol, Token.Quote,
    Token.LeftParen, Token.RightParen {
    record Symbol(String value) implements Token {
        @Override
        public String toString() {
            return value;
        }
    }

    record Quote() implements Token {
        @Override
        public String toString() {
            return "'";
        }
    }

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
}
