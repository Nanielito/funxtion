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
 *
 * @param <T> the type of the optional value
 */
public sealed interface Option<T> permits Option.Some, Option.None {

    // =========================================================
    // Factory methods
    // =========================================================

    /**
     * Creates an {@code Option} containing a non-null value.
     *
     * @param <T> the value type
     * @param value the non-null value to wrap
     * @return a {@code Some} containing {@code value}
     * @throws NullPointerException if {@code value} is null
     */
    static <T> Option<T> some(final T value) {
        return new Some<>(Objects.requireNonNull(value, "value must not be null"));
    }

    /**
     * Returns the singleton empty {@code Option}.
     *
     * @param <T> the value type
     * @return the empty {@code Option}
     */
    @SuppressWarnings("unchecked")
    static <T> Option<T> none() {
        return (Option<T>) None.INSTANCE;
    }

    /**
     * Converts a nullable value into {@code Some(value)} or {@code None}.
     *
     * @param <T> the value type
     * @param value the nullable value to wrap
     * @return {@code Some} when {@code value} is non-null, otherwise {@code None}
     */
    static <T> Option<T> ofNullable(final T value) {
        return value == null ? none() : some(value);
    }

    // =========================================================
    // Transformations
    // =========================================================

    /**
     * Maps a present value and converts a null mapping result to {@code None}.
     *
     * @param <R> the mapped value type
     * @param mapper the function used to map a present value
     * @return {@code Some} with the mapped value, or {@code None}
     * @throws NullPointerException if {@code mapper} is null
     */
    <R> Option<R> map(Function<? super T, ? extends R> mapper);

    /**
     * Maps a present value to another {@code Option}.
     *
     * @param <R> the mapped value type
     * @param mapper the function used to map a present value
     * @return the mapped option for {@code Some}, or {@code None}
     * @throws NullPointerException if {@code mapper} or its result is null
     */
    <R> Option<R> flatMap(Function<? super T, ? extends Option<? extends R>> mapper);

    /**
     * Keeps a present value only when it satisfies the predicate.
     *
     * @param predicate the predicate used to test a present value
     * @return this option when empty or matching, otherwise {@code None}
     * @throws NullPointerException if {@code predicate} is null
     */
    Option<T> filter(Predicate<? super T> predicate);

    // =========================================================
    // Folding
    // =========================================================

    /**
     * Folds this option by evaluating one branch for {@code None} and another for {@code Some}.
     *
     * @param <R> the folded result type
     * @param ifEmpty the supplier used when this option is empty
     * @param ifDefined the function used when this option contains a value
     * @return the folded result
     * @throws NullPointerException if {@code ifEmpty} or {@code ifDefined} is null
     */
    <R> R fold(Supplier<? extends R> ifEmpty, Function<? super T, ? extends R> ifDefined);

    // =========================================================
    // Extraction
    // =========================================================

    /**
     * Returns the present value or the provided fallback.
     *
     * @param other the fallback value returned when this option is empty
     * @return the present value, or {@code other}
     */
    T getOrElse(T other);

    /**
     * Returns the present value or gets a fallback from the supplier.
     *
     * @param supplier the fallback supplier used when this option is empty
     * @return the present value, or the supplied fallback
     * @throws NullPointerException if this option is empty and {@code supplier} is null
     */
    T getOrElseGet(Supplier<? extends T> supplier);

    /**
     * Returns this option when defined, otherwise returns {@code other}.
     *
     * @param other the fallback option used when this option is empty
     * @return this option when defined, otherwise {@code other}
     * @throws NullPointerException if {@code other} is null
     */
    Option<T> orElse(Option<? extends T> other);

    /**
     * Returns this option when defined, otherwise gets a fallback option from the supplier.
     *
     * @param supplier the fallback option supplier used when this option is empty
     * @return this option when defined, otherwise the supplied option
     * @throws NullPointerException if {@code supplier} or its result is null
     */
    Option<T> orElseGet(Supplier<? extends Option<? extends T>> supplier);

    // =========================================================
    // State
    // =========================================================

    /**
     * Returns whether this option contains a value.
     *
     * @return {@code true} for {@code Some}, {@code false} for {@code None}
     */
    boolean isDefined();

    /**
     * Returns whether this option is empty.
     *
     * @return {@code true} for {@code None}, {@code false} for {@code Some}
     */
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
     * @param <L> the left value type
     * @param leftSupplier the supplier used to create the left value for {@code None}
     * @return an {@code Either} representing this option
     * @throws NullPointerException if {@code leftSupplier} or its result is null
     */
    <L> Either<L, T> toEither(Supplier<? extends L> leftSupplier);

    /**
     * Converts {@code Some(value)} to a single-value {@code ImmutableList} and
     * {@code None} to an empty {@code ImmutableList}.
     *
     * @return an immutable list containing zero or one value
     */
    ImmutableList<T> toList();

    /**
     * Converts {@code Some(value)} to {@code Success(value)} and {@code None}
     * to {@code Failure(throwableSupplier.get())}.
     *
     * @param throwableSupplier the supplier used to create the failure for {@code None}
     * @return a {@code Try} representing this option
     * @throws NullPointerException if {@code throwableSupplier} or its result is null
     */
    Try<T> toTry(Supplier<? extends Throwable> throwableSupplier);

    // =========================================================
    // Implementation
    // =========================================================

    /**
     * An {@code Option} implementation containing a non-null value.
     *
     * @param <T> the value type
     */
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
        public ImmutableList<T> toList() {
            return ImmutableList.of(value);
        }

        @Override
        public Try<T> toTry(final Supplier<? extends Throwable> throwableSupplier) {
            Objects.requireNonNull(throwableSupplier, "throwableSupplier must not be null");
            return Try.success(value);
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

    /**
     * The empty {@code Option} implementation.
     *
     * @param <T> the value type
     */
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
        public ImmutableList<T> toList() {
            return ImmutableList.empty();
        }

        @Override
        public Try<T> toTry(final Supplier<? extends Throwable> throwableSupplier) {
            Objects.requireNonNull(throwableSupplier, "throwableSupplier must not be null");
            return Try.failure(Objects.requireNonNull(throwableSupplier.get(), "throwableSupplier must not return null"));
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
