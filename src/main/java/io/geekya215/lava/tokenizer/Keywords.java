package io.geekya215.lava.tokenizer;

public sealed interface Keywords
        permits Keywords.CAR, Keywords.CDR, Keywords.COND, Keywords.CONS, Keywords.DEF,
        Keywords.ELSE, Keywords.FN, Keywords.IF, Keywords.LIST, Keywords.QUOTE {
    record DEF() implements Keywords {
        @Override
        public String toString() {
            return "DEF";
        }
    }

    record FN() implements Keywords {
        @Override
        public String toString() {
            return "FN";
        }
    }

    record QUOTE() implements Keywords {
        @Override
        public String toString() {
            return "QUOTE";
        }
    }

    record IF() implements Keywords {
        @Override
        public String toString() {
            return "IF";
        }
    }

    record ELSE() implements Keywords {
        @Override
        public String toString() {
            return "ELSE";
        }
    }

    record COND() implements Keywords {
        @Override
        public String toString() {
            return "COND";
        }
    }

    record CONS() implements Keywords {
        @Override
        public String toString() {
            return "CONS";
        }
    }

    record CAR() implements Keywords {
        @Override
        public String toString() {
            return "CAR";
        }
    }

    record CDR() implements Keywords {
        @Override
        public String toString() {
            return "CDR";
        }
    }

    record LIST() implements Keywords {
        @Override
        public String toString() {
            return "LIST";
        }
    }
}
