package io.geekya215.lava;

import java.util.List;
import java.util.Optional;

public final class PeekingIterator<T> {
    private final List<T> list;
    private int currentIndex;
    private final int listSize;

    public PeekingIterator(List<T> list) {
        this.list = list;
        this.currentIndex = -1;
        this.listSize = list.size();
    }

    public Optional<T> peek() {
        if (currentIndex + 1 >= listSize) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(currentIndex + 1));
        }
    }

    public Optional<T> next() {
        if (currentIndex + 1 >= listSize) {
            return Optional.empty();
        } else {
            currentIndex += 1;
            return Optional.of(list.get(currentIndex));
        }
    }
}
