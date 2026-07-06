package com.nan.funxtion.types.functional;

import java.util.Objects;

/**
 * A one-argument predicate whose operation may throw any {@link Throwable}.
 *
 * @param <T> the tested value type
 */
@FunctionalInterface
public interface CheckedPredicate<T> {

    /**
     * Tests the given value.
     *
     * @param value the input value
     * @return {@code true} if the value matches this predicate
     * @throws Throwable if the predicate fails
     */
    boolean test(T value) throws Throwable;

    /**
     * Returns a composed predicate using short-circuiting logical AND.
     *
     * @param other the predicate to combine with this predicate
     * @return a predicate that matches when both predicates match
     * @throws NullPointerException if {@code other} is null
     */
    default CheckedPredicate<T> and(final CheckedPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return value -> test(value) && other.test(value);
    }

    /**
     * Returns a composed predicate using short-circuiting logical OR.
     *
     * @param other the predicate to combine with this predicate
     * @return a predicate that matches when either predicate matches
     * @throws NullPointerException if {@code other} is null
     */
    default CheckedPredicate<T> or(final CheckedPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return value -> test(value) || other.test(value);
    }

    /**
     * Returns a predicate that negates this predicate.
     *
     * @return the negated predicate
     */
    default CheckedPredicate<T> negate() {
        return value -> !test(value);
    }

    /**
     * Returns a predicate that always matches.
     *
     * @param <T> the tested value type
     * @return a predicate that always returns {@code true}
     */
    static <T> CheckedPredicate<T> alwaysTrue() {
        return value -> true;
    }

    /**
     * Returns a predicate that never matches.
     *
     * @param <T> the tested value type
     * @return a predicate that always returns {@code false}
     */
    static <T> CheckedPredicate<T> alwaysFalse() {
        return value -> false;
    }
}
