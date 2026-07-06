package com.nan.funxtion.types;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a value of one of two non-null types.
 *
 * <p>By convention, {@code Left} carries an alternative or error value and
 * {@code Right} carries the successful value. Right-biased operations such as
 * {@link #map(Function)} and {@link #flatMap(Function)} only transform
 * {@code Right}; {@code Left} passes through unchanged.
 *
 * @param <L> the left value type
 * @param <R> the right value type
 */
public sealed interface Either<L, R> permits Either.Left, Either.Right {

    // =========================================================
    // Factories
    // =========================================================

    /**
     * Creates a {@code Left} containing a non-null value.
     *
     * @param <L> the left value type
     * @param <R> the right value type
     * @param value the non-null left value to wrap
     * @return a {@code Left} containing {@code value}
     * @throws NullPointerException if {@code value} is null
     */
    static <L, R> Either<L, R> left(final L value) {
        return new Left<>(Objects.requireNonNull(value, "left value must not be null"));
    }

    /**
     * Creates a {@code Right} containing a non-null value.
     *
     * @param <L> the left value type
     * @param <R> the right value type
     * @param value the non-null right value to wrap
     * @return a {@code Right} containing {@code value}
     * @throws NullPointerException if {@code value} is null
     */
    static <L, R> Either<L, R> right(final R value) {
        return new Right<>(Objects.requireNonNull(value, "right value must not be null"));
    }

    // =========================================================
    // Transformations
    // =========================================================

    /**
     * Maps the right value when this is {@code Right}; leaves {@code Left}
     * unchanged.
     *
     * @param <T> the mapped right value type
     * @param mapper the function used to map a right value
     * @return a {@code Right} with the mapped value, or this {@code Left}
     * @throws NullPointerException if the mapper or its result is null
     */
    <T> Either<L, T> map(Function<? super R, ? extends T> mapper);

    /**
     * Maps the right value to another {@code Either}; leaves {@code Left}
     * unchanged.
     *
     * @param <T> the mapped right value type
     * @param mapper the function used to map a right value
     * @return the mapped either for {@code Right}, or this {@code Left}
     * @throws NullPointerException if the mapper or its result is null
     */
    <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, ? extends T>> mapper);

    /**
     * Maps the left value when this is {@code Left}; leaves {@code Right}
     * unchanged.
     *
     * @param <T> the mapped left value type
     * @param mapper the function used to map a left value
     * @return a {@code Left} with the mapped value, or this {@code Right}
     * @throws NullPointerException if the mapper or its result is null
     */
    <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper);

    /**
     * Maps whichever side is present.
     *
     * @param <LL> the mapped left value type
     * @param <RR> the mapped right value type
     * @param leftMapper the function used to map a left value
     * @param rightMapper the function used to map a right value
     * @return an either containing the mapped present side
     * @throws NullPointerException if either mapper or the selected mapper result is null
     */
    <LL, RR> Either<LL, RR> bimap(Function<? super L, ? extends LL> leftMapper, Function<? super R, ? extends RR> rightMapper);

    // =========================================================
    // Folding
    // =========================================================

    /**
     * Folds this either by evaluating the mapper for the present side.
     *
     * @param <T> the folded result type
     * @param leftMapper the function used when this is {@code Left}
     * @param rightMapper the function used when this is {@code Right}
     * @return the folded result
     * @throws NullPointerException if {@code leftMapper} or {@code rightMapper} is null
     */
    <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper);

    // =========================================================
    // Extraction
    // =========================================================

    /**
     * Returns the right value or the provided fallback.
     *
     * @param other the fallback value returned when this is {@code Left}
     * @return the right value, or {@code other}
     */
    R getOrElse(R other);

    /**
     * Returns the right value or gets a fallback from the supplier.
     *
     * @param supplier the fallback supplier used when this is {@code Left}
     * @return the right value, or the supplied fallback
     * @throws NullPointerException if this is {@code Left} and {@code supplier} is null
     */
    R getOrElseGet(Supplier<? extends R> supplier);

    /**
     * Returns this either when it is {@code Right}, otherwise returns {@code other}.
     *
     * @param other the fallback either used when this is {@code Left}
     * @return this either when right, otherwise {@code other}
     * @throws NullPointerException if {@code other} is null
     */
    Either<L, R> orElse(Either<L, ? extends R> other);

    // =========================================================
    // State
    // =========================================================

    /**
     * Returns whether this either is a {@code Left}.
     *
     * @return {@code true} for {@code Left}, {@code false} for {@code Right}
     */
    boolean isLeft();

    /**
     * Returns whether this either is a {@code Right}.
     *
     * @return {@code true} for {@code Right}, {@code false} for {@code Left}
     */
    default boolean isRight() {
        return !isLeft();
    }

    // =========================================================
    // Utilities
    // =========================================================

    /**
     * Swaps the left and right sides.
     *
     * @return a {@code Right} when this is {@code Left}, or a {@code Left} when this is {@code Right}
     */
    Either<R, L> swap();

    /**
     * Converts {@code Right(value)} to {@code Some(value)} and {@code Left}
     * to {@code None}.
     *
     * @return an option containing the right value, or {@code None}
     */
    Option<R> toOption();

    /**
     * Converts {@code Right(value)} to {@code Success(value)} and {@code Left}
     * to {@code Failure(leftMapper.apply(value))}.
     *
     * @param leftMapper the function used to convert a left value into a throwable
     * @return a {@code Try} representing this either
     * @throws NullPointerException if {@code leftMapper} or its result is null
     */
    Try<R> toTry(Function<? super L, ? extends Throwable> leftMapper);

    // =========================================================
    // Implementation
    // =========================================================

    /**
     * An {@code Either} implementation containing a non-null left value.
     *
     * @param <L> the left value type
     * @param <R> the right value type
     */
    final class Left<L, R> implements Either<L, R> {

        private final L value;

        private Left(final L value) {
            this.value = Objects.requireNonNull(value, "left value must not be null");
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Either<L, T> map(final Function<? super R, ? extends T> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return (Either<L, T>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Either<L, T>  flatMap(final Function<? super R, ? extends Either<L, ? extends T>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return (Either<L, T>) this;
        }

        @Override
        public <T> Either<T, R> mapLeft(final Function<? super L, ? extends T> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            final T result = Objects.requireNonNull(mapper.apply(value), "mapLeft mapper must not return null");
            return Either.left(result);
        }

        @Override
        public <LL, RR> Either<LL, RR> bimap(
                final Function<? super L, ? extends LL> leftMapper,
                final Function<? super R, ? extends RR> rightMapper) {
            Objects.requireNonNull(leftMapper, "leftMapper must not be null");
            Objects.requireNonNull(rightMapper, "rightMapper must not be null");
            final LL result = Objects.requireNonNull(leftMapper.apply(value), "leftMapper must not return null");
            return Either.left(result);
        }

        @Override
        public <T> T fold(
                final Function<? super L, ? extends T> leftMapper,
                final Function<? super R, ? extends T> rightMapper) {
            Objects.requireNonNull(leftMapper, "leftMapper must not be null");
            Objects.requireNonNull(rightMapper, "rightMapper must not be null");
            return leftMapper.apply(value);
        }

        @Override
        public R getOrElse(final R other) {
            return other;
        }

        @Override
        public R getOrElseGet(final Supplier<? extends R> supplier) {
            Objects.requireNonNull(supplier, "supplier must not be null");
            return supplier.get();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Either<L, R> orElse(final Either<L, ? extends R> other) {
            Objects.requireNonNull(other, "other must not be null");
            return (Either<L, R>) other;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public Either<R, L> swap() {
            return Either.right(value);
        }

        @Override
        public Option<R> toOption() {
            return Option.none();
        }

        @Override
        public Try<R> toTry(final Function<? super L, ? extends Throwable> leftMapper) {
            Objects.requireNonNull(leftMapper, "leftMapper must not be null");
            return Try.failure(Objects.requireNonNull(leftMapper.apply(value), "leftMapper must not return null"));
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Left<?, ?> other && Objects.equals(this.value, other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Left(%s)".formatted(value);
        }
    }

    /**
     * An {@code Either} implementation containing a non-null right value.
     *
     * @param <L> the left value type
     * @param <R> the right value type
     */
    final class Right<L, R> implements Either<L, R> {

        private final R value;

        private Right(final R value) {
            this.value = Objects.requireNonNull(value, "right value must not be null");
        }

        @Override
        public <T> Either<L, T> map(final Function<? super R, ? extends T> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            final T result = Objects.requireNonNull(mapper.apply(value), "mapper must not return null");
            return Either.right(result);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Either<L, T> flatMap(final Function<? super R, ? extends Either<L, ? extends T>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            final Either<L, ? extends T> result = Objects.requireNonNull(mapper.apply(value), "mapper must not return null");
            return (Either<L, T>) result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Either<T, R> mapLeft(final Function<? super L, ? extends T> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return (Either<T, R>) this;
        }

        @Override
        public <LL, RR> Either<LL, RR> bimap(
                final Function<? super L, ? extends LL> leftMapper,
                final Function<? super R, ? extends RR> rightMapper) {
            Objects.requireNonNull(leftMapper, "leftMapper must not be null");
            Objects.requireNonNull(rightMapper, "rightMapper must not be null");
            final RR result = Objects.requireNonNull(rightMapper.apply(value), "rightMapper must not return null");
            return Either.right(result);
        }

        @Override
        public <T> T fold(
                final Function<? super L, ? extends T> leftMapper,
                final Function<? super R, ? extends T> rightMapper) {
            Objects.requireNonNull(leftMapper, "leftMapper must not be null");
            Objects.requireNonNull(rightMapper, "rightMapper must not be null");
            return rightMapper.apply(value);
        }

        @Override
        public R getOrElse(final R other) {
            return value;
        }

        @Override
        public R getOrElseGet(final Supplier<? extends R> supplier) {
            return value;
        }

        @Override
        public Either<L, R> orElse(final Either<L, ? extends R> other) {
            Objects.requireNonNull(other, "other must not be null");
            return this;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public Either<R, L> swap() {
            return Either.left(value);
        }

        @Override
        public Option<R> toOption() {
            return Option.some(value);
        }

        @Override
        public Try<R> toTry(final Function<? super L, ? extends Throwable> leftMapper) {
            Objects.requireNonNull(leftMapper, "leftMapper must not be null");
            return Try.success(value);
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Right<?, ?> other && Objects.equals(this.value, other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Right(%s)".formatted(value);
        }
    }
}
