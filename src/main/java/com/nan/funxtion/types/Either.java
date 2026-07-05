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
 */
public sealed interface Either<L, R> permits Either.Left, Either.Right {

    // =========================================================
    // Factories
    // =========================================================

    /**
     * Creates a {@code Left} containing a non-null value.
     *
     * @throws NullPointerException if {@code value} is null
     */
    static <L, R> Either<L, R> left(final L value) {
        return new Left<>(Objects.requireNonNull(value, "left value must not be null"));
    }

    /**
     * Creates a {@code Right} containing a non-null value.
     *
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
     * @throws NullPointerException if the mapper or its result is null
     */
    <T> Either<L, T> map(Function<? super R, ? extends T> mapper);

    /**
     * Maps the right value to another {@code Either}; leaves {@code Left}
     * unchanged.
     *
     * @throws NullPointerException if the mapper or its result is null
     */
    <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, ? extends T>> mapper);

    /**
     * Maps the left value when this is {@code Left}; leaves {@code Right}
     * unchanged.
     *
     * @throws NullPointerException if the mapper or its result is null
     */
    <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper);

    /**
     * Maps whichever side is present.
     *
     * @throws NullPointerException if either mapper or the selected mapper result is null
     */
    <LL, RR> Either<LL, RR> bimap(Function<? super L, ? extends LL> leftMapper, Function<? super R, ? extends RR> rightMapper);

    // =========================================================
    // Folding
    // =========================================================

    <T> T fold(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper);

    // =========================================================
    // Extraction
    // =========================================================

    R getOrElse(R other);

    R getOrElseGet(Supplier<? extends R> supplier);

    Either<L, R> orElse(Either<L, ? extends R> other);

    // =========================================================
    // State
    // =========================================================

    boolean isLeft();

    default boolean isRight() {
        return !isLeft();
    }

    // =========================================================
    // Utilities
    // =========================================================

    Either<R, L> swap();

    /**
     * Converts {@code Right(value)} to {@code Some(value)} and {@code Left}
     * to {@code None}.
     */
    Option<R> toOption();

    /**
     * Converts {@code Right(value)} to {@code Success(value)} and {@code Left}
     * to {@code Failure(leftMapper.apply(value))}.
     *
     * @throws NullPointerException if {@code leftMapper} or its result is null
     */
    Try<R> toTry(Function<? super L, ? extends Throwable> leftMapper);

    // =========================================================
    // Implementation
    // =========================================================

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
