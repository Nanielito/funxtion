package com.nan.funxtion.types.functional;

import java.util.Objects;

/**
 * A two-argument function whose operation may throw any {@link Throwable}.
 *
 * @param <T> the first input value type
 * @param <U> the second input value type
 * @param <R> the result type
 */
@FunctionalInterface
public interface CheckedBiFunction<T, U, R> {

    /**
     * Applies this function to the given values.
     *
     * @param left the first input value
     * @param right the second input value
     * @return the function result
     * @throws Throwable if the function fails
     */
    R apply(T left, U right) throws Throwable;

    /**
     * Returns a composed function that first applies this function and then applies {@code after}.
     *
     * @param <V> the composed result type
     * @param after the function to apply after this function
     * @return a composed function
     * @throws NullPointerException if {@code after} is null
     */
    default <V> CheckedBiFunction<T, U, V> andThen(final CheckedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T left, U right) -> after.apply(apply(left, right));
    }
}
