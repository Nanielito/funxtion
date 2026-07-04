package com.nan.funxtion.types.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckedFunctionTest {

    @Nested
    class AndThen {

        @Test
        void shouldFailWhenAfterIsNull() {
            final CheckedFunction<Integer, Integer> function = value -> value + 1;

            assertThrows(
                    NullPointerException.class,
                    () -> function.andThen(null));
        }

        @Test
        void shouldComposeFunctionsInOrder() throws Throwable {
            final CheckedFunction<Integer, Integer> function = value -> value + 1;
            final CheckedFunction<Integer, Integer> result = function.andThen(value -> value * 2);

            assertEquals(8, result.apply(3));
        }

        @Test
        void shouldPropagateFunctionException() {
            final RuntimeException exception = new RuntimeException("boom");
            final CheckedFunction<Integer, Integer> function = value -> {
                throw exception;
            };

            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> function.andThen(value -> value * 2).apply(3));

            assertSame(exception, thrown);
        }
    }

    @Nested
    class Compose {

        @Test
        void shouldFailWhenBeforeIsNull() {
            final CheckedFunction<Integer, Integer> function = value -> value + 1;

            assertThrows(
                    NullPointerException.class,
                    () -> function.compose(null));
        }

        @Test
        void shouldComposeFunctionsInOrder() throws Throwable {
            final CheckedFunction<Integer, Integer> function = value -> value + 1;
            final CheckedFunction<Integer, Integer> result = function.compose(value -> value * 2);

            assertEquals(7, result.apply(3));
        }
    }

    @Nested
    class Identity {

        @Test
        void shouldReturnInput() throws Throwable {
            final String value = "funxtion";

            assertSame(value, CheckedFunction.<String>identity().apply(value));
        }
    }
}
