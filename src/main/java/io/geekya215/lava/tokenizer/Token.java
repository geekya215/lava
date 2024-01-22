package io.geekya215.lava.tokenizer;

public sealed interface Token
        permits Token.EOF, Token.LeftParen, Token.Number, Token.Operator,
        Token.Quote, Token.RightParen, Token.SpaceChar, Token.Symbol {
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

    record Operator(Operators operators) implements Token {
        @Override
        public String toString() {
            return operators.toString();
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
