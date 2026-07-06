package com.nan.funxtion.types;

import com.nan.funxtion.types.functional.CheckedFunction;
import com.nan.funxtion.types.functional.CheckedSupplier;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents the result of a computation that may either succeed with a
 * non-null value or fail with a {@link Throwable}.
 *
 * <p>{@code Try.of} captures non-fatal throwables as {@code Failure}. Fatal
 * JVM errors are rethrown, and {@link InterruptedException} restores the
 * thread interrupt flag before being stored as a failure.
 *
 * @param <T> the success value type
 */
public sealed interface Try<T> permits Try.Success, Try.Failure {

    // =========================================================
    // Factory
    // =========================================================

    /**
     * Creates a successful {@code Try} with a non-null value.
     *
     * @param <T> the success value type
     * @param value the non-null success value to wrap
     * @return a {@code Success} containing {@code value}
     * @throws NullPointerException if {@code value} is null
     */
    static <T> Try<T> success(T value) {
        return new Success<>(Objects.requireNonNull(value, "success value must not be null"));
    }

    /**
     * Creates a failed {@code Try} with a non-null throwable.
     *
     * @param <T> the success value type
     * @param throwable the non-null throwable to wrap
     * @return a {@code Failure} containing {@code throwable}
     * @throws NullPointerException if {@code throwable} is null
     */
    static <T> Try<T> failure(Throwable throwable) {
        return new Failure<>(Objects.requireNonNull(throwable, "throwable must not be null"));
    }

    /**
     * Runs a checked supplier and captures non-fatal throwables as
     * {@code Failure}.
     *
     * @param <T> the success value type
     * @param supplier the supplier to run
     * @return {@code Success} with the supplied value, or {@code Failure} with a captured throwable
     * @throws NullPointerException if {@code supplier} or its success result is null
     */
    @SuppressWarnings("unchecked")
    static <T> Try<T> of(CheckedSupplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier must not be null");

        try {
            final T value = Objects.requireNonNull(supplier.get(), "supplier must not return null");
            return success(value);
        } catch (final Throwable throwable) {
            return (Try<T>) Try.failureFrom(throwable);
        }
    }

    // =========================================================
    // Transformations
    // =========================================================

    /**
     * Maps a successful value; failures pass through unchanged. Exceptions
     * thrown by the mapper are captured as {@code Failure}.
     *
     * @param <R> the mapped success value type
     * @param mapper the function used to map a successful value
     * @return a {@code Try} containing the mapped success or a failure
     * @throws NullPointerException if {@code mapper} is null
     */
    <R> Try<R> map(CheckedFunction<? super T, ? extends R> mapper);

    /**
     * Maps a successful value to another {@code Try}; failures pass through
     * unchanged. Exceptions thrown by the mapper are captured as {@code Failure}.
     *
     * @param <R> the mapped success value type
     * @param mapper the function used to map a successful value to a {@code Try}
     * @return the mapped {@code Try} for {@code Success}, or this failure
     * @throws NullPointerException if {@code mapper} is null
     */
    <R> Try<R> flatMap(CheckedFunction<? super T, ? extends Try<? extends R>> mapper);

    // =========================================================
    // Recovery
    // =========================================================

    /**
     * Converts a failure into a success when the recovery function succeeds.
     * Successful values pass through unchanged.
     *
     * @param recovery the function used to recover from a failure
     * @return this {@code Try} when successful, or the recovered result
     * @throws NullPointerException if {@code recovery} is null
     */
    Try<T> recover(CheckedFunction<? super Throwable, ? extends T> recovery);

    /**
     * Converts a failure into another {@code Try}. Successful values pass
     * through unchanged.
     *
     * @param recovery the function used to recover from a failure
     * @return this {@code Try} when successful, or the recovered {@code Try}
     * @throws NullPointerException if {@code recovery} is null
     */
    Try<T> recoverWith(CheckedFunction<? super Throwable, ? extends Try<? extends T>> recovery);

    // =========================================================
    // Folding
    // =========================================================

    /**
     * Folds this try by evaluating the mapper for the present state.
     *
     * @param <R> the folded result type
     * @param failureMapper the function used when this is {@code Failure}
     * @param successMapper the function used when this is {@code Success}
     * @return the folded result
     * @throws NullPointerException if {@code failureMapper} or {@code successMapper} is null
     */
    <R> R fold(
            Function<? super Throwable, ? extends R> failureMapper,
            Function<? super T, ? extends R> successMapper);

    // =========================================================
    // Extraction
    // =========================================================

    /**
     * Returns the success value or the provided fallback.
     *
     * @param other the fallback value returned when this is {@code Failure}
     * @return the success value, or {@code other}
     */
    T getOrElse(T other);

    /**
     * Returns the success value or gets a fallback from the supplier.
     *
     * @param supplier the fallback supplier used when this is {@code Failure}
     * @return the success value, or the supplied fallback
     * @throws NullPointerException if this is {@code Failure} and {@code supplier} is null
     */
    T getOrElseGet(Supplier<? extends T> supplier);

    /**
     * Returns this try when successful, otherwise returns {@code other}.
     *
     * @param other the fallback try used when this is {@code Failure}
     * @return this try when successful, otherwise {@code other}
     * @throws NullPointerException if {@code other} is null
     */
    Try<T> orElse(Try<? extends T> other);

    // =========================================================
    // State
    // =========================================================

    /**
     * Returns whether this try is a {@code Success}.
     *
     * @return {@code true} for {@code Success}, {@code false} for {@code Failure}
     */
    boolean isSuccess();

    /**
     * Returns whether this try is a {@code Failure}.
     *
     * @return {@code true} for {@code Failure}, {@code false} for {@code Success}
     */
    default boolean isFailure() {
        return !isSuccess();
    }

    // =========================================================
    // Conversion
    // =========================================================

    /**
     * Converts {@code Success(value)} to {@code Some(value)} and {@code Failure} to {@code None}.
     *
     * @return an option containing the success value, or {@code None}
     */
    Option<T> toOption();

    /**
     * Converts {@code Success(value)} to {@code Right(value)} and
     * {@code Failure(throwable)} to {@code Left(throwable)}.
     *
     * @return an either containing the throwable or success value
     */
    Either<Throwable, T> toEither();

    /**
     * Converts {@code Success(value)} to a single-value {@code ImmutableList} and
     * {@code Failure} to an empty {@code ImmutableList}.
     *
     * @return an immutable list containing zero or one value
     */
    ImmutableList<T> toList();

    // =========================================================
    // Internal utilities
    // =========================================================

    private static boolean isFatal(final Throwable throwable) {
        return throwable instanceof VirtualMachineError
                || throwable instanceof LinkageError;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(final Throwable throwable) throws E {
        throw (E) throwable;
    }

    private static Try<?> failureFrom(final Throwable throwable) {
        if (throwable instanceof InterruptedException)
            Thread.currentThread().interrupt();
        if (isFatal(throwable))
            sneakyThrow(throwable);
        return Try.failure(throwable);
    }

    // =========================================================
    // Implementation
    // =========================================================

    /**
     * A {@code Try} implementation containing a non-null success value.
     *
     * @param <T> the success value type
     */
    final class Success<T> implements Try<T> {

        private final T value;

        private Success(final T value) {
            this.value = Objects.requireNonNull(value, "success value must not be null");
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> Try<R> map(final CheckedFunction<? super T, ? extends R> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");

            try {
                final R result = Objects.requireNonNull(mapper.apply(value), "mapper must not return null");
                return Try.success(result);
            } catch (final Throwable throwable) {
                return (Try<R>) Try.failureFrom(throwable);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> Try<R> flatMap(final CheckedFunction<? super T, ? extends Try<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");

            try {
                final Try<? extends R> result = Objects.requireNonNull(mapper.apply(value), "flatMap mapper must not return null");
                return (Try<R>) result;
            } catch (final Throwable throwable) {
                return (Try<R>) Try.failureFrom(throwable);
            }
        }

        @Override
        public Try<T> recover(final CheckedFunction<? super Throwable, ? extends T> recovery) {
            Objects.requireNonNull(recovery, "recovery must not be null");
            return this;
        }

        @Override
        public Try<T> recoverWith(final CheckedFunction<? super Throwable, ? extends Try<? extends T>> recovery) {
            Objects.requireNonNull(recovery, "recovery must not be null");
            return this;
        }

        @Override
        public <R> R fold(
                final Function<? super Throwable, ? extends R> failureMapper,
                final Function<? super T, ? extends R> successMapper) {
            Objects.requireNonNull(failureMapper, "failureMapper must not be null");
            Objects.requireNonNull(successMapper, "successMapper must not be null");
            return successMapper.apply(value);
        }

        @Override
        public T getOrElse(final T other) {
            return value;
        }

        @Override
        public T getOrElseGet(final Supplier<? extends T> supplier) {
            return value;
        }

        @Override
        public Try<T> orElse(final Try<? extends T> other) {
            Objects.requireNonNull(other, "other must not be null");
            return this;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public Option<T> toOption() {
            return Option.some(value);
        }

        @Override
        public Either<Throwable, T> toEither() {
            return Either.right(value);
        }

        @Override
        public ImmutableList<T> toList() {
            return ImmutableList.of(value);
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Success<?> other && Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Success(%s)".formatted(value);
        }
    }

    /**
     * A {@code Try} implementation containing a non-null throwable.
     *
     * @param <T> the success value type
     */
    final class Failure<T> implements Try<T> {

        private final Throwable throwable;

        private Failure(final Throwable throwable) {
            this.throwable = Objects.requireNonNull(throwable, "throwable must not be null");
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> Try<R> map(final CheckedFunction<? super T, ? extends R> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return (Try<R>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> Try<R> flatMap(final CheckedFunction<? super T, ? extends Try<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return (Try<R>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Try<T> recover(final CheckedFunction<? super Throwable, ? extends T> recovery) {
            Objects.requireNonNull(recovery, "recovery must not be null");

            try {
                final T result = Objects.requireNonNull(recovery.apply(throwable), "recovery must not return null");
                return Try.success(result);
            } catch (final Throwable throwable) {
                return (Try<T>) Try.failureFrom(throwable);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Try<T> recoverWith(final CheckedFunction<? super Throwable, ? extends Try<? extends T>> recovery) {
            Objects.requireNonNull(recovery, "recovery must not be null");

            try {
                final Try<? extends T> result = Objects.requireNonNull(recovery.apply(throwable), "recovery must not return null");
                return (Try<T>) result;
            } catch (final Throwable throwable) {
                return (Try<T>) Try.failureFrom(throwable);
            }
        }

        @Override
        public <R> R fold(
                final Function<? super Throwable, ? extends R> failureMapper,
                final Function<? super T, ? extends R> successMapper) {
            Objects.requireNonNull(failureMapper, "failureMapper must not be null");
            Objects.requireNonNull(successMapper, "successMapper must not be null");
            return failureMapper.apply(throwable);
        }

        @Override
        public T getOrElse(final T other) {
            return other;
        }

        @Override
        public T getOrElseGet(final Supplier<? extends T> supplier) {
            Objects.requireNonNull(supplier, "supplier must not be null");
            return supplier.get();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Try<T> orElse(final Try<? extends T> other) {
            Objects.requireNonNull(other, "other must not be null");
            return (Try<T>) other;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public Option<T> toOption() {
            return Option.none();
        }

        @Override
        public Either<Throwable, T> toEither() {
            return Either.left(throwable);
        }

        @Override
        public ImmutableList<T> toList() {
            return ImmutableList.empty();
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Failure<?> other && Objects.equals(throwable, other.throwable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(throwable);
        }

        @Override
        public String toString() {
            return "Failure(%s)".formatted(throwable);
        }
    }
}
