package com.nan.funxtion.types.functional;

import java.util.Objects;

/**
 * A one-argument consumer whose operation may throw any {@link Throwable}.
 *
 * @param <T> the consumed value type
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

    /**
     * Performs this operation on the given value.
     *
     * @param value the input value
     * @throws Throwable if the operation fails
     */
    void accept(T value) throws Throwable;

    /**
     * Returns a composed consumer that performs this operation followed by {@code after}.
     *
     * @param after the operation to run after this operation
     * @return a composed consumer that runs both operations in order
     * @throws NullPointerException if {@code after} is null
     */
    default CheckedConsumer<T> andThen(CheckedConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return value -> {
            accept(value);
            after.accept(value);
        };
    }
}
