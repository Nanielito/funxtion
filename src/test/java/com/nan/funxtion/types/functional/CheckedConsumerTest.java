package com.nan.funxtion.types.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckedConsumerTest {

    @Nested
    class AndThen {

        @Test
        void shouldFailWhenAfterIsNull() {
            final CheckedConsumer<Integer> consumer = value -> {
            };

            assertThrows(
                    NullPointerException.class,
                    () -> consumer.andThen(null));
        }

        @Test
        void shouldRunConsumersInOrder() throws Throwable {
            final StringBuilder builder = new StringBuilder();
            final CheckedConsumer<String> consumer = value -> builder.append("first:").append(value);
            final CheckedConsumer<String> result = consumer.andThen(value -> builder.append(",second:").append(value));

            result.accept("x");

            assertEquals("first:x,second:x", builder.toString());
        }

        @Test
        void shouldNotRunAfterWhenFirstConsumerFails() {
            final RuntimeException exception = new RuntimeException("boom");
            final StringBuilder builder = new StringBuilder();
            final CheckedConsumer<String> consumer = value -> {
                throw exception;
            };
            final CheckedConsumer<String> result = consumer.andThen(builder::append);

            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> result.accept("x"));

            assertSame(exception, thrown);
            assertEquals("", builder.toString());
        }
    }
}
