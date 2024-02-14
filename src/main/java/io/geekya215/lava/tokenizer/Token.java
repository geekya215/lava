package io.geekya215.lava.tokenizer;

public sealed interface Token
        permits Token.Annotation, Token.EOF, Token.Keyword, Token.LeftParen, Token.Number, Token.Operator,
        Token.QuasiQuote, Token.Quote, Token.RightParen, Token.SpaceChar, Token.Symbol, Token.Underscore,
        Token.Unquote, Token.UnquoteSplicing {
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
            return "QuasiQuote";
        }
    }

    record Unquote() implements Token {
        @Override
        public String toString() {
            return "Unquote";
        }
    }

    record UnquoteSplicing() implements Token {
        @Override
        public String toString() {
            return "UnquoteSplicing";
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

    record Annotation(Annotations annotations) implements Token {
        @Override
        public String toString() {
            return annotations.toString();
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

    record Underscore() implements Token {
        @Override
        public String toString() {
            return "_";
        }
    }

    record EOF() implements Token {
        @Override
        public String toString() {
            return "EOF";
        }
    }
}
