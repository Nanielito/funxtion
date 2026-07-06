package com.nan.funxtion.types.functional;

/**
 * A supplier whose operation may throw any {@link Throwable}.
 *
 * @param <T> the supplied value type
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * Gets a value.
     *
     * @return the supplied value
     * @throws Throwable if the supplier fails
     */
    T get() throws Throwable;
}
