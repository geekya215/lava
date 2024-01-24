package io.geekya215.lava.tokenizer;

public sealed interface Operators
        permits Operators.Div, Operators.Eq, Operators.Gt, Operators.GtEq, Operators.LeftShift,
        Operators.Lt, Operators.LtEq, Operators.Minus, Operators.Mod, Operators.Mul, Operators.NotEq,
        Operators.Plus, Operators.RightShift {
    record Plus() implements Operators {
        @Override
        public String toString() {
            return "+";
        }
    }

    record Minus() implements Operators {
        @Override
        public String toString() {
            return "-";
        }
    }

    record Mul() implements Operators {
        @Override
        public String toString() {
            return "*";
        }
    }

    record Div() implements Operators {
        @Override
        public String toString() {
            return "/";
        }
    }

    record Mod() implements Operators {
        @Override
        public String toString() {
            return "%";
        }
    }

    record LeftShift() implements Operators {
        @Override
        public String toString() {
            return "<<";
        }
    }

    record RightShift() implements Operators {
        @Override
        public String toString() {
            return ">>";
        }
    }

    record Gt() implements Operators {
        @Override
        public String toString() {
            return ">";
        }
    }

    record GtEq() implements Operators {
        @Override
        public String toString() {
            return ">=";
        }
    }

    record Lt() implements Operators {
        @Override
        public String toString() {
            return "<";
        }
    }

    record LtEq() implements Operators {
        @Override
        public String toString() {
            return "<=";
        }
    }

    record Eq() implements Operators {
        @Override
        public String toString() {
            return "=";
        }
    }

    record NotEq() implements Operators {
        @Override
        public String toString() {
            return "<>";
        }
    }
}
