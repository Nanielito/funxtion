package com.nan.funxtion.types;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents an optional non-null value.
 *
 * <p>{@code Some} always contains a non-null value, while {@code None}
 * represents absence. Transformations are only applied to {@code Some};
 * {@code None} skips them and remains empty.
 */
public sealed interface Option<T> permits Option.Some, Option.None {

    // =========================================================
    // Factory methods
    // =========================================================

    /**
     * Creates an {@code Option} containing a non-null value.
     *
     * @throws NullPointerException if {@code value} is null
     */
    static <T> Option<T> some(final T value) {
        return new Some<>(Objects.requireNonNull(value, "value must not be null"));
    }

    /**
     * Returns the singleton empty {@code Option}.
     */
    @SuppressWarnings("unchecked")
    static <T> Option<T> none() {
        return (Option<T>) None.INSTANCE;
    }

    /**
     * Converts a nullable value into {@code Some(value)} or {@code None}.
     */
    static <T> Option<T> ofNullable(final T value) {
        return value == null ? none() : some(value);
    }

    // =========================================================
    // Transformations
    // =========================================================

    /**
     * Maps a present value and converts a null mapping result to {@code None}.
     */
    <R> Option<R> map(Function<? super T, ? extends R> mapper);

    /**
     * Maps a present value to another {@code Option}.
     *
     * @throws NullPointerException if the mapper returns null
     */
    <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> mapper);

    Option<T> filter(Predicate<? super T> predicate);

    // =========================================================
    // Folding
    // =========================================================

    <R> R fold(Supplier<? extends R> ifEmpty, Function<? super T, ? extends R> ifDefined);

    // =========================================================
    // Extraction
    // =========================================================

    T getOrElse(T other);

    T getOrElseGet(Supplier<? extends T> supplier);

    Option<T> orElse(Option<? extends T> other);

    Option<T> orElseGet(Supplier<? extends Option<? extends T>> supplier);

    // =========================================================
    // State
    // =========================================================

    boolean isDefined();

    default boolean isEmpty() {
        return !isDefined();
    }

    // =========================================================
    // Conversion
    // =========================================================

    /**
     * Converts {@code Some(value)} to {@code Right(value)} and {@code None}
     * to {@code Left(leftSupplier.get())}.
     *
     * @throws NullPointerException if {@code leftSupplier} or its result is null
     */
    <L> Either<L, T> toEither(Supplier<? extends L> leftSupplier);

    // =========================================================
    // Implementation
    // =========================================================

    final class Some<T> implements Option<T> {

        private final T value;

        private Some(final T value) {
            this.value = Objects.requireNonNull(value, "value must not be null");
        }

        @Override
        public <R> Option<R> map(final Function<? super T, ? extends R> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return Option.ofNullable(mapper.apply(value));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R> Option<R> flatMap(final Function<? super T, ? extends Option<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            final Option<? extends R> result = Objects.requireNonNull(mapper.apply(value), "flatMap mapper must not return null");
            return (Option<R>) result;
        }

        @Override
        public Option<T> filter(final Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate, "predicate must not be null");
            return predicate.test(value) ? this : Option.none();
        }

        @Override
        public <R> R fold(final Supplier<? extends R> ifEmpty, final Function<? super T, ? extends R> ifDefined) {
            Objects.requireNonNull(ifEmpty, "ifEmpty must not be null");
            Objects.requireNonNull(ifDefined, "ifDefined must not be null");
            return ifDefined.apply(value);
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
        public Option<T> orElse(final Option<? extends T> other) {
            Objects.requireNonNull(other, "other must not be null");
            return this;
        }

        @Override
        public Option<T> orElseGet(final Supplier<? extends Option<? extends T>> supplier) {
            Objects.requireNonNull(supplier, "supplier must not be null");
            return this;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public <L> Either<L, T> toEither(final Supplier<? extends L> leftSupplier) {
            Objects.requireNonNull(leftSupplier, "leftSupplier must not be null");
            return Either.right(value);
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Some<?> other && Objects.equals(this.value, other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Some(%s)".formatted(value);
        }
    }

    final class None<T> implements Option<T> {

        private static final None<?> INSTANCE = new None<>();

        private None() {
        }

        @Override
        public <R> Option<R> map(final Function<? super T, ? extends R> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return Option.none();
        }

        @Override
        public <R> Option<R> flatMap(final Function<? super T, ? extends Option<? extends R>> mapper) {
            Objects.requireNonNull(mapper, "mapper must not be null");
            return Option.none();
        }

        @Override
        public Option<T> filter(final Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate, "predicate must not be null");
            return this;
        }

        @Override
        public <R> R fold(final Supplier<? extends R> ifEmpty, final Function<? super T, ? extends R> ifDefined) {
            Objects.requireNonNull(ifEmpty, "ifEmpty must not be null");
            Objects.requireNonNull(ifDefined, "ifDefined must not be null");
            return ifEmpty.get();
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
        public Option<T> orElse(final Option<? extends T> other) {
            Objects.requireNonNull(other, "other must not be null");
            return (Option<T>) other;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Option<T> orElseGet(final Supplier<? extends Option<? extends T>> supplier) {
            Objects.requireNonNull(supplier, "supplier must not be null");
            final Option<? extends T> result = Objects.requireNonNull(supplier.get(), "supplier must not be null");
            return (Option<T>) result;
        }

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public <L> Either<L, T> toEither(final Supplier<? extends L> leftSupplier) {
            Objects.requireNonNull(leftSupplier, "leftSupplier must not be null");
            return Either.left(Objects.requireNonNull(leftSupplier.get(), "leftSupplier must not return null"));
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof None<?>;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "None";
        }
    }
}
