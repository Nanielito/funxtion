package com.nan.funxtion.types.functional;

import java.util.Objects;

/**
 * A two-argument consumer whose operation may throw any {@link Throwable}.
 *
 * @param <T> the first consumed value type
 * @param <U> the second consumed value type
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U> {

    /**
     * Performs this operation on the given values.
     *
     * @param left the first input value
     * @param right the second input value
     * @throws Throwable if the operation fails
     */
    void accept(T left, U right) throws Throwable;

    /**
     * Returns a composed consumer that performs this operation followed by {@code after}.
     *
     * @param after the operation to run after this operation
     * @return a composed consumer that runs both operations in order
     * @throws NullPointerException if {@code after} is null
     */
    default CheckedBiConsumer<T, U> andThen(CheckedBiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (left, right) -> {
            accept(left, right);
            after.accept(left, right);
        };
    }
}
