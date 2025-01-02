package io.geekya215.lava.common;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class Peekable<E> {
    private @NotNull final Iterator<E> iter;
    private boolean peeked;
    private @NotNull Option<E> value;

    public Peekable(@NotNull final Iterator<E> iter) {
        this.iter = iter;
        this.peeked = false;
        this.value = Option.none();
    }

    public @NotNull Option<E> peek() {
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

    public @NotNull Option<E> next() {
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
