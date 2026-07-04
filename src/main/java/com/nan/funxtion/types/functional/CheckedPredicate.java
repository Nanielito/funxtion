package com.nan.funxtion.types.functional;

import java.util.Objects;

@FunctionalInterface
public interface CheckedPredicate<T> {

    boolean test(T value) throws Throwable;

    default CheckedPredicate<T> and(final CheckedPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return value -> test(value) && other.test(value);
    }

    default CheckedPredicate<T> or(final CheckedPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return value -> test(value) || other.test(value);
    }

    default CheckedPredicate<T> negate() {
        return value -> !test(value);
    }

    static <T> CheckedPredicate<T> alwaysTrue() {
        return value -> true;
    }

    static <T> CheckedPredicate<T> alwaysFalse() {
        return value -> false;
    }
}
