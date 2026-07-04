package com.nan.funxtion.types.functional;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckedPredicateTest {

    @Nested
    class And {

        @Test
        void shouldFailWhenOtherIsNull() {
            final CheckedPredicate<Integer> predicate = value -> value > 0;

            assertThrows(
                    NullPointerException.class,
                    () -> predicate.and(null));
        }

        @Test
        void shouldReturnTrueWhenBothPredicatesMatch() throws Throwable {
            final CheckedPredicate<Integer> predicate = ((CheckedPredicate<Integer>) value -> value > 0)
                    .and(value -> value % 2 == 0);

            assertTrue(predicate.test(2));
        }

        @Test
        void shouldShortCircuitWhenFirstPredicateDoesNotMatch() throws Throwable {
            final AtomicBoolean called = new AtomicBoolean(false);
            final CheckedPredicate<Integer> predicate = ((CheckedPredicate<Integer>) value -> false)
                    .and(value -> {
                        called.set(true);
                        return true;
                    });

            assertFalse(predicate.test(1));
            assertFalse(called.get());
        }
    }

    @Nested
    class Or {

        @Test
        void shouldFailWhenOtherIsNull() {
            final CheckedPredicate<Integer> predicate = value -> value > 0;

            assertThrows(
                    NullPointerException.class,
                    () -> predicate.or(null));
        }

        @Test
        void shouldReturnTrueWhenAnyPredicateMatches() throws Throwable {
            final CheckedPredicate<Integer> predicate = ((CheckedPredicate<Integer>) value -> value < 0)
                    .or(value -> value % 2 == 0);

            assertTrue(predicate.test(2));
        }

        @Test
        void shouldShortCircuitWhenFirstPredicateMatches() throws Throwable {
            final AtomicBoolean called = new AtomicBoolean(false);
            final CheckedPredicate<Integer> predicate = ((CheckedPredicate<Integer>) value -> true)
                    .or(value -> {
                        called.set(true);
                        return false;
                    });

            assertTrue(predicate.test(1));
            assertFalse(called.get());
        }
    }

    @Nested
    class Negate {

        @Test
        void shouldNegatePredicate() throws Throwable {
            final CheckedPredicate<Integer> predicate = ((CheckedPredicate<Integer>) value -> value > 0).negate();

            assertFalse(predicate.test(1));
            assertTrue(predicate.test(-1));
        }
    }

    @Nested
    class StaticFactories {

        @Test
        void shouldReturnAlwaysTruePredicate() throws Throwable {
            assertTrue(CheckedPredicate.alwaysTrue().test("anything"));
        }

        @Test
        void shouldReturnAlwaysFalsePredicate() throws Throwable {
            assertFalse(CheckedPredicate.alwaysFalse().test("anything"));
        }
    }

    @Nested
    class Exceptions {

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException exception = new RuntimeException("boom");
            final CheckedPredicate<Integer> predicate = value -> {
                throw exception;
            };

            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> predicate.negate().test(1));

            assertSame(exception, thrown);
        }
    }
}
