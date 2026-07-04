package com.nan.funxtion.types.functional;

import java.util.Objects;

@FunctionalInterface
public interface CheckedBiFunction<T, U, R> {

    R apply(T left, U right) throws Throwable;

    default <V> CheckedBiFunction<T, U, V> andThen(final CheckedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T left, U right) -> after.apply(apply(left, right));
    }
}
