package io.geekya215.lava;

import java.util.Iterator;
import java.util.List;

public sealed interface SExpr permits SExpr.Atom, SExpr.Cons {
    record Atom(Token token) implements SExpr {
    }

    record Cons(List<SExpr> list) implements SExpr {
    }
}
