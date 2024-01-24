package io.geekya215.lava.tokenizer;

public sealed interface WhiteSpace permits WhiteSpace.Space, WhiteSpace.Tab, WhiteSpace.NewLine {
    record Space() implements WhiteSpace {
        @Override
        public String toString() {
            return " ";
        }
    }

    record Tab() implements WhiteSpace {
        @Override
        public String toString() {
            return "\\t";
        }
    }

    record NewLine() implements WhiteSpace {
        @Override
        public String toString() {
            return "\\n";
        }
    }
}
