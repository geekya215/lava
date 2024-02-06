package io.geekya215.lava.tokenizer;

public sealed interface Keywords
        permits Keywords.CAR, Keywords.CDR, Keywords.COND, Keywords.CONS, Keywords.DEF, Keywords.DEFAULT, Keywords.ELSE,
        Keywords.EQ, Keywords.EVAL, Keywords.FN, Keywords.IF, Keywords.LIST, Keywords.MATCH, Keywords.PROG, Keywords.QUOTE,
        Keywords.MACRO {
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

    record PROG() implements Keywords {
        @Override
        public String toString() {
            return "PROG";
        }
    }

    record EQ() implements Keywords {
        @Override
        public String toString() {
            return "EQ";
        }
    }

    record EVAL() implements Keywords {
        @Override
        public String toString() {
            return "EVAL";
        }
    }

    record MATCH() implements Keywords {
        @Override
        public String toString() {
            return "MATCH";
        }
    }

    record DEFAULT() implements Keywords {
        @Override
        public String toString() {
            return "DEFAULT";
        }
    }

    record MACRO() implements Keywords {
        @Override
        public String toString() {
            return "MACRO";
        }
    }
}
