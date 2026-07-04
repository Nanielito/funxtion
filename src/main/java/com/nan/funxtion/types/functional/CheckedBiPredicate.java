package com.nan.funxtion.types.functional;

import java.util.Objects;

@FunctionalInterface
public interface CheckedBiPredicate<T, U> {

    boolean test(T left, U right) throws Throwable;

    default CheckedBiPredicate<T, U> and(CheckedBiPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return (T left, U right) -> test(left, right) && other.test(left, right);
    }

    default CheckedBiPredicate<T, U> or(CheckedBiPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return (T left, U right) -> test(left, right) || other.test(left, right);
    }

    default CheckedBiPredicate<T, U> negate() {
        return (T left, U right) -> !test(left, right);
    }
}
