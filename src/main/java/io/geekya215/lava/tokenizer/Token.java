package io.geekya215.lava.tokenizer;

public sealed interface Token
        permits Token.LeftParen, Token.RightParen, Token.Quote,
        Token.Symbol, Token.Number, Token.SpaceChar, Token.EOF {
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

    record Quote(String value) implements Token {
        @Override
        public String toString() {
            return value;
        }
    }

    record Symbol(String value) implements Token {
        @Override
        public String toString() {
            return value;
        }
    }

    record Number(String value) implements Token {
        @Override
        public String toString() {
            return value;
        }
    }

    record SpaceChar(WhiteSpace ws) implements Token {
        @Override
        public String toString() {
            return ws.toString();
        }
    }

    record EOF() implements Token {
        @Override
        public String toString() {
            return "EOF";
        }
    }
}
