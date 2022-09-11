package io.geekya215.lava;

public sealed interface Token permits
    Token.Div, Token.False, Token.LeftParenthesis, Token.Minus, Token.Mod, Token.Mul, Token.Symbol,
    Token.Neg, Token.Nil, Token.Number, Token.Plus, Token.RightParenthesis, Token.True
{
    record Symbol(String value) implements Token {
        @Override
        public String toString() {
            return value;
        }
    }

    record Nil() implements Token {
        @Override
        public String toString() {
            return "nil";
        }
    }

    record LeftParenthesis() implements Token {
        @Override
        public String toString() {
            return "(";
        }
    }

    record RightParenthesis() implements Token {
        @Override
        public String toString() {
            return ")";
        }
    }

    record Number(Integer number) implements Token {
        @Override
        public String toString() {
            return number.toString();
        }
    }

    record True() implements Token {
        @Override
        public String toString() {
            return "#t";
        }
    }

    record False() implements Token {
        @Override
        public String toString() {
            return "#f";
        }
    }

    record Neg() implements Token {
        @Override
        public String toString() {
            return "neg";
        }
    }

    record Plus() implements Token {
        @Override
        public String toString() {
            return "+";
        }
    }

    record Minus() implements Token {
        @Override
        public String toString() {
            return "-";
        }
    }

    record Mul() implements Token {
        @Override
        public String toString() {
            return "*";
        }
    }

    record Div() implements Token {
        @Override
        public String toString() {
            return "/";
        }
    }

    record Mod() implements Token {
        @Override
        public String toString() {
            return "mod";
        }
    }
}
