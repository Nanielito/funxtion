package com.nan.funxtion.types.functional;

/**
 * A runnable operation that may throw any {@link Throwable}.
 */
@FunctionalInterface
public interface CheckedRunnable {

    /**
     * Runs this operation.
     *
     * @throws Throwable if the operation fails
     */
    void run() throws Throwable;
}
