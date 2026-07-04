package com.nan.funxtion.types.functional;

import java.util.Objects;

@FunctionalInterface
public interface CheckedFunction<T, R> {

    R apply(T value) throws Throwable;

    default <V> CheckedFunction<T, V> andThen(final CheckedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return value -> after.apply(apply(value));
    }

    default <V> CheckedFunction<V, R> compose(final CheckedFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return value -> apply(before.apply(value));
    }

    static <T> CheckedFunction<T, T> identity() {
        return value -> value;
    }
}
