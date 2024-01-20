package io.geekya215.lava.tokenizer;

import java.util.Iterator;

public final class Peekable<E> {
    private final Iterator<E> iter;
    private boolean peeked;
    private Option<E> value;

    public Peekable(Iterator<E> iter) {
        this.iter = iter;
        this.peeked = false;
        this.value = Option.none();
    }

    public Option<E> peek() {
        if (peeked) {
            return value;
        }

        peeked = true;

        if (iter.hasNext()) {
            value = Option.some(iter.next());
        } else {
            value = Option.none();
        }

        return value;
    }

    public Option<E> next() {
        if (peeked) {
            peeked = false;
            return value;
        }

        if (iter.hasNext()) {
            value = Option.some(iter.next());
        } else {
            value = Option.none();
        }

        return value;
    }
}
