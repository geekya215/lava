package io.geekya215.lava.tokenizer;

public sealed interface Option<T> permits Option.Some, Option.None  {
    record Some<T>(T value) implements Option<T> {
    }

    record None<T>() implements Option<T> {
    }

    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    static <T> Option<T> none() {
        return new None<>();
    }
}
