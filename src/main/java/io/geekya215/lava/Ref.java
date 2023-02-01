package io.geekya215.lava;

public class Ref<T> {
    private T value;

    private Ref() {
    }

    public Ref(T value) {
        this.value = value;
    }

    public static <T> Ref<T> of(T value) {
        return new Ref(value);
    }

    public T unwrap() {
        return value;
    }

    public void update(T value) {
        this.value = value;
    }
}
