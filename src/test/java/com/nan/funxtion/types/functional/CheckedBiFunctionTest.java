package com.nan.funxtion.types.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckedBiFunctionTest {

    @Nested
    class AndThen {

        @Test
        void shouldFailWhenAfterIsNull() {
            final CheckedBiFunction<Integer, Integer, Integer> function = Integer::sum;

            assertThrows(
                    NullPointerException.class,
                    () -> function.andThen(null));
        }

        @Test
        void shouldComposeFunctionsInOrder() throws Throwable {
            final CheckedBiFunction<Integer, Integer, Integer> function = Integer::sum;
            final CheckedBiFunction<Integer, Integer, Integer> result = function.andThen(value -> value * 2);

            assertEquals(10, result.apply(2, 3));
        }

        @Test
        void shouldPropagateFunctionException() {
            final RuntimeException exception = new RuntimeException("boom");
            final CheckedBiFunction<Integer, Integer, Integer> function = (left, right) -> {
                throw exception;
            };

            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> function.andThen(value -> value * 2).apply(2, 3));

            assertSame(exception, thrown);
        }
    }
}
