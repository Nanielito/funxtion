package com.nan.funxtion.types.functional;

import java.util.Objects;

@FunctionalInterface
public interface CheckedBiConsumer<T, U> {

    void accept(T left, U right) throws Throwable;

    default CheckedBiConsumer<T, U> andThen(CheckedBiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return after::accept;
    }
}
