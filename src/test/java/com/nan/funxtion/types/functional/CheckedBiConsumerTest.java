package com.nan.funxtion.types.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckedBiConsumerTest {

    @Nested
    class AndThen {

        @Test
        void shouldFailWhenAfterIsNull() {
            final CheckedBiConsumer<Integer, Integer> consumer = (left, right) -> {
            };

            assertThrows(
                    NullPointerException.class,
                    () -> consumer.andThen(null));
        }

        @Test
        void shouldRunConsumersInOrder() throws Throwable {
            final StringBuilder builder = new StringBuilder();
            final CheckedBiConsumer<String, String> consumer = (left, right) -> builder.append(left).append(right);
            final CheckedBiConsumer<String, String> result = consumer.andThen((left, right) -> builder.append(right).append(left));

            result.accept("a", "b");

            assertEquals("abba", builder.toString());
        }

        @Test
        void shouldNotRunAfterWhenFirstConsumerFails() {
            final RuntimeException exception = new RuntimeException("boom");
            final StringBuilder builder = new StringBuilder();
            final CheckedBiConsumer<String, String> consumer = (left, right) -> {
                throw exception;
            };
            final CheckedBiConsumer<String, String> result = consumer.andThen((left, right) -> builder.append(left).append(right));

            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> result.accept("a", "b"));

            assertSame(exception, thrown);
            assertEquals("", builder.toString());
        }
    }
}
