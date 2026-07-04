package com.nan.funxtion.types.functional;

import java.util.Objects;

@FunctionalInterface
public interface CheckedConsumer<T> {

    void accept(T value) throws Throwable;

    default CheckedConsumer<T> andThen(CheckedConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return after::accept;
    }
}
