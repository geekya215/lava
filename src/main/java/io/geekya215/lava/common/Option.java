package io.geekya215.lava.common;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Option<T> permits Option.Some, Option.None {
    record Some<T>(T value) implements Option<T> {
        @Override
        public String toString() {
            return value.toString();
        }
    }

    record None<T>() implements Option<T> {
        @Override
        public String toString() {
            return "None";
        }
    }

    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    static <T> Option<T> none() {
        return new None<>();
    }

    default <X extends Throwable> T getOrThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return switch (this) {
            case Option.Some(T value) -> value;
            default -> throw exceptionSupplier.get();
        };
    }

    static <T, R> Option<R> map(Option<T> o, Function<? super T, ? extends R> fn) {
        return switch (o) {
            case Option.Some(T value) -> Option.some(fn.apply(value));
            default -> Option.none();
        };
    }

    @SuppressWarnings("unchecked")
    static <T, R> Option<R> bind(Option<T> o, Function<? super T, ? extends Option<? extends R>> fn) {
        return switch (o) {
            case Option.Some(T value) -> (Option<R>) fn.apply(value);
            default -> Option.none();
        };
    }
}
