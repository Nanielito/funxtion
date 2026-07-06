package com.nan.funxtion.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.nan.funxtion.types.functional.CheckedBiFunction;
import com.nan.funxtion.types.functional.CheckedFunction;
import com.nan.funxtion.types.functional.CheckedPredicate;
import com.nan.tuplex.Tuple;
import com.nan.tuplex.Tuples;

/**
 * An immutable, null-rejecting list with functional transformation, search,
 * reduction, slicing, and conversion operations.
 *
 * <p>Instances defensively copy incoming values and expose an unmodifiable
 * {@link List} through {@link #toList()}.
 *
 * @param <T> the type of values stored in the list
 */
public sealed interface ImmutableList<T> permits ImmutableList.ArrayImmutableList {

    // =========================================================
    // Factories
    // =========================================================

    /**
     * Creates an empty immutable list.
     *
     * @param <T> the element type of the empty list
     * @return an empty immutable list
     */
    static <T> ImmutableList<T> empty() {
        return new ArrayImmutableList<>(List.of());
    }

    /**
     * Creates an immutable list from the provided values.
     *
     * @param <T> the element type of the list
     * @param values the values to copy into the list
     * @return an immutable list containing the provided values
     * @throws NullPointerException if the array or any value is null
     */
    @SafeVarargs
    static <T> ImmutableList<T> of(final T... values) {
        Objects.requireNonNull(values, "values must not be null");
        return new ArrayImmutableList<>(List.of(values));
    }

    /**
     * Creates an immutable list from an iterable, making a defensive copy.
     *
     * @param <T> the element type of the list
     * @param iterable the values to copy into the list
     * @return an immutable list containing the iterable values
     * @throws NullPointerException if the iterable or any value is null
     */
    static <T> ImmutableList<T> from(final Iterable<? extends T> iterable) {
        Objects.requireNonNull(iterable, "iterable must not be null");
        final List<T> values = new ArrayList<>();
        for (final T value : iterable)
            values.add(value);
        return new ArrayImmutableList<>(List.copyOf(values));
    }

    // =========================================================
    // Queries
    // =========================================================

    /**
     * Returns the number of values in this list.
     *
     * @return the list size
     */
    int size();

    /**
     * Returns whether this list contains no values.
     *
     * @return {@code true} when this list is empty
     */
    boolean isEmpty();

    /**
     * Returns whether this list contains at least one value.
     *
     * @return {@code true} when this list is not empty
     */
    default boolean nonEmpty() {
        return !isEmpty();
    }

    /**
     * Returns the first value, if present.
     *
     * @return {@code Some} with the first value, or {@code None} when empty
     */
    Option<T> head();

    /**
     * Returns the last value, if present.
     *
     * @return {@code Some} with the last value, or {@code None} when empty
     */
    Option<T> last();

    /**
     * Returns {@code None} when the index is out of bounds.
     *
     * @param index the zero-based index to read
     * @return {@code Some} with the value at {@code index}, or {@code None}
     */
    Option<T> get(int index);

    /**
     * Returns whether the non-null value is present.
     *
     * @param value the non-null value to search for
     * @return {@code true} when this list contains {@code value}
     * @throws NullPointerException if {@code value} is null
     */
    boolean contains(T value);

    // =========================================================
    // Transformations
    // =========================================================

    /**
     * Maps each value, preserving order.
     *
     * @param <R> the mapped value type
     * @param mapper the function used to map each value
     * @return an immutable list containing the mapped values
     * @throws NullPointerException if the mapper or any mapped value is null
     * @throws Throwable if {@code mapper} throws
     */
    <R> ImmutableList<R> map(CheckedFunction<? super T, ? extends R> mapper) throws Throwable;

    /**
     * Keeps values that satisfy the predicate, preserving order.
     *
     * @param predicate the predicate used to test each value
     * @return an immutable list containing only matching values
     * @throws NullPointerException if {@code predicate} is null
     * @throws Throwable if {@code predicate} throws
     */
    ImmutableList<T> filter(CheckedPredicate<? super T> predicate) throws Throwable;

    /**
     * Maps each value to an immutable list and concatenates the results.
     *
     * @param <R> the mapped value type
     * @param mapper the function used to map each value to an immutable list
     * @return an immutable list containing the concatenated mapped values
     * @throws NullPointerException if the mapper or any mapped list is null
     * @throws Throwable if {@code mapper} throws
     */
    <R> ImmutableList<R> flatMap(CheckedFunction<? super T, ? extends ImmutableList<? extends R>> mapper) throws Throwable;

    // =========================================================
    // Partitioning
    // =========================================================

    /**
     * Splits this list into matching and non-matching values, preserving order.
     *
     * @param predicate the predicate used to classify each value
     * @return a tuple containing the matching values first and non-matching values second
     * @throws NullPointerException if {@code predicate} is null
     * @throws Throwable if {@code predicate} throws
     */
    Tuple partition(CheckedPredicate<? super T> predicate) throws Throwable;

    // =========================================================
    // Grouping
    // =========================================================

    /**
     * Groups values by a non-null classifier result, preserving the order in
     * which group keys first appear.
     *
     * @param <K> the group key type
     * @param classifier the function used to compute each value's group key
     * @return an unmodifiable map from group keys to immutable lists of grouped values
     * @throws NullPointerException if {@code classifier} or any classifier result is null
     * @throws Throwable if {@code classifier} throws
     */
    <K> Map<K, ImmutableList<T>> groupBy(CheckedFunction<? super T, ? extends K> classifier) throws Throwable;

    // =========================================================
    // Search
    // =========================================================

    /**
     * Finds the first value that satisfies the predicate.
     *
     * @param predicate the predicate used to test each value
     * @return {@code Some} with the first matching value, or {@code None}
     * @throws NullPointerException if {@code predicate} is null
     * @throws Throwable if {@code predicate} throws
     */
    Option<T> find(CheckedPredicate<? super T> predicate) throws Throwable;

    /**
     * Returns whether at least one value satisfies the predicate.
     *
     * @param predicate the predicate used to test each value
     * @return {@code true} when any value matches
     * @throws NullPointerException if {@code predicate} is null
     * @throws Throwable if {@code predicate} throws
     */
    boolean any(CheckedPredicate<? super T> predicate) throws Throwable;

    /**
     * Returns true when every value matches, including for an empty list.
     *
     * @param predicate the predicate used to test each value
     * @return {@code true} when every value matches
     * @throws NullPointerException if {@code predicate} is null
     * @throws Throwable if {@code predicate} throws
     */
    boolean all(CheckedPredicate<? super T> predicate) throws Throwable;

    /**
     * Returns whether no values satisfy the predicate.
     *
     * @param predicate the predicate used to test each value
     * @return {@code true} when no value matches
     * @throws NullPointerException if {@code predicate} is null
     * @throws Throwable if {@code predicate} throws
     */
    default boolean none(final CheckedPredicate<? super T> predicate) throws Throwable {
        return !any(predicate);
    }

    /**
     * Counts values that satisfy the predicate.
     *
     * @param predicate the predicate used to test each value
     * @return the number of matching values
     * @throws NullPointerException if {@code predicate} is null
     * @throws Throwable if {@code predicate} throws
     */
    int count(CheckedPredicate<? super T> predicate) throws Throwable;

    // =========================================================
    // Reduction
    // =========================================================

    /**
     * Folds from left to right. The initial accumulator may be null.
     *
     * @param <R> the accumulator and result type
     * @param initial the initial accumulator value
     * @param function the function used to combine the accumulator and each value
     * @return the final accumulated value
     * @throws NullPointerException if {@code function} is null
     * @throws Throwable if {@code function} throws
     */
    <R> R foldLeft(R initial, CheckedBiFunction<? super R, ? super T, ? extends R> function) throws Throwable;

    /**
     * Folds from right to left. The initial accumulator may be null.
     *
     * @param <R> the accumulator and result type
     * @param initial the initial accumulator value
     * @param function the function used to combine each value and the accumulator
     * @return the final accumulated value
     * @throws NullPointerException if {@code function} is null
     * @throws Throwable if {@code function} throws
     */
    <R> R foldRight(R initial, CheckedBiFunction<? super T, ? super R, ? extends R> function) throws Throwable;

    /**
     * Folds from left to right, returning every intermediate accumulator including
     * the initial value.
     *
     * @param <R> the accumulator and scanned value type
     * @param initial the initial accumulator value
     * @param function the function used to combine the accumulator and each value
     * @return an immutable list containing the initial value and each intermediate accumulator
     * @throws NullPointerException if {@code initial}, {@code function}, or any intermediate accumulator value is null
     * @throws Throwable if {@code function} throws
     */
    <R> ImmutableList<R> scanLeft(R initial, CheckedBiFunction<? super R, ? super T, ? extends R> function) throws Throwable;

    /**
     * Folds from right to left, returning every intermediate accumulator including
     * the initial value.
     *
     * @param <R> the accumulator and scanned value type
     * @param initial the initial accumulator value
     * @param function the function used to combine each value and the accumulator
     * @return an immutable list containing each intermediate accumulator and the initial value
     * @throws NullPointerException if {@code initial}, {@code function}, or any accumulated value is null
     * @throws Throwable if {@code function} throws
     */
    <R> ImmutableList<R> scanRight(R initial, CheckedBiFunction<? super T, ? super R, ? extends R> function) throws Throwable;

    /**
     * Reduces this list from left to right, returning {@code None} for an
     * empty list.
     *
     * @param function the function used to combine accumulated values
     * @return {@code Some} with the final reduced value, or {@code None} when empty
     * @throws NullPointerException if the function is null or the final reduced value is null
     * @throws Throwable if {@code function} throws
     */
    Option<T> reduce(CheckedBiFunction<? super T, ? super T, ? extends T> function) throws Throwable;

    /**
     * Reduces this list from right to left, returning {@code None} for an
     * empty list.
     *
     * @param function the function used to combine accumulated values
     * @return {@code Some} with the final reduced value, or {@code None} when empty
     * @throws NullPointerException if the function is null or the final reduced value is null
     * @throws Throwable if {@code function} throws
     */
    Option<T> reduceRight(CheckedBiFunction<? super T, ? super T, ? extends T> function) throws Throwable;

    // =========================================================
    // Slicing
    // =========================================================

    /**
     * Returns up to the first {@code count} values.
     *
     * @param count the maximum number of values to keep
     * @return an immutable list containing the kept prefix
     */
    ImmutableList<T> take(int count);

    /**
     * Drops up to the first {@code count} values.
     *
     * @param count the number of values to skip
     * @return an immutable list containing the remaining suffix
     */
    ImmutableList<T> drop(int count);

    // =========================================================
    // Windows
    // =========================================================

    /**
     * Returns fixed-size sliding windows separated by {@code step} values.
     *
     * @param size the size of each window
     * @param step the distance between the start of consecutive windows
     * @return an immutable list containing each complete window
     * @throws IllegalArgumentException if {@code size} or {@code step} is less than or equal to zero
     */
    ImmutableList<ImmutableList<T>> sliding(int size, int step);

    /**
     * Returns fixed-size sliding windows with a step of {@code 1}.
     *
     * @param size the size of each window
     * @return an immutable list containing each complete window
     * @throws IllegalArgumentException if {@code size} is less than or equal to zero
     */
    ImmutableList<ImmutableList<T>> sliding(int size);

    // =========================================================
    // Combination
    // =========================================================

    /**
     * Returns a new list with {@code value} added at the end.
     *
     * @param value the value to add
     * @return a new immutable list ending with {@code value}
     * @throws NullPointerException if {@code value} is null
     */
    ImmutableList<T> append(T value);

    /**
     * Returns a new list with {@code value} added at the beginning.
     *
     * @param value the value to add
     * @return a new immutable list starting with {@code value}
     * @throws NullPointerException if {@code value} is null
     */
    ImmutableList<T> prepend(T value);

    /**
     * Returns a new list containing this list followed by {@code other}.
     *
     * @param other the list to append after this list
     * @return a new immutable list containing both lists in order
     * @throws NullPointerException if {@code other} is null
     */
    ImmutableList<T> concat(ImmutableList<? extends T> other);

    /**
     * Combines this list with {@code other} into tuples, stopping at the shortest list.
     *
     * @param <U> the value type of the other list
     * @param other the list to combine with this list
     * @return an immutable list of tuples containing paired values
     * @throws NullPointerException if {@code other} is null
     */
    <U> ImmutableList<Tuple> zip(ImmutableList<? extends U> other);

    /**
     * Combines this list with {@code other} using {@code function}, stopping at the shortest list.
     *
     * @param <U> the value type of the other list
     * @param <R> the combined value type
     * @param other the list to combine with this list
     * @param function the function used to combine paired values
     * @return an immutable list of combined values
     * @throws NullPointerException if {@code other}, {@code function}, or any combined value is null
     * @throws Throwable if {@code function} throws
     */
    <U, R> ImmutableList<R> zipWith(ImmutableList<? extends U> other, CheckedBiFunction<? super T, ? super U, ? extends R> function) throws Throwable;

    // =========================================================
    // Ordering
    // =========================================================

    /**
     * Returns a new list with the values in reverse order.
     *
     * @return an immutable list with values in reverse order
     */
    ImmutableList<T> reverse();

    /**
     * Returns a new list sorted by the provided comparator.
     *
     * @param comparator the comparator used to sort values
     * @return an immutable list with sorted values
     * @throws NullPointerException if {@code comparator} is null
     */
    ImmutableList<T> sort(Comparator<? super T> comparator);

    // =========================================================
    // Distinct
    // =========================================================

    /**
     * Returns a new list with duplicate values removed, keeping first occurrences.
     *
     * @return an immutable list containing distinct values in first-occurrence order
     */
    ImmutableList<T> distinct();

    // =========================================================
    // Conversion
    // =========================================================

    /**
     * Returns an unmodifiable list view of this immutable list.
     *
     * @return an unmodifiable list containing this list's values
     */
    List<T> toList();

    /**
     * Returns a new array containing this list's values.
     *
     * @return an array containing this list's values
     */
    Object[] toArray();

    /**
     * Joins values into a string using the provided separator.
     *
     * @param separator the separator placed between values
     * @return the joined string
     * @throws NullPointerException if {@code separator} is null
     */
    String mkString(String separator);

    // =========================================================
    // Implementation
    // =========================================================

    /**
     * Default immutable list implementation backed by an unmodifiable list copy.
     *
     * @param <T> the type of values stored in the list
     */
    final class ArrayImmutableList<T> implements ImmutableList<T> {

        private final List<T> values;

        private ArrayImmutableList(final List<T> values) {
            this.values = List.copyOf(Objects.requireNonNull(values, "values must not be null"));
        }
        
        @Override
        public int size() {
            return values.size();
        }

        @Override
        public boolean isEmpty() {
            return values.isEmpty();
        }

        @Override
        public Option<T> head() {
            return values.isEmpty() ? Option.none() : Option.some(values.get(0));
        }

        @Override
        public Option<T> last() {
            return values.isEmpty() ? Option.none() : Option.some(values.get(values.size() - 1));
        }

        @Override
        public Option<T> get(final int index) {
            return index < 0 || index >= values.size() ? Option.none() : Option.some(values.get(index));
        }

        @Override
        public boolean contains(final T value) {
            return values.contains(Objects.requireNonNull(value, "value must not be null"));
        }

        @Override
        public <R> ImmutableList<R> map(final CheckedFunction<? super T, ? extends R> mapper) throws Throwable {
            Objects.requireNonNull(mapper, "mapper must not be null");
            final List<R> mapped = new ArrayList<>(values.size());
            for (final T value : values)
                mapped.add(mapper.apply(value));
            return new ArrayImmutableList<>(mapped);
        }

        @Override
        public ImmutableList<T> filter(final CheckedPredicate<? super T> predicate) throws Throwable {
            Objects.requireNonNull(predicate, "predicate must not be null");
            final List<T> filtered = new ArrayList<>();
            for (final T value : values)
                if (predicate.test(value))
                    filtered.add(value);
            return new ArrayImmutableList<>(filtered);
        }

        @Override
        public <R> ImmutableList<R> flatMap(final CheckedFunction<? super T, ? extends ImmutableList<? extends R>> mapper) throws Throwable {
            Objects.requireNonNull(mapper, "mapper must not be null");
            final List<R> flattened = new ArrayList<>();
            for (final T value : values) {
                final ImmutableList<? extends R> mapped = Objects.requireNonNull(mapper.apply(value), "flatMap mapper must not return null");
                flattened.addAll(mapped.toList());
            }
            return new ArrayImmutableList<>(flattened);
        }

        @Override
        public Tuple partition(final CheckedPredicate<? super T> predicate) throws Throwable {
            Objects.requireNonNull(predicate, "predicate must not be null");
            final List<T> matching = new ArrayList<>();
            final List<T> nonMatching = new ArrayList<>();
            for (final T value : values) {
                if (predicate.test(value))
                    matching.add(value);
                else
                    nonMatching.add(value);
            }
            return Tuples.of(new ArrayImmutableList<>(matching), new ArrayImmutableList<>(nonMatching));
        }

        @Override
        public <K> Map<K, ImmutableList<T>> groupBy(final CheckedFunction<? super T, ? extends K> classifier) throws Throwable {
            Objects.requireNonNull(classifier, "classifier must not be null");
            final Map<K, List<T>> groups = new LinkedHashMap<>();
            for (final T value : values) {
                final K key = Objects.requireNonNull(classifier.apply(value), "classifier must not return null");
                groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
            }

            final Map<K, ImmutableList<T>> grouped = new LinkedHashMap<>();
            for (final Map.Entry<K, List<T>> entry : groups.entrySet())
                grouped.put(entry.getKey(), new ArrayImmutableList<>(entry.getValue()));

            return Collections.unmodifiableMap(grouped);
        }

        @Override
        public Option<T> find(final CheckedPredicate<? super T> predicate) throws Throwable {
            Objects.requireNonNull(predicate, "predicate must not be null");
            for (final T value : values) {
                if (predicate.test(value))
                    return Option.some(value);
            }
            return Option.none();
        }

        @Override
        public boolean any(final CheckedPredicate<? super T> predicate) throws Throwable {
            Objects.requireNonNull(predicate, "predicate must not be null");
            for (final T value : values) {
                if (predicate.test(value))
                    return true;
            }
            return false;
        }

        @Override
        public boolean all(final CheckedPredicate<? super T> predicate) throws Throwable {
            Objects.requireNonNull(predicate, "predicate must not be null");
            for (final T value : values) {
                if (!predicate.test(value))
                    return false;
            }
            return true;
        }

        @Override
        public int count(final CheckedPredicate<? super T> predicate) throws Throwable {
            Objects.requireNonNull(predicate, "predicate must not be null");
            int count = 0;
            for (final T value : values)
                if (predicate.test(value))
                    count++;
            return count;
        }

        @Override
        public <R> R foldLeft(final R initial, final CheckedBiFunction<? super R, ? super T, ? extends R> function) throws Throwable {
            Objects.requireNonNull(function, "function must not be null");
            R result = initial;
            for (final T value : values)
                result = function.apply(result, value);
            return result;
        }

        @Override
        public <R> R foldRight(final R initial, final CheckedBiFunction<? super T, ? super R, ? extends R> function) throws Throwable {
            Objects.requireNonNull(function, "function must not be null");
            R result = initial;
            for (int i = values.size() - 1; i >= 0; i--)
                result = function.apply(values.get(i), result);
            return result;
        }

        @Override
        public <R> ImmutableList<R> scanLeft(final R initial, final CheckedBiFunction<? super R, ? super T, ? extends R> function) throws Throwable {
            Objects.requireNonNull(initial, "initial must not be null");
            Objects.requireNonNull(function, "function must not be null");
            final List<R> scanned = new ArrayList<>(values.size() + 1);
            R accumulator = initial;
            scanned.add(accumulator);
            for (final T value : values) {
                accumulator = Objects.requireNonNull(
                        function.apply(accumulator, value), "scanLeft function must not return null");
                scanned.add(accumulator);
            }
            return new ArrayImmutableList<>(scanned);
        }

        @Override
        public <R> ImmutableList<R> scanRight(final R initial, final CheckedBiFunction<? super T, ? super R, ? extends R> function) throws Throwable {
            Objects.requireNonNull(initial, "initial must not be null");
            Objects.requireNonNull(function, "function must not be null");
            final List<R> scanned = new ArrayList<>(values.size() + 1);
            R accumulator = initial;
            scanned.add(accumulator);
            for (int i = values.size() - 1; i >= 0; i--) {
                final T value = values.get(i);
                accumulator = Objects.requireNonNull(
                        function.apply(value, accumulator), "scanRight function must not return null");
                scanned.add(accumulator);
            }
            Collections.reverse(scanned);
            return new ArrayImmutableList<>(scanned);
        }

        @Override
        public Option<T> reduce(final CheckedBiFunction<? super T, ? super T, ? extends T> function) throws Throwable {
            Objects.requireNonNull(function, "function must not be null");
            if (values.isEmpty())
                return Option.none();
            T result = values.get(0);
            for (int i = 1; i < values.size(); i++)
                result = function.apply(result, values.get(i));
            return Option.some(result);
        }

        @Override
        public Option<T> reduceRight(final CheckedBiFunction<? super T, ? super T, ? extends T> function) throws Throwable {
            Objects.requireNonNull(function, "function must not be null");
            if (values.isEmpty())
                return Option.none();
            T result = values.get(values.size() - 1);
            for (int i = values.size() - 2; i >= 0; i--)
                result = function.apply(values.get(i), result);
            return Option.some(result);
        }

        @Override
        public ImmutableList<T> take(final int count) {
            if (count <= 0)
                return ImmutableList.empty();
            if (count >= values.size())
                return this;
            return new ArrayImmutableList<>(values.subList(0, count));
        }

        @Override
        public ImmutableList<T> drop(final int count) {
            if (count <= 0)
                return this;
            if (count >= values.size())
                return ImmutableList.empty();
            return new ArrayImmutableList<>(values.subList(count, values.size()));
        }

        @Override
        public ImmutableList<ImmutableList<T>> sliding(final int size, final int step) {
            if (size <= 0)
                throw new IllegalArgumentException("size must be greater than zero");
            if (step <= 0)
                throw new IllegalArgumentException("step must be greater than zero");
            if (values.isEmpty() || size > values.size())
                return ImmutableList.empty();
            final List<ImmutableList<T>> windows = new ArrayList<>();
            for (int start = 0; start + size <= values.size(); start += step)
                windows.add(new ArrayImmutableList<>(values.subList(start, start + size)));
            return new ArrayImmutableList<>(windows);
        }

        @Override
        public ImmutableList<ImmutableList<T>> sliding(final int size) {
            return sliding(size, 1);
        }

        @Override
        public ImmutableList<T> append(final T value) {
            Objects.requireNonNull(value, "value must not be null");
            final List<T> appended = new ArrayList<>(values.size() + 1);
            appended.addAll(values);
            appended.add(value);
            return new ArrayImmutableList<>(appended);
        }

        @Override
        public ImmutableList<T> prepend(final T value) {
            Objects.requireNonNull(value, "value must not be null");
            final List<T> prepended = new ArrayList<>(values.size() + 1);
            prepended.add(value);
            prepended.addAll(values);
            return new ArrayImmutableList<>(prepended);
        }

        @Override
        public ImmutableList<T> concat(final ImmutableList<? extends T> other) {
            Objects.requireNonNull(other, "other must not be null");
            final List<T> concatenated = new ArrayList<>(values.size() + other.size());
            concatenated.addAll(values);
            concatenated.addAll(other.toList());
            return new ArrayImmutableList<>(concatenated);
        }

        @Override
        public <U> ImmutableList<Tuple> zip(final ImmutableList<? extends U> other) {
            Objects.requireNonNull(other, "other must not be null");
            final List<? extends U> otherValues = other.toList();
            final int size = Math.min(values.size(), otherValues.size());
            final List<Tuple> zipped = new ArrayList<>(size);
            for (int i = 0; i < size; i++)
                zipped.add(Tuples.of(values.get(i), otherValues.get(i)));
            return new ArrayImmutableList<>(zipped);
        }

        @Override
        public <U, R> ImmutableList<R> zipWith(
                final ImmutableList<? extends U> other,
                final CheckedBiFunction<? super T, ? super U, ? extends R> function) throws Throwable {
            Objects.requireNonNull(other, "other must not be null");
            Objects.requireNonNull(function, "function must not be null");
            final List<? extends U> otherValues = other.toList();
            final int size = Math.min(values.size(), otherValues.size());
            final List<R> zipped = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                final R value = Objects.requireNonNull(
                        function.apply(values.get(i), otherValues.get(i)), "zipWith function must not return null");
                zipped.add(value);
            }
            return new ArrayImmutableList<>(zipped);
        }

        @Override
        public ImmutableList<T> reverse() {
            if (values.size() <= 1)
                return this;
            final List<T> reversed = new ArrayList<>(values);
            Collections.reverse(reversed);
            return new ArrayImmutableList<>(reversed);
        }

        @Override
        public ImmutableList<T> sort(final Comparator<? super T> comparator) {
            Objects.requireNonNull(comparator, "comparator must not be null");
            if (values.size() <= 1)
                return this;
            final List<T> sorted = new ArrayList<>(values);
            sorted.sort(comparator);
            return new ArrayImmutableList<>(sorted);
        }

        @Override
        public ImmutableList<T> distinct() {
            if (values.size() <= 1)
                return this;
            final List<T> distinct = new ArrayList<>(new LinkedHashSet<>(values));
            if (distinct.size() == values.size())
                return this;
            return new ArrayImmutableList<>(distinct);
        }

        @Override
        public List<T> toList() {
            return values;
        }

        @Override
        public Object[] toArray() {
            return values.toArray();
        }

        @Override
        public String mkString(final String separator) {
            Objects.requireNonNull(separator, "separator must not be null");
            return values.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(separator));
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof ArrayImmutableList<?> other && Objects.equals(values, other.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(values);
        }

        @Override
        public String toString() {
            return "ImmutableList(%s)".formatted(values);
        }
    }
}
