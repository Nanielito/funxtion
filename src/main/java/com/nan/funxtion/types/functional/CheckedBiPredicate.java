package com.nan.funxtion.types.functional;

import java.util.Objects;

/**
 * A two-argument predicate whose operation may throw any {@link Throwable}.
 *
 * @param <T> the first tested value type
 * @param <U> the second tested value type
 */
@FunctionalInterface
public interface CheckedBiPredicate<T, U> {

    /**
     * Tests the given values.
     *
     * @param left the first input value
     * @param right the second input value
     * @return {@code true} if the values match this predicate
     * @throws Throwable if the predicate fails
     */
    boolean test(T left, U right) throws Throwable;

    /**
     * Returns a composed predicate using short-circuiting logical AND.
     *
     * @param other the predicate to combine with this predicate
     * @return a predicate that matches when both predicates match
     * @throws NullPointerException if {@code other} is null
     */
    default CheckedBiPredicate<T, U> and(CheckedBiPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return (T left, U right) -> test(left, right) && other.test(left, right);
    }

    /**
     * Returns a composed predicate using short-circuiting logical OR.
     *
     * @param other the predicate to combine with this predicate
     * @return a predicate that matches when either predicate matches
     * @throws NullPointerException if {@code other} is null
     */
    default CheckedBiPredicate<T, U> or(CheckedBiPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return (T left, U right) -> test(left, right) || other.test(left, right);
    }

    /**
     * Returns a predicate that negates this predicate.
     *
     * @return the negated predicate
     */
    default CheckedBiPredicate<T, U> negate() {
        return (T left, U right) -> !test(left, right);
    }
}
