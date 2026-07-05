package com.nan.funxtion.types;

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

class EitherTest {

    @Nested
    class Left {

        @Test
        void shouldRejectNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> Either.left(null));
        }

        @Test
        void shouldCreate() {
            final Either<String, Integer> either = Either.left("error");

            assertTrue(either.isLeft());
        }
    }

    @Nested
    class Right {

        @Test
        void shouldRejectNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> Either.right(null));
        }

        @Test
        void shouldCreate() {
            final Either<Integer, String> either = Either.right("10");

            assertTrue(either.isRight());
        }
    }

    @Nested
    class Map {

        @Nested
        class Left {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.left("error")
                                .map(null));
            }

            @Test
            void shouldNotMap() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Either<String, Integer> result = Either.<String, Integer>left("error")
                        .map(v -> {
                            called.set(true);
                            return v * 2;
                        });

                assertFalse(called.get());
                assertEquals(Either.left("error"), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.right(10)
                                .map(null));
            }

            @Test
            void shouldMap() {
                final Either<String, Integer> result = Either.<String, Integer>right(10)
                        .map(v -> v * 2);

                assertEquals(Either.right(20), result);
            }
        }
    }

    @Nested
    class FlatMap {

        @Nested
        class Left {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.left("error")
                                .flatMap(null));
            }

            @Test
            void shouldNotFlatMap() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Either<String, Integer> result = Either.left("error")
                        .flatMap(v -> {
                            called.set(true);
                            return Either.right(20);
                        });

                assertFalse(called.get());
                assertEquals(Either.left("error"), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.right(10)
                                .flatMap(null));
            }

            @Test
            void shouldFlatMap() {
                final Either<String, Integer> result = Either.<String, Integer>right(10)
                        .flatMap(v -> Either.right(v * 2));

                assertEquals(Either.right(20), result);
            }
        }
    }

    @Nested
    class MapLeft {

        @Nested
        class Left {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.left("error")
                                .mapLeft(null));
            }

            @Test
            void shouldFailWhenMapperResultIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.left("error")
                                .mapLeft(v -> null));
            }

            @Test
            void shouldMap() {
                final Either<String, Integer> result = Either.<String, Integer>left("error")
                        .mapLeft("value=%s"::formatted);

                assertEquals(Either.left("value=error"), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.right(10)
                                .mapLeft(null));
            }

            @Test
            void shouldNotFlatMap() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Either<String, Integer> result = Either.right(10)
                        .mapLeft(v -> {
                            called.set(true);
                            return "value=%s".formatted(v);
                        });

                assertFalse(called.get());
                assertEquals(Either.right(10), result);
            }
        }
    }

    @Nested
    class BiMap {

        @Nested
        class Left {

            @Test
            void shouldFailWhenLeftMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>left("error")
                                .bimap(null, v -> v * 2));
            }

            @Test
            void shouldFailWhenRightMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>left("error")
                                .bimap("value=%s"::formatted, null));
            }

            @Test
            void shouldFailWhenLeftMapperResultIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>left("error")
                                .bimap(v -> null, v -> v * 2));
            }

            @Test
            void shouldBimap() {
                final Either<String, Integer> result = Either.<String, Integer>left("error")
                        .bimap("value=%s"::formatted, v -> v * 2);

                assertEquals(Either.left("value=error"), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldFailWhenLeftMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>right(10)
                                .bimap(null, v -> v * 2));
            }

            @Test
            void shouldFailWhenRightMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>right(10)
                                .bimap("value=%s"::formatted, null));
            }

            @Test
            void shouldFailWhenRightMapperResultIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>right(10)
                                .bimap("value=%s"::formatted, v -> null));
            }

            @Test
            void shouldBimap() {
                final Either<String, Integer> result = Either.<String, Integer>right(10)
                        .bimap("value=%s"::formatted, v -> v * 2);

                assertEquals(Either.right(20), result);
            }
        }
    }

    @Nested
    class Fold {

        @Nested
        class Left {

            @Test
            void shouldFailWhenLeftMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<Integer, Integer>left(10)
                                .fold(null, v -> v * 2));
            }

            @Test
            void shouldFailWhenRightMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<Integer, Integer>left(10)
                                .fold(v -> v + 1, null));
            }

            @Test
            void shouldFold() {
                final int result = Either.<Integer, Integer>left(10)
                        .fold(v -> v + 1, v -> v * 2);

                assertEquals(11, result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldFailWhenLeftMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>right(10)
                                .fold(null, v -> v * 2));
            }

            @Test
            void shouldFailWhenRightMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>right(10)
                                .fold(v -> v + 1, null));
            }

            @Test
            void shouldFold() {
                final int result = Either.<Integer, Integer>right(10)
                        .fold(v -> v + 1, v -> v * 2);

                assertEquals(20, result);
            }
        }
    }

    @Nested
    class GetOrElse {

        @Nested
        class Left {

            @Test
            void shouldGetOtherValue() {
                final int result = Either.<Integer, Integer>left(10)
                        .getOrElse(20);

                assertEquals(20, result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldGetValue() {
                final int result = Either.<Integer, Integer>right(10)
                        .getOrElse(20);

                assertEquals(10, result);
            }
        }
    }

    @Nested
    class GetOrElseGet {

        @Nested
        class Left {

            @Test
            void shouldFailWhenSupplierIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<Integer, Integer>left(10)
                                .getOrElseGet(null));
            }

            @Test
            void shouldGetOtherValue() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final int result = Either.<Integer, Integer>left(10)
                        .getOrElseGet(() -> {
                            called.set(true);
                            return 20;
                        });

                assertTrue(called.get());
                assertEquals(20, result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldGetValue() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final int result = Either.<Integer, Integer>right(10)
                        .getOrElseGet(() -> {
                            called.set(true);
                            return 20;
                        });

                assertFalse(called.get());
                assertEquals(10, result);
            }
        }
    }

    @Nested
    class OrElse {

        @Nested
        class Left {

            @Test
            void shouldFailWhenOtherIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>left("error")
                                .orElse(null));
            }

            @Test
            void shouldGetOtherValue() {
                final Either<String, Integer> result = Either.<String, Integer>left("error")
                        .orElse(Either.right(10));

                assertEquals(Either.right(10), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldFailWhenOtherIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>right(10)
                                .orElse(null));
            }

            @Test
            void shouldGetValue() {
                final Either<String, Integer> result = Either.<String, Integer>right(10)
                        .orElse(Either.left("error"));

                assertEquals(Either.right(10), result);
            }
        }
    }

    @Nested
    class Swap {

        @Nested
        class Left {

            @Test
            void shouldSwap() {
                final Either<String, Integer> result = Either.<Integer, String>left(10)
                        .swap();

                assertEquals(Either.right(10), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldSwap() {
                final Either<Integer, String> result = Either.<String, Integer>right(10)
                        .swap();

                assertEquals(Either.left(10), result);
            }
        }
    }

    @Nested
    class ToOption {

        @Nested
        class Left {

            @Test
            void shouldGetNone() {
                final Option<Integer> result = Either.<String, Integer>left("error")
                        .toOption();

                assertEquals(Option.none(), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldGetSome() {
                final  Option<Integer> result = Either.<String, Integer>right(10)
                        .toOption();

                assertEquals(Option.some(10), result);
            }
        }
    }

    @Nested
    class ToTry {

        @Nested
        class Left {

            @Test
            void shouldFailWhenLeftMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>left("error")
                                .toTry(null));
            }

            @Test
            void shouldFailWhenLeftMapperResultIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>left("error")
                                .toTry(v -> null));
            }

            @Test
            void shouldConvertToFailure() {
                final Throwable ex = new NullPointerException("error");
                final Try<Integer> result = Either.<String, Integer>left("error")
                        .toTry(v -> ex);

                assertEquals(Try.failure(ex), result);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldFailWhenLeftMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Either.<String, Integer>right(10)
                                .toTry(null));
            }

            @Test
            void shouldConvertToSuccess() {
                final Throwable ex = new NullPointerException("error");
                final Try<Integer> result = Either.<String, Integer>right(10)
                        .toTry(v -> ex);

                assertEquals(Try.success(10), result);
            }
        }
    }

    @Nested
    class Equals {

        @Nested
        class Left {

            @Test
            void shouldCompareValues() {
                assertEquals(Either.left("error"), Either.left("error"));
                assertNotEquals(Either.left("error"), Either.left("other error"));
            }
        }

        @Nested
        class Right {

            @Test
            void shouldCompareValues() {
                assertEquals(Either.right(10), Either.right(10));
                assertNotEquals(Either.right(10), Either.right(20));
            }
        }
    }

    @Nested
    class HashCode {

        @Nested
        class Left {

            @Test
            void shouldGetHashCode() {
                assertEquals(Either.left("error").hashCode(), Either.left("error").hashCode());
                assertNotEquals(Either.left("error").hashCode(), Either.left("other error").hashCode());
            }
        }

        @Nested
        class Right {

            @Test
            void shouldGetHashCode() {
                assertEquals(Either.right(10).hashCode(), Either.right(10).hashCode());
                assertNotEquals(Either.right(10).hashCode(), Either.right(20).hashCode());
            }
        }
    }

    @Nested
    class ToString {

        @Nested
        class Left {

            @Test
            void shouldConvertToString() {
                assertEquals("Left(error)", Either.left("error").toString());
            }
        }

        @Nested
        class Right {

            @Test
            void shouldConvertToString() {
                assertEquals("Right(10)", Either.right(10).toString());
            }
        }
    }

    @Nested
    class Identity {

        @Nested
        class Left {

            @Test
            void shouldIgnoreIdentity() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Either<String, Integer> either = Either.left("error");
                final Either<String, Integer> mapped = either.map(v -> {
                    called.set(true);
                    return v;
                });

                assertFalse(called.get());
                assertSame(either, mapped);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldRespectFunctorIdentity() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Either<String, Integer> either = Either.right(10);
                final Either<String, Integer> mapped = either.map(v -> {
                    called.set(true);
                    return v;
                });

                assertTrue(called.get());
                assertEquals(either, mapped);
            }
        }
    }

    @Nested
    class Composition {

        @Nested
        class Left {

            @Test
            void shouldIgnoreComposition() {
                final Function<Integer, Integer> fn = x -> x + 1;
                final Function<Integer, Integer> gn = x -> x + 2;
                final Either<String, Integer> either = Either.left("error");
                final Either<String, Integer> left = either
                        .map(fn)
                        .map(gn);
                final Either<String, Integer> right = either
                        .map(fn.andThen(gn));

                assertEquals(Either.left("error"), left);
                assertEquals(Either.left("error"), right);
                assertSame(left, right);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldRespectFunctorComposition() {
                final Function<Integer, Integer> fn = x -> x + 1;
                final Function<Integer, Integer> gn = x -> x + 2;
                final Either<String, Integer> either = Either.right(10);
                final Either<String, Integer> left = either
                        .map(fn)
                        .map(gn);
                final Either<String, Integer> right = either
                        .map(fn.andThen(gn));

                assertEquals(Either.right(13), left);
                assertEquals(Either.right(13), right);
                assertEquals(left, right);
            }
        }
    }

    @Nested
    class Monad {

        @Nested
        class Left {

            @Test
            void shouldIgnoreRightIdentity() {
                final Either<Integer, Integer> either = Either.left(10);
                final Either<Integer, Integer> result = either
                        .flatMap(Either::right);

                assertTrue(result.isLeft());
                assertEquals(either, result);
            }

            @Test
            void shouldRespectAssociativity() {
                final Function<Integer, Either<Integer, Integer>> fn = x -> Either.left(x + 1);
                final Function<Integer, Either<Integer, Integer>> gn = x -> Either.left(x * 2);
                final Either<Integer, Integer> either = Either.left(10);
                final Either<Integer, Integer> left = either
                        .flatMap(fn)
                        .flatMap(gn);
                final Either<Integer, Integer> right = either
                        .flatMap(x -> fn.apply(x).flatMap(gn));

                assertEquals(Either.left(10),  left);
                assertEquals(Either.left(10), right);
                assertEquals(left, right);
            }
        }

        @Nested
        class Right {

            @Test
            void shouldRespectLeftIdentity() {
                final Function<Integer, Either<String, Integer>> fn = x -> Either.right( x + 1);
                final Either<String, Integer> left = Either.<String, Integer>right(10)
                        .flatMap(fn);
                final Either<String, Integer> right = fn.apply(10);

                assertEquals(Either.right(11), left);
                assertEquals(Either.right(11), right);
                assertEquals(left, right);
            }

            @Test
            void shouldRespectRightIdentity() {
                final Either<String, Integer> either = Either.right(10);
                final Either<String, Integer> result = either
                        .flatMap(Either::right);

                assertTrue(result.isRight());
                assertEquals(either, result);
            }

            @Test
            void shouldRespectAssociativity() {
                final Function<Integer, Either<String, Integer>> fn = x -> Either.right(x + 1);
                final Function<Integer, Either<String, Integer>> gn = x -> Either.right(x * 2);
                final Either<String, Integer> either = Either.right(10);
                final Either<String, Integer> left = either
                        .flatMap(fn)
                        .flatMap(gn);
                final Either<String, Integer> right = either
                        .flatMap(x -> fn.apply(x).flatMap(gn));

                assertEquals(Either.right(22),  left);
                assertEquals(Either.right(22), right);
                assertEquals(left, right);
            }
        }
    }
}
