package com.nan.funxtion.types.functional;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckedBiPredicateTest {

    @Nested
    class And {

        @Test
        void shouldFailWhenOtherIsNull() {
            final CheckedBiPredicate<Integer, Integer> predicate = (left, right) -> left > right;

            assertThrows(
                    NullPointerException.class,
                    () -> predicate.and(null));
        }

        @Test
        void shouldReturnTrueWhenBothPredicatesMatch() throws Throwable {
            final CheckedBiPredicate<Integer, Integer> predicate =
                    ((CheckedBiPredicate<Integer, Integer>) (left, right) -> left > right)
                            .and((left, right) -> left + right > 0);

            assertTrue(predicate.test(3, 1));
        }

        @Test
        void shouldShortCircuitWhenFirstPredicateDoesNotMatch() throws Throwable {
            final AtomicBoolean called = new AtomicBoolean(false);
            final CheckedBiPredicate<Integer, Integer> predicate =
                    ((CheckedBiPredicate<Integer, Integer>) (left, right) -> false)
                            .and((left, right) -> {
                                called.set(true);
                                return true;
                            });

            assertFalse(predicate.test(1, 2));
            assertFalse(called.get());
        }
    }

    @Nested
    class Or {

        @Test
        void shouldFailWhenOtherIsNull() {
            final CheckedBiPredicate<Integer, Integer> predicate = (left, right) -> left > right;

            assertThrows(
                    NullPointerException.class,
                    () -> predicate.or(null));
        }

        @Test
        void shouldReturnTrueWhenAnyPredicateMatches() throws Throwable {
            final CheckedBiPredicate<Integer, Integer> predicate =
                    ((CheckedBiPredicate<Integer, Integer>) (left, right) -> left < right)
                            .or((left, right) -> left + right > 0);

            assertTrue(predicate.test(3, 1));
        }

        @Test
        void shouldShortCircuitWhenFirstPredicateMatches() throws Throwable {
            final AtomicBoolean called = new AtomicBoolean(false);
            final CheckedBiPredicate<Integer, Integer> predicate =
                    ((CheckedBiPredicate<Integer, Integer>) (left, right) -> true)
                            .or((left, right) -> {
                                called.set(true);
                                return false;
                            });

            assertTrue(predicate.test(1, 2));
            assertFalse(called.get());
        }
    }

    @Nested
    class Negate {

        @Test
        void shouldNegatePredicate() throws Throwable {
            final CheckedBiPredicate<Integer, Integer> predicate =
                    ((CheckedBiPredicate<Integer, Integer>) (left, right) -> left > right).negate();

            assertFalse(predicate.test(3, 1));
            assertTrue(predicate.test(1, 3));
        }
    }

    @Nested
    class Exceptions {

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException exception = new RuntimeException("boom");
            final CheckedBiPredicate<Integer, Integer> predicate = (left, right) -> {
                throw exception;
            };

            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> predicate.negate().test(1, 2));

            assertSame(exception, thrown);
        }
    }
}
