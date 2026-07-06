package com.nan.funxtion.types.functional;

import java.util.Objects;

/**
 * A one-argument function whose operation may throw any {@link Throwable}.
 *
 * @param <T> the input value type
 * @param <R> the result type
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {

    /**
     * Applies this function to the given value.
     *
     * @param value the input value
     * @return the function result
     * @throws Throwable if the function fails
     */
    R apply(T value) throws Throwable;

    /**
     * Returns a composed function that first applies this function and then applies {@code after}.
     *
     * @param <V> the composed result type
     * @param after the function to apply after this function
     * @return a composed function
     * @throws NullPointerException if {@code after} is null
     */
    default <V> CheckedFunction<T, V> andThen(final CheckedFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return value -> after.apply(apply(value));
    }

    /**
     * Returns a composed function that first applies {@code before} and then applies this function.
     *
     * @param <V> the input type of the composed function
     * @param before the function to apply before this function
     * @return a composed function
     * @throws NullPointerException if {@code before} is null
     */
    default <V> CheckedFunction<V, R> compose(final CheckedFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return value -> apply(before.apply(value));
    }

    /**
     * Returns a function that always returns its input.
     *
     * @param <T> the input and result type
     * @return the identity function
     */
    static <T> CheckedFunction<T, T> identity() {
        return value -> value;
    }
}
