package io.geekya215.lava;

public final class Ref<T> {
    private T value;

    public Ref(T value) {
        this.value = value;
    }

    public T unwrap() {
        return this.value;
    }

    public void update(T newValue) {
        this.value = newValue;
    }
}
