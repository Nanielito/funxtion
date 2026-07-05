package com.nan.funxtion.types;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OptionTest {

    @Nested
    class Some {

        @Test
        void shouldRejectNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> Option.some(null));
        }

        @Test
        void shouldCreate() {
            final Option<Integer> option = Option.some(10);

            assertTrue(option.isDefined());
            assertFalse(option.isEmpty());
        }
    }

    @Nested
    class None {

        @Test
        void shouldReturnInstance() {
            final Option<Integer> option = Option.none();

            assertFalse(option.isDefined());
            assertTrue(option.isEmpty());
        }

        @Test
        void shouldReturnSameInstance() {
            final Option<Integer> optionA = Option.none();
            final Option<Integer> optionB = Option.none();

            assertFalse(optionA.isDefined());
            assertTrue(optionB.isEmpty());
            assertFalse(optionB.isDefined());
            assertTrue(optionA.isEmpty());
            assertSame(optionA, optionB);
        }
    }

    @Nested
    class OfNullable {

        @Test
        void shouldReturnNoneFromNullableNull() {
            final Option<Integer> option = Option.ofNullable(null);

            assertFalse(option.isDefined());
            assertTrue(option.isEmpty());
        }

        @Test
        void shouldReturnSomeFromNonNullable() {
            final Option<Integer> option = Option.ofNullable(10);

            assertTrue(option.isDefined());
            assertFalse(option.isEmpty());
        }
    }

    @Nested
    class Map {

        @Nested
        class Some {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .map(null));
            }

            @Test
            void shouldMap() {
                final Option<Integer> result = Option.some(10)
                        .map(v -> v * 2);

                assertEquals(Option.some(20), result);
            }

            @Test
            void shouldConvertNullMappingToNone() {
                final Option<Integer> result = Option.some(10)
                        .map(v -> null);

                assertSame(Option.none(), result);
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.<Integer>none()
                                .map(null));
            }

            @Test
            void shouldReturnInstance() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Option<Integer> result = Option.<Integer>none()
                        .map(v -> {
                            called.set(true);
                            return v * 2;
                        });

                assertFalse(called.get());
                assertSame(Option.none(), result);
            }
        }
    }

    @Nested
    class FlatMap {

        @Nested
        class Some {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .map(null));
            }

            @Test
            void shouldRejectNullFlapMapResult() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(null)
                                .flatMap(v -> null));
            }

            @Test
            void shouldFlatMap() {
                final Option<Integer> result = Option.some(10)
                        .flatMap(v -> Option.some(v * 2));

                assertEquals(Option.some(20), result);
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.<Integer>none()
                                .map(null));
            }

            @Test
            void shouldNotFlatMap() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Option<Integer> result = Option.<Integer>none()
                        .flatMap(v -> {
                            called.set(true);
                            return Option.some(v * 2);
                        });

                assertFalse(called.get());
                assertSame(Option.none(), result);
            }
        }
    }

    @Nested
    class Filter {

        @Nested
        class Some {

            @Test
            void shouldFailWhenPredicateIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .filter(null));
            }

            @Test
            void shouldKeepMatchingValue() {
                final Option<Integer> result = Option.some(10)
                        .filter( v -> v > 5);

                assertEquals(Option.some(10), result);
            }

            @Test
            void shouldDiscardNonMatchingValue() {
                final Option<Integer> result = Option.some(10)
                        .filter( v -> v < 5);

                assertSame(Option.none(), result);
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenPredicateIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.<Integer>none()
                                .filter(null));
            }

            @Test
            void shouldReturnInstance() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Option<Integer> result = Option.<Integer>none()
                        .filter(v -> {
                            called.set(true);
                            return v > 5;
                        });

                assertFalse(called.get());
                assertSame(Option.none(), result);
            }
        }
    }

    @Nested
    class Fold {

        @Nested
        class Some {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .fold(null, "value=%s"::formatted));
            }

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .fold(() -> "empty", null));
            }

            @Test
            void shouldFold() {
                final String result = Option.some(10)
                        .fold(
                                () -> "empty",
                                "value=%s"::formatted);

                assertEquals("value=10", result);
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.none()
                                .fold(null, "value=%s"::formatted));
            }

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.none()
                                .fold(() -> "empty", null));
            }

            @Test
            void shouldFold() {
                final String result = Option.<Integer>none()
                        .fold(
                                () -> "empty",
                                "value=%s"::formatted);

                assertEquals("empty", result);
            }
        }
    }

    @Nested
    class GetOrElse {

        @Nested
        class Some {

            @Test
            void shouldReturnValue() {
                final Option<Integer> result = Option.some(10);

                assertEquals(10, result.getOrElse(20));
            }
        }

        @Nested
        class None {

            @Test
            void shouldReturnOtherValue() {
                final Option<Integer> result = Option.none();

                assertEquals(10, result.getOrElse(10));
            }
        }
    }

    @Nested
    class getOrElseGet {

        @Nested
        class Some {

            @Test
            void shouldReturnValue() {
                final Option<Integer> result = Option.some(10);

                assertEquals(10, result.getOrElseGet(() -> 20));
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.none()
                                .orElseGet(null));
            }

            @Test
            void shouldReturnOtherValue() {
                final Option<Integer> result = Option.none();

                assertEquals(10, result.getOrElseGet(() -> 10));
            }
        }
    }

    @Nested
    class OrElse {

        @Nested
        class Some {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .orElse(null));
            }

            @Test
            void shouldReturnValue() {
                final Option<Integer> result = Option.some(10);

                assertEquals(Option.some(10), result.orElse(Option.some(20)));
            }
        }

        @Nested
        class None {

            @Test
            void shouldReturnOtherValue() {
                final Option<Integer> result = Option.none();

                assertEquals(Option.some(20), result.orElse(Option.some(20)));
            }
        }
    }

    @Nested
    class OrElseGet {

        @Nested
        class Some {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .orElseGet(null));
            }

            @Test
            void shouldReturnValue() {
                final Option<Integer> result = Option.some(10);

                assertEquals(Option.some(10), result.orElseGet(() -> Option.some(20)));
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.none()
                                .orElseGet(null));
            }

            @Test
            void shouldReturnOtherValue() {
                final Option<Integer> result = Option.none();

                assertEquals(Option.some(20), result.orElseGet(() -> Option.some(20)));
            }
        }
    }

    @Nested
    class ToEither {

        @Nested
        class Some {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .toEither(null));
            }

            @Test
            void shouldConvertToRight() {
                final Either<String, Integer> result = Option.some(10)
                        .toEither(() -> "error");

                assertEquals(Either.right(10), result);
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.<Integer>none()
                                .toEither(null));
            }

            @Test
            void shouldFailWhenSupplierReturnsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.<Integer>none()
                                .toEither(() -> null));
            }

            @Test
            void shouldConvertToLeft() {
                final Either<String, Integer> result = Option.<Integer>none()
                        .toEither(() -> "missing");

                assertEquals(Either.left("missing"), result);
            }
        }
    }

    @Nested
    class ToList {

        @Nested
        class Some {

            @Test
            void shouldConvertToSingletonList() {
                final ImmutableList<Integer> result = Option.some(10)
                        .toList();

                assertEquals(List.of(10), result.toList());
            }
        }

        @Nested
        class None {

            @Test
            void shouldConvertToEmptyList() {
                final ImmutableList<Integer> result = Option.<Integer>none()
                        .toList();

                assertEquals(List.of(), result.toList());
            }
        }
    }

    @Nested
    class ToTry {

        @Nested
        class Some {

            @Test
            void shouldFailWhenThrowableSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.some(10)
                                .toTry(null));
            }

            @Test
            void shouldConvertToSuccess() {
                final Throwable ex = new NullPointerException("error");
                final Try<Integer> result = Option.some(10)
                        .toTry(() -> ex);

                assertEquals(Try.success(10), result);
            }
        }

        @Nested
        class None {

            @Test
            void shouldFailWhenThrowableSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.<Integer>none()
                                .toTry(null));
            }

            @Test
            void shouldFailWhenThrowableSupplierResultIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Option.<Integer>none()
                                .toTry(() -> null));
            }

            @Test
            void shouldConvertToFailure() {
                final Throwable ex = new NullPointerException("error");
                final Try<Integer> result = Option.<Integer>none()
                        .toTry(() -> ex);

                assertEquals(Try.failure(ex), result);
            }
        }
    }

    @Nested
    class Equals {

        @Nested
        class Some {

            @Test
            void shouldCompareValues() {
                assertEquals(Option.some(10), Option.some(10));
                assertNotEquals(Option.some(10), Option.some(20));
            }
        }

        @Nested
        class None {

            @Test
            void shouldCompareInstances() {
                assertSame(Option.none(), Option.none());
            }
        }
    }

    @Nested
    class HashCode {

        @Nested
        class Some {

            @Test
            void shouldGetHashCode() {
                assertNotEquals(0, Option.some(10).hashCode());
            }
        }

        @Nested
        class None {

            @Test
            void shouldGetHashCode() {
                assertEquals(0, Option.none().hashCode());
            }
        }
    }

    @Nested
    class ToString {

        @Nested
        class Some {

            @Test
            void shouldConvertToString() {
                assertEquals("Some(10)", Option.some(10).toString());
            }
        }

        @Nested
        class None {

            @Test
            void shouldConvertToString() {
                assertEquals("None", Option.none().toString());
            }
        }
    }

    @Nested
    class Identity {

        @Nested
        class Some {

            @Test
            void shouldRespectFunctorIdentity() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Option<Integer> option = Option.some(10);
                final Option<Integer> mapped = option.map(v -> {
                    called.set(true);
                    return v;
                });

                assertTrue(called.get());
                assertEquals(option, mapped);
            }
        }

        @Nested
        class None {

            @Test
            void shouldIgnoreIdentity() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Option<Integer> option = Option.none();
                final Option<Integer> mapped = option.map(v -> {
                    called.set(true);
                    return v;
                });

                assertFalse(called.get());
                assertEquals(Option.none(), mapped);
                assertSame(option, mapped);
            }
        }
    }

    @Nested
    class Composition {

        @Nested
        class Some {

           @Test
           void shouldRespectFunctorComposition() {
               final Function<Integer, Integer> fn = x -> x + 1;
               final Function<Integer, Integer> gn = x -> x + 2;
               final Option<Integer> option = Option.some(10);
               final Option<Integer> left = option
                       .map(fn)
                       .map(gn);
               final Option<Integer> right = option
                       .map(fn.andThen(gn));

               assertEquals(Option.some(13), left);
               assertEquals(Option.some(13), right);
               assertEquals(left, right);
           }
        }

        @Nested
        class None {

            @Test
            void shouldIgnoreComposition() {
                final Function<Integer, Integer> fn = x -> x + 1;
                final Function<Integer, Integer> gn = x -> x + 2;
                final Option<Integer> option = Option.none();
                final Option<Integer> left = option
                        .map(fn)
                        .map(gn);
                final Option<Integer> right = option
                        .map(fn.andThen(gn));

                assertEquals(Option.none(), left);
                assertEquals(Option.none(), right);
                assertSame(left, right);
            }
        }
    }

    @Nested
    class Monad {

        @Nested
        class Some {

            @Test
            void shouldRespectLeftIdentity() {
                final Function<Integer, Option<Integer>> fn = x -> Option.some( x + 1);
                final Option<Integer> left = Option.some(10)
                        .flatMap(fn);
                final Option<Integer> right = fn.apply(10);

                assertEquals(Option.some(11), left);
                assertEquals(Option.some(11), right);
                assertEquals(left, right);
            }

            @Test
            void shouldRespectRightIdentity() {
                final Option<Integer> option = Option.some(10);
                final Option<Integer> result = option
                        .flatMap(Option::some);

                assertTrue(result.isDefined());
                assertEquals(option, result);
            }

            @Test
            void shouldRespectAssociativity() {
                final Function<Integer, Option<Integer>> fn = x -> Option.some(x + 1);
                final Function<Integer, Option<Integer>> gn = x -> Option.some(x * 2);
                final Option<Integer> option = Option.some(10);
                final Option<Integer> left = option
                        .flatMap(fn)
                        .flatMap(gn);
                final Option<Integer> right = option
                        .flatMap(x -> fn.apply(x).flatMap(gn));

                assertEquals(Option.some(22),  left);
                assertEquals(Option.some(22), right);
                assertEquals(left, right);
            }
        }

        @Nested
        class None {

            @Test
            void shouldIgnoreRightIdentity() {
                final Option<Integer> option = Option.none();
                final Option<Integer> result = option
                        .flatMap(v -> Option.none());

                assertTrue(result.isEmpty());
                assertEquals(option, result);
            }

            @Test
            void shouldRespectAssociativity() {
                final Function<Integer, Option<Integer>> fn = x -> Option.some(x + 1);
                final Function<Integer, Option<Integer>> gn = x -> Option.some(x * 2);
                final Option<Integer> option = Option.none();
                final Option<Integer> left = option
                        .flatMap(fn)
                        .flatMap(gn);
                final Option<Integer> right = option
                        .flatMap(x -> fn.apply(x).flatMap(gn));

                assertEquals(Option.none(),  left);
                assertEquals(Option.none(), right);
                assertEquals(left, right);
            }
        }
    }
}
