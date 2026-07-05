package com.nan.funxtion.types;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.nan.funxtion.types.functional.CheckedFunction;

class TryTest {

    @Nested
    class Success {

        @Test
        void shouldRejectNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> Try.success(null));
        }

        @Test
        void shouldCreate() {
            final Try<Integer> result = Try.success(10);

            assertTrue(result.isSuccess());
        }
    }

    @Nested
    class Failure {

        @Test
        void shouldRejectNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> Try.failure(null));
        }

        @Test
        void shouldCreate() {
            final Try<Integer> result = Try.failure(new NullPointerException());

            assertTrue(result.isFailure());
        }
    }

    @Nested
    class Of {

        @Test
        void shouldFailWhenSupplierIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> Try.of(null));
        }

        @Test
        void shouldCaptureException() {
            final RuntimeException ex =  new RuntimeException();
            final Try<Integer> result = Try.of(() -> {
                throw ex;
            });

            assertEquals(Try.failure(ex), result);
        }

        @Test
        void shouldCaptureInterruptedExceptionAndRestoreInterruptFlag() {
            Thread.interrupted();

            final InterruptedException ex = new InterruptedException();
            final Try<Integer> result = Try.of(() -> {
                throw ex;
            });

            assertTrue(result.isFailure());
            assertEquals(Try.failure(ex), result);
            assertTrue(Thread.currentThread().isInterrupted());

            Thread.interrupted();
        }

        @Test
        void shouldReturnFailureWhenSupplierReturnsNull() {
            final Try<Object> result = Try.of(() -> null);

            assertTrue(result.isFailure());
        }

        @Test
        void shouldCreate() {
            final Try<Integer> result = Try.of(() -> 10);

            assertEquals(Try.success(10), result);
        }
    }

    @Nested
    class Map {

        @Nested
        class Success {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.success(10)
                                .map(null));
            }

            @Test
            void shouldReturnFailureWhenMapperReturnsNull() {
                final Try<Integer> result = Try.success(10)
                        .map(v -> null);

                assertTrue(result.isFailure());
            }

            @Test
            void shouldCaptureInterruptedExceptionAndRestoreInterruptFlag() {
                Thread.interrupted();

                final InterruptedException ex = new InterruptedException();
                final Try<Integer> result = Try.success(10)
                        .map(v -> {
                            throw ex;
                        });

                assertTrue(result.isFailure());
                assertEquals(Try.failure(ex), result);
                assertTrue(Thread.currentThread().isInterrupted());

                Thread.interrupted();
            }

            @Test
            void shouldMap() {
                final Try<Integer> result = Try.success(10)
                        .map(v -> v * 2);

                assertTrue(result.isSuccess());
                assertEquals(Try.success(20), result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.failure(null));
            }

            @Test
            void shouldNotMap() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> result = Try.<Integer>failure(ex)
                        .map(v -> v * 2);

                assertTrue(result.isFailure());
                assertEquals(Try.failure(ex), result);
            }
        }
    }

    @Nested
    class FlatMap {

        @Nested
        class Success {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.success(10)
                                .flatMap(null));
            }

            @Test
            void shouldReturnFailureWhenMapperReturnsNull() {
                final Try<Integer> result = Try.success(10)
                        .flatMap(v -> null);

                assertTrue(result.isFailure());
            }

            @Test
            void shouldCaptureInterruptedExceptionAndRestoreInterruptFlag() {
                Thread.interrupted();

                final InterruptedException ex = new InterruptedException();
                final Try<Integer> result = Try.success(10)
                        .flatMap(v -> {
                            throw ex;
                        });

                assertTrue(result.isFailure());
                assertEquals(Try.failure(ex), result);
                assertTrue(Thread.currentThread().isInterrupted());

                Thread.interrupted();
            }

            @Test
            void shouldMap() {
                final Try<Integer> result = Try.success(10)
                        .flatMap(v -> Try.success(v * 2));

                assertTrue(result.isSuccess());
                assertEquals(Try.success(20), result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.failure(null));
            }

            @Test
            void shouldNotMap() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> result = Try.<Integer>failure(ex)
                        .flatMap(v -> Try.success(v * 2));

                assertTrue(result.isFailure());
                assertEquals(Try.failure(ex), result);
            }
        }
    }

    @Nested
    class Recover {

        @Nested
        class Success {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.success(10)
                                .recover(null));
            }

            @Test
            void shouldNotRecover() {
                final Try<Integer> result = Try.success(10)
                        .recover(ex -> 20);

                assertTrue(result.isSuccess());
                assertEquals(Try.success(10), result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldFailWhenMapperIsNull() {
                final Throwable ex = new NullPointerException("boom");
                assertThrows(
                        NullPointerException.class,
                        () -> Try.failure(ex)
                                .recover(null));
            }

            @Test
            void shouldReturnFailureWhenMapperReturnsNull() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> result = Try.<Integer>failure(ex)
                        .recover(e -> null);

                assertTrue(result.isFailure());
            }

            @Test
            void shouldCaptureInterruptedExceptionAndRestoreInterruptFlag() {
                Thread.interrupted();

                final InterruptedException ex = new InterruptedException();
                final Try<Integer> result = Try.<Integer>failure(new NullPointerException("boom"))
                        .recover( e -> {
                            throw ex;
                        });

                assertTrue(result.isFailure());
                assertEquals(Try.failure(ex), result);
                assertTrue(Thread.currentThread().isInterrupted());

                Thread.interrupted();
            }

            @Test
            void shouldRecover() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> result = Try.<Integer>failure(ex)
                        .recover(e -> 20);

                assertTrue(result.isSuccess());
                assertEquals(Try.success(20), result);
            }
        }
    }

    @Nested
    class RecoverWith {

        @Nested
        class Success {

            @Test
            void shouldFailWhenMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.success(10)
                                .recoverWith(null));
            }

            @Test
            void shouldNotRecoverWith() {
                final Try<Integer> result = Try.success(10)
                        .recoverWith(ex -> Try.success(20));

                assertTrue(result.isSuccess());
                assertEquals(Try.success(10), result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldFailWhenMapperIsNull() {
                final Throwable ex = new NullPointerException("boom");
                assertThrows(
                        NullPointerException.class,
                        () -> Try.failure(ex)
                                .recoverWith(null));
            }

            @Test
            void shouldReturnFailureWhenMapperReturnsNull() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> result = Try.<Integer>failure(ex)
                        .recoverWith(e -> null);

                assertTrue(result.isFailure());
            }

            @Test
            void shouldCaptureInterruptedExceptionAndRestoreInterruptFlag() {
                Thread.interrupted();

                final InterruptedException ex = new InterruptedException();
                final Try<Integer> result = Try.<Integer>failure(new NullPointerException("boom"))
                        .recoverWith( e -> {
                            throw ex;
                        });

                assertTrue(result.isFailure());
                assertEquals(Try.failure(ex), result);
                assertTrue(Thread.currentThread().isInterrupted());

                Thread.interrupted();
            }

            @Test
            void shouldRecoverWith() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> result = Try.<Integer>failure(ex)
                        .recoverWith(e -> Try.success(20));

                assertTrue(result.isSuccess());
                assertEquals(Try.success(20), result);
            }
        }
    }

    @Nested
    class Fold {

        @Nested
        class Success {

            @Test
            void shouldFailWhenFailureMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.success(10)
                                .fold(null, v -> v * 2));
            }

            @Test
            void shouldFailWhenSuccessMapperIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.success(10)
                                .fold(e -> 20, null));
            }

            @Test
            void shouldFold() {
                final int result = Try.success(10)
                        .fold(e -> 1, v -> v * 2);

                assertEquals(20, result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldFailWhenFailureMapperIsNull() {
                final  Throwable ex = new NullPointerException("boom");
                assertThrows(
                        NullPointerException.class,
                        () -> Try.<Integer>failure(ex)
                                .fold(null, v -> v * 2));
            }

            @Test
            void shouldFailWhenSuccessMapperIsNull() {
                final Throwable ex = new NullPointerException("boom");
                assertThrows(
                        NullPointerException.class,
                        () -> Try.<Integer>failure(ex)
                                .fold(e -> 20, null));
            }

            @Test
            void shouldFold() {
                final Throwable ex = new NullPointerException("boom");
                final int result = Try.<Integer>failure(ex)
                        .fold(e -> 1, v -> v * 2);

                assertEquals(1, result);
            }
        }
    }

    @Nested
    class GetOrElse {

        @Nested
        class Success {

            @Test
            void shouldReturnValue() {
                final int result = Try.success(10)
                        .getOrElse(20);

                assertEquals(10, result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldReturnOtherValue() {
                final Throwable ex = new NullPointerException("boom");
                final int result = Try.<Integer>failure(ex)
                        .getOrElse(20);

                assertEquals(20, result);
            }
        }
    }

    @Nested
    class GetOrElseGet {

        @Nested
        class Success {

            @Test
            void shouldReturnValue() {
                final int result = Try.success(10)
                        .getOrElseGet(() -> 20);

                assertEquals(10, result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldFailWhenSupplierIsNull() {
                final Throwable ex = new NullPointerException("boom");
                assertThrows(
                        NullPointerException.class,
                        () -> Try.<Integer>failure(ex)
                                .getOrElseGet(null));
            }

            @Test
            void shouldReturnOtherValue() {
                final Throwable ex = new NullPointerException("boom");
                final int result = Try.<Integer>failure(ex)
                        .getOrElseGet(() -> 20);

                assertEquals(20, result);
            }
        }
    }

    @Nested
    class OrElse {

        @Nested
        class Success {

            @Test
            void shouldFailWhenOtherIsNull() {
                assertThrows(
                        NullPointerException.class,
                        () -> Try.success(10)
                                .orElse(null));
            }

            @Test
            void shouldReturnValue() {
                final Try<Integer> result = Try.success(10)
                        .orElse(Try.success(20));

                assertEquals(Try.success(10), result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldFailWhenOtherIsNull() {
                final Throwable ex = new NullPointerException("boom");
                assertThrows(
                        NullPointerException.class,
                        () -> Try.<Integer>failure(ex)
                                .orElse(null));
            }

            @Test
            void shouldReturnOtherValue() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> result = Try.<Integer>failure(ex)
                        .orElse(Try.success(20));

                assertEquals(Try.success(20), result);
            }
        }
    }

    @Nested
    class ToOption {

        @Nested
        class Success {

            @Test
            void shouldGetSome() {
                final Option<Integer> result = Try.success(10)
                        .toOption();

                assertEquals(Option.some(10), result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldGetNone() {
                final Throwable ex = new NullPointerException("boom");
                final Option<Integer> result = Try.<Integer>failure(ex)
                        .toOption();

                assertEquals(Option.none(), result);
            }
        }
    }

    @Nested
    class ToEither {

        @Nested
        class Success {

            @Test
            void shouldConvertToRight() {
                final Either<Throwable, Integer> result = Try.success(10)
                        .toEither();

                assertEquals(Either.right(10), result);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldConvertToLeft() {
                final Throwable ex = new NullPointerException("boom");
                final Either<Throwable, Integer> result = Try.<Integer>failure(ex)
                        .toEither();

                assertEquals(Either.left(ex), result);
            }
        }
    }

    @Nested
    class ToList {

        @Nested
        class Success {

            @Test
            void shouldConvertToSingletonList() {
                final ImmutableList<Integer> result = Try.success(10)
                        .toList();

                assertEquals(List.of(10), result.toList());
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldConvertToEmptyList() {
                final Throwable ex = new NullPointerException("boom");
                final ImmutableList<Integer> result = Try.<Integer>failure(ex)
                        .toList();

                assertEquals(List.of(), result.toList());
            }
        }
    }

    @Nested
    class Equals {

        @Nested
        class Success {

            @Test
            void shouldCompareValues() {
                assertEquals(Try.success(10), Try.success(10));
                assertNotEquals(Try.success(10), Try.success(20));
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldCompareValues() {
                final Throwable ex1 = new NullPointerException("boom");
                final Throwable ex2 = new NullPointerException("POM");

                assertEquals(Try.failure(ex1), Try.failure(ex1));
                assertNotEquals(Try.failure(ex1), Try.failure(ex2));
            }
        }
    }

    @Nested
    class HashCode {

        @Nested
        class Success {

            @Test
            void shouldGetHashCode() {
                assertEquals(Try.success(10).hashCode(), Try.success(10).hashCode());
                assertNotEquals(Try.success(10).hashCode(), Try.success(20).hashCode());
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldGetHashCode() {
                final Throwable ex1 = new NullPointerException("boom");
                final Throwable ex2 = new NullPointerException("POM");

                assertEquals(Try.failure(ex1).hashCode(), Try.failure(ex1).hashCode());
                assertNotEquals(Try.failure(ex1).hashCode(), Try.failure(ex2).hashCode());
            }
        }
    }

    @Nested
    class ToString {

        @Nested
        class Success {

            @Test
            void shouldConvertToString() {
                assertEquals("Success(10)",  Try.success(10).toString());
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldConvertToString() {
                final Throwable ex = new NullPointerException("boom");

                assertEquals("Failure(java.lang.NullPointerException: boom)",  Try.failure(ex).toString());
            }
        }
    }

    @Nested
    class Identity {

        @Nested
        class Success {

            @Test
            void shouldRespectFunctorIdentity() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Try<Integer> t = Try.success(10);
                final Try<Integer> mapped = t.map(v -> {
                    called.set(true);
                    return v;
                });

                assertTrue(called.get());
                assertEquals(t, mapped);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldIgnoreIdentity() {
                final AtomicBoolean called = new AtomicBoolean(false);
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> t = Try.failure(ex);
                final Try<Integer> mapped = t.map(v -> {
                    called.set(true);
                    return v;
                });

                assertFalse(called.get());
                assertSame(t, mapped);
            }
        }
    }

    @Nested
    class Composition {

        @Nested
        class Success {

            @Test
            void shouldRespectFunctorComposition() {
                final CheckedFunction<Integer, Integer> fn = x -> x + 1;
                final CheckedFunction<Integer, Integer> gn = x -> x + 2;
                final Try<Integer> t = Try.success(10);
                final Try<Integer> left = t
                        .map(fn)
                        .map(gn);
                final Try<Integer> right = t
                        .map(fn.andThen(gn));

                assertEquals(Try.success(13), left);
                assertEquals(Try.success(13), right);
                assertEquals(left, right);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldIgnoreComposition() {
                final CheckedFunction<Integer, Integer> fn = x -> x + 1;
                final CheckedFunction<Integer, Integer> gn = x -> x + 2;
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> t = Try.failure(ex);
                final Try<Integer> left = t
                        .map(fn)
                        .map(gn);
                final Try<Integer> right = t
                        .map(fn.andThen(gn));

                assertEquals(Try.failure(ex), left);
                assertEquals(Try.failure(ex), right);
                assertSame(left, right);
            }
        }
    }

    @Nested
    class Monad {

        @Nested
        class Success {

            @Test
            void shouldRespectLeftIdentity() throws Throwable {
                final CheckedFunction<Integer, Try<Integer>> fn = x -> Try.success( x + 1);
                final Try<Integer> left = Try.success(10)
                        .flatMap(fn);
                final Try<Integer> right = fn.apply(10);

                assertEquals(Try.success(11), left);
                assertEquals(Try.success(11), right);
                assertEquals(left, right);
            }

            @Test
            void shouldRespectRightIdentity() {
                final Try<Integer> t = Try.success(10);
                final Try<Integer> result = t
                        .flatMap(Try::success);

                assertTrue(result.isSuccess());
                assertEquals(t, result);
            }

            @Test
            void shouldRespectAssociativity() {
                final CheckedFunction<Integer, Try<Integer>> fn = x -> Try.success(x + 1);
                final CheckedFunction<Integer, Try<Integer>> gn = x -> Try.success(x * 2);
                final Try<Integer> t = Try.success(10);
                final Try<Integer> left = t
                        .flatMap(fn)
                        .flatMap(gn);
                final Try<Integer> right = t
                        .flatMap(x -> fn.apply(x).flatMap(gn));

                assertEquals(Try.success(22),  left);
                assertEquals(Try.success(22), right);
                assertEquals(left, right);
            }
        }

        @Nested
        class Failure {

            @Test
            void shouldIgnoreRightIdentity() {
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> t = Try.failure(ex);
                final Try<Integer> result = t
                        .flatMap(Try::success);

                assertTrue(result.isFailure());
                assertEquals(t, result);
            }

            @Test
            void shouldRespectAssociativity() {
                final CheckedFunction<Integer, Try<Integer>> fn = x -> Try.success(x + 1);
                final CheckedFunction<Integer, Try<Integer>> gn = x -> Try.success(x * 2);
                final Throwable ex = new NullPointerException("boom");
                final Try<Integer> t = Try.failure(ex);
                final Try<Integer> left = t
                        .flatMap(fn)
                        .flatMap(gn);
                final Try<Integer> right = t
                        .flatMap(x -> fn.apply(x).flatMap(gn));

                assertEquals(Try.failure(ex),  left);
                assertEquals(Try.failure(ex), right);
                assertEquals(left, right);
            }
        }
    }
}
