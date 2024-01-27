package io.geekya215.lava.tokenizer;

public sealed interface Token
        permits Token.EOF, Token.Keyword, Token.LeftParen, Token.Number, Token.Operator,
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

    record Quote() implements Token {
        @Override
        public String toString() {
            return "'" ;
        }
    }

    record Operator(Operators operators) implements Token {
        @Override
        public String toString() {
            return operators.toString();
        }
    }

    record Keyword(Keywords keywords) implements Token {
        @Override
        public String toString() {
            return keywords.toString();
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
