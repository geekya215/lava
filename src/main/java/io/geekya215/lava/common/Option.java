package io.geekya215.lava.common;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Option<T> permits Option.Some, Option.None {
    static <T> @NotNull Option<T> some(@NotNull final T value) {
        return new Some<>(value);
    }

    static <T> @NotNull Option<T> none() {
        return new None<>();
    }

    static <T, R> @NotNull Option<R> map(@NotNull final Option<T> o,
                                         @NotNull final Function<? super T, ? extends R> fn) {
        return switch (o) {
            case Option.Some(T value) -> Option.some(fn.apply(value));
            default -> Option.none();
        };
    }

    @SuppressWarnings("unchecked")
    static <T, R> Option<R> bind(@NotNull final Option<T> o,
                                 @NotNull final Function<? super T, ? extends Option<? extends R>> fn) {
        return switch (o) {
            case Option.Some(T value) -> (Option<R>) fn.apply(value);
            default -> Option.none();
        };
    }

    default <X extends Throwable> T getOrThrow(@NotNull final Supplier<? extends X> exceptionSupplier) throws X {
        return switch (this) {
            case Option.Some(T value) -> value;
            default -> throw exceptionSupplier.get();
        };
    }

    record Some<T>(@NotNull T value) implements Option<T> {
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
}
