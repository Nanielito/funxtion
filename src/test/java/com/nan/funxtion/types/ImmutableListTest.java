package com.nan.funxtion.types;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.nan.tuplex.Tuple;
import com.nan.tuplex.Tuples;

class ImmutableListTest {

    @Nested
    class ArrayImmutableList {

        @Nested
        class Empty {

            @Test
            void shouldCreateEmptyList() {
                final ImmutableList<Integer> list = ImmutableList.empty();

                assertEquals(0, list.size());
                assertEquals(List.of(), list.toList());
            }
        }

        @Nested
        class Of {

            @Test
            void shouldRejectNullArray() {
                assertThrows(
                        NullPointerException.class,
                        () -> ImmutableList.of((Integer[]) null));
            }

            @Test
            void shouldRejectNullElements() {
                assertThrows(
                        NullPointerException.class,
                        () -> ImmutableList.of(1, null, 3));
            }

            @Test
            void shouldCreateFromValues() {
                final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

                assertEquals(3, list.size());
                assertEquals(List.of(1, 2, 3), list.toList());
            }
        }

        @Nested
        class From {

            @Test
            void shouldRejectNullIterable() {
                assertThrows(
                        NullPointerException.class,
                        () -> ImmutableList.from((Iterable<Integer>) null));
            }

            @Test
            void shouldRejectNullElements() {
                final List<Integer> source = new ArrayList<>() {{
                    add(1);
                    add(null);
                    add(3);
                }};

                assertThrows(
                        NullPointerException.class,
                        () -> ImmutableList.from(source));
            }

            @Test
            void shouldCreateFromIterable() {
                final ImmutableList<Integer> list = ImmutableList.from(List.of(1, 2, 3));

                assertEquals(3, list.size());
                assertEquals(List.of(1, 2, 3), list.toList());
            }

            @Test
            void shouldMakeDefensiveCopy() {
                final List<Integer> source = new ArrayList<>() {{
                    add(1);
                    add(2);
                    add(3);
                }};
                final ImmutableList<Integer> list = ImmutableList.from(source);

                source.add(4);

                assertEquals(List.of(1, 2, 3), list.toList());
            }
        }

        @Nested
        class Immutability {

            @Test
            void shouldExposeUnmodifiableList() {
                final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

                assertThrows(
                        UnsupportedOperationException.class,
                        () -> list.toList().add(4));
            }

            @Test
            void shouldReturnArrayCopy() {
                final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
                final Object[] array = list.toArray();

                array[0] = 99;

                assertArrayEquals(new Object[] { 1, 2, 3 }, list.toArray());
            }
        }
    }

    @Nested
    class Size {

        @Test
        void shouldReturnZeroForEmptyList() {
            final ImmutableList<Integer> list = ImmutableList.empty();

            assertEquals(0, list.size());
        }

        @Test
        void shouldReturnSize() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertEquals(3, list.size());
        }
    }

    @Nested
    class IsEmpty {

        @Test
        void shouldReturnEmpty() {
            final ImmutableList<Integer> list = ImmutableList.empty();

            assertTrue(list.isEmpty());
        }

        @Test
        void shouldReturnNotEmpty() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertFalse(list.isEmpty());
        }
    }

    @Nested
    class NonEmpty {

        @Test
        void shouldReturnNonEmpty() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertTrue(list.nonEmpty());
        }

        @Test
        void shouldReturnEmpty() {
            final ImmutableList<Integer> list = ImmutableList.empty();

            assertFalse(list.nonEmpty());
        }
    }

    @Nested
    class Head {

        @Test
        void shouldReturnNoneForEmptyList() {
            final ImmutableList<Integer> list = ImmutableList.empty();

            assertSame(Option.none(), list.head());
        }

        @Test
        void shouldReturnFirstElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertEquals(Option.some(1), list.head());
        }
    }

    @Nested
    class Last {

        @Test
        void shouldReturnNoneForEmptyList() {
            final ImmutableList<Integer> list = ImmutableList.empty();

            assertSame(Option.none(), list.last());
        }

        @Test
        void shouldReturnLastElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertEquals(Option.some(3), list.last());
        }
    }

    @Nested
    class Get {

        @Test
        void shouldReturnNoneForIndexOutOfBounds() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertSame(Option.none(), list.get(-1));
            assertSame(Option.none(), list.get(3));
        }

        @Test
        void shouldReturnElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertEquals(Option.some(1), list.get(0));
            assertEquals(Option.some(2), list.get(1));
            assertEquals(Option.some(3), list.get(2));
        }
    }

    @Nested
    class Contains {

        @Test
        void shouldFailWhenLookingForNullElement() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .contains(null));
        }

        @Test
        void shouldReturnFalseForEmptyList() {
            final ImmutableList<Integer> list = ImmutableList.empty();

            assertFalse(list.contains(1));
        }

        @Test
        void shouldReturnFalseForNonElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertFalse(list.contains(4));
        }

        @Test
        void shouldReturnTrueForElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertTrue(list.contains(2));
        }
    }

    @Nested
    class Map {

        @Test
        void shouldFailWhenMapperIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .map(null));
        }

        @Test
        void shouldPropagateMapperException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.map(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldRejectNullMappedValue() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertThrows(
                    NullPointerException.class,
                    () -> list.map(v -> null));
        }

        @Test
        void shouldReturnEmptyListWhenMappingEmptyList() throws Throwable {
            final ImmutableList<Integer> list = ImmutableList.<Integer>empty()
                    .map(v -> v * 2);

            assertEquals(List.of(), list.toList());
        }

        @Test
        void shouldMap() throws Throwable {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3)
                    .map(v -> v * 2);

            assertEquals(List.of(2, 4, 6), list.toList());
        }
    }

    @Nested
    class Filter {

        @Test
        void shouldFailWhenPredicateIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .filter(null));
        }

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.filter(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnEmptyListWhenFilteringEmptyList() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .filter(v -> v % 2 == 0);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldReturnEmptyListWhenNoElementsMatchPredicate() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .filter(v -> v > 10);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldFilter() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3, 4)
                    .filter(v -> v % 2 == 0);

            assertEquals(List.of(2, 4), result.toList());
        }
    }

    @Nested
    class FlatMap {

        @Test
        void shouldFailWhenMapperIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .flatMap(null));
        }

        @Test
        void shouldRejectNullMappedList() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertThrows(
                    NullPointerException.class,
                    () -> list.flatMap(v -> null));
        }

        @Test
        void shouldPropagateMapperException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.flatMap(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnEmptyListWhenMappingEmptyList() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .flatMap(v -> ImmutableList.of(v, v * 10));

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldSkipEmptyMappedLists() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .flatMap(v -> v % 2 == 0 ? ImmutableList.of(v) : ImmutableList.empty());

            assertEquals(List.of(2), result.toList());
        }

        @Test
        void shouldFlatMap() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .flatMap(v -> ImmutableList.of(v, v * 10));

            assertEquals(List.of(1, 10, 2, 20, 3, 30), result.toList());
        }
    }

    @Nested
    class Partition {

        @Test
        void shouldFailWhenPredicateIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .partition(null));
        }

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.partition(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldPartitionEmptyList() throws Throwable {
            final Tuple result = ImmutableList.<Integer>empty()
                    .partition(v -> v > 10);

            assertEquals(ImmutableList.empty(), result.get(1));
            assertEquals(ImmutableList.empty(), result.get(2));
        }

        @Test
        void shouldPartitionValuesPreservingOrder() throws Throwable {
            final Tuple result = ImmutableList.of(1, 2, 3, 4, 5)
                    .partition(v -> v % 2 == 0);

            assertEquals(ImmutableList.of(2, 4), result.get(1));
            assertEquals(ImmutableList.of(1, 3, 5), result.get(2));
        }

        @Test
        void shouldPutAllValuesInMatchingWhenAllMatch() throws Throwable {
            final Tuple result = ImmutableList.of(2, 4, 6)
                    .partition(v -> v % 2 == 0);

            assertEquals(ImmutableList.of(2, 4, 6), result.get(1));
            assertEquals(ImmutableList.empty(), result.get(2));
        }

        @Test
        void shouldPutAllValuesInNonMatchingWhenNoneMatch() throws Throwable {
            final Tuple result = ImmutableList.of(1, 3, 5)
                    .partition(v -> v % 2 == 0);

            assertEquals(ImmutableList.empty(), result.get(1));
            assertEquals(ImmutableList.of(1, 3, 5), result.get(2));
        }
    }

    @Nested
    class GroupBy {

        @Test
        void shouldFailWhenClassifierIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .groupBy(null));
        }

        @Test
        void shouldFailWhenClassifierResultIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .groupBy(v -> null));
        }

        @Test
        void shouldPropagateClassifierException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.groupBy(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnEmptyMapWhenListIsEmpty() throws Throwable {
            final java.util.Map<String, ImmutableList<Integer>> result = ImmutableList.<Integer>empty()
                    .groupBy(v -> v % 2 == 0 ? "even" : "odd");

            assertEquals(java.util.Map.of(), result);
        }

        @Test
        void shouldGroupValuesPreservingValueOrder() throws Throwable {
            final java.util.Map<String, ImmutableList<Integer>> result = ImmutableList.of(1, 2, 3, 4, 5)
                    .groupBy(v -> v % 2 == 0 ? "even" : "odd");

            assertEquals(ImmutableList.of(1, 3, 5), result.get("odd"));
            assertEquals(ImmutableList.of(2, 4), result.get("even"));
        }

        @Test
        void shouldPreserveKeyInsertionOrder() throws Throwable {
            final java.util.Map<String, ImmutableList<Integer>> result = ImmutableList.of(1, 2, 3, 4)
                    .groupBy(v -> v % 2 == 0 ? "even" : "odd");

            assertEquals(List.of("odd", "even"), new ArrayList<>(result.keySet()));
        }

        @Test
        void shouldReturnUnmodifiableMap() throws Throwable {
            final java.util.Map<String, ImmutableList<Integer>> result = ImmutableList.of(1, 2, 3)
                    .groupBy(v -> v % 2 == 0 ? "even" : "odd");

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> result.put("other", ImmutableList.empty()));
        }
    }

    @Nested
    class Find {

        @Test
        void shouldFailWhenPredicateIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .find(null));
        }

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.find(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnNoneWhenCheckingEmptyList() throws Throwable {
            final Option<Integer> result = ImmutableList.<Integer>empty()
                    .find(v -> v > 10);

            assertSame(Option.none(), result);
        }

        @Test
        void shouldReturnNoneWhenNoElementsMatchPredicate() throws Throwable {
            final Option<Integer> result = ImmutableList.of(1, 2, 3)
                    .find(v -> v > 10);

            assertSame(Option.none(), result);
        }

        @Test
        void shouldFind() throws Throwable {
            final Option<Integer> result = ImmutableList.of(1, 2, 3, 4)
                    .find(v -> v % 2 == 0);

            assertEquals(Option.some(2), result);
        }
    }

    @Nested
    class Any {

        @Test
        void shouldFailWhenPredicateIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .any(null));
        }

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.any(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnFalseWhenCheckingEmptyList() throws Throwable {
            final boolean result = ImmutableList.<Integer>empty()
                    .any(v -> v > 10);

            assertFalse(result);
        }

        @Test
        void shouldReturnFalseWhenNoElementsMatchPredicate() throws Throwable {
            final boolean result = ImmutableList.of(1, 2, 3)
                    .any(v -> v > 10);

            assertFalse(result);
        }

        @Test
        void shouldReturnTrueWhenAnElementMatchesPredicate() throws Throwable {
            final boolean result = ImmutableList.of(1, 2, 3, 4)
                    .any(v -> v % 2 == 0);

            assertTrue(result);
        }
    }

    @Nested
    class All {

        @Test
        void shouldFailWhenPredicateIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .all(null));
        }

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.all(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnTrueWhenCheckingEmptyList() throws Throwable {
            final boolean result = ImmutableList.<Integer>empty()
                    .all(v -> v % 2 == 0);

            assertTrue(result);
        }

        @Test
        void shouldReturnFalseWhenAllElementsDoNotMatchPredicate() throws Throwable {
            final boolean result = ImmutableList.of(1, 2, 3, 4)
                    .all(v -> v % 2 == 0);

            assertFalse(result);
        }

        @Test
        void shouldReturnTrueWhenAllElementsMatchPredicate() throws Throwable {
            final boolean result = ImmutableList.of(2, 4, 6, 8)
                    .all(v -> v % 2 == 0);

            assertTrue(result);
        }
    }

    @Nested
    class None {

        @Test
        void shouldFailWhenPredicateIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .none(null));
        }

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.none(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnTrueWhenCheckingEmptyList() throws Throwable {
            final boolean result = ImmutableList.<Integer>empty()
                    .none(v -> v > 10);

            assertTrue(result);
        }

        @Test
        void shouldReturnTrueWhenNoElementsMatchPredicate() throws Throwable {
            final boolean result = ImmutableList.of(1, 2, 3)
                    .none(v -> v > 10);

            assertTrue(result);
        }

        @Test
        void shouldReturnFalseWhenAnElementMatchesPredicate() throws Throwable {
            final boolean result = ImmutableList.of(1, 2, 3, 4)
                    .none(v -> v % 2 == 0);

            assertFalse(result);
        }
    }

    @Nested
    class Count {

        @Test
        void shouldFailWhenPredicateIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .count(null));
        }

        @Test
        void shouldPropagatePredicateException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.count(v -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnZeroWhenCountingEmptyList() throws Throwable {
            final int result = ImmutableList.<Integer>empty()
                    .count(v -> v > 10);

            assertEquals(0, result);
        }

        @Test
        void shouldReturnZeroWhenNoElementsMatchPredicate() throws Throwable {
            final int result = ImmutableList.of(1, 2, 3)
                    .count(v -> v > 10);

            assertEquals(0, result);
        }

        @Test
        void shouldCountMatchingElements() throws Throwable {
            final int result = ImmutableList.of(1, 2, 3, 4)
                    .count(v -> v % 2 == 0);

            assertEquals(2, result);
        }
    }

    @Nested
    class FoldLeft {

        @Test
        void shouldFailWhenFunctionIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .foldLeft(0, null));
        }

        @Test
        void shouldPropagateFunctionException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.foldLeft(0, (a, b) -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnInitialValueWhenFoldingEmptyList() throws Throwable {
            final int result = ImmutableList.<Integer>empty()
                    .foldLeft(0, Integer::sum);

            assertEquals(0, result);
        }

        @Test
        void shouldAllowNullInitialValue() throws Throwable {
            final String result = ImmutableList.of(1, 2, 3)
                    .foldLeft(null, (acc, value) -> acc == null ? value.toString() : acc + value);

            assertEquals("123", result);
        }

        @Test
        void shouldFoldFromLeft() throws Throwable {
            final int result = ImmutableList.of(1, 2, 3)
                    .foldLeft(0, Integer::sum);

            assertEquals(6, result);
        }
    }

    @Nested
    class ScanLeft {

        @Test
        void shouldFailWhenInitialIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .scanLeft(null, Integer::sum));
        }

        @Test
        void shouldFailWhenFunctionIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .scanLeft(0, null));
        }

        @Test
        void shouldFailWhenFunctionResultIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .scanLeft(0, (a, b) -> null));
        }

        @Test
        void shouldPropagateFunctionException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.scanLeft(0, (a, b) -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnInitialValueWhenScanningEmptyList() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .scanLeft(0, Integer::sum);

            assertEquals(List.of(0), result.toList());
        }

        @Test
        void shouldScanFromLeft() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .scanLeft(0, Integer::sum);

            assertEquals(List.of(0, 1, 3, 6), result.toList());
        }
    }

    @Nested
    class ScanRight {

        @Test
        void shouldFailWhenInitialIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .scanRight(null, Integer::sum));
        }

        @Test
        void shouldFailWhenFunctionIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .scanRight(0, null));
        }

        @Test
        void shouldFailWhenFunctionResultIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .scanRight(0, (a, b) -> null));
        }

        @Test
        void shouldPropagateFunctionException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.scanRight(0, (a, b) -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnInitialValueWhenScanningEmptyList() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .scanRight(0, Integer::sum);

            assertEquals(List.of(0), result.toList());
        }

        @Test
        void shouldScanFromRight() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .scanRight(0, Integer::sum);

            assertEquals(List.of(6, 5, 3, 0), result.toList());
        }
    }

    @Nested
    class Reduce {

        @Test
        void shouldFailWhenFunctionIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .reduce(null));
        }

        @Test
        void shouldFailWhenReduceToNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .reduce((left, right) -> null));
        }

        @Test
        void shouldReturnNoneForEmptyList() throws Throwable {
            final Option<Integer> result = ImmutableList.<Integer>empty()
                    .reduce(Integer::sum);

            assertSame(Option.none(), result);
        }

        @Test
        void shouldPropagateFunctionException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.reduce((left, right) -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnSingleElementForSingleElementList() throws Throwable {
            final Option<Integer> result = ImmutableList.of(1)
                    .reduce(Integer::sum);

            assertEquals(Option.some(1), result);
        }

        @Test
        void shouldReduce() throws Throwable {
            final Option<Integer> result = ImmutableList.of(1, 2, 3)
                    .reduce(Integer::sum);

            assertEquals(Option.some(6), result);
        }
    }

    @Nested
    class Take {

        @Test
        void shouldReturnEmptyListWhenCountIsNegative() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .take(-1);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldReturnEmptyListWhenCountIsZero() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .take(0);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldReturnSameListWhenCountIsGreaterThanListSize() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.take(10);

            assertSame(list, result);
        }

        @Test
        void shouldReturnSameListWhenCountIsEqualToListSize() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.take(3);

            assertSame(list, result);
        }

        @Test
        void shouldReturnSubListWhenCountIsLessThanListSize() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.take(2);

            assertEquals(List.of(1, 2), result.toList());
        }
    }

    @Nested
    class Drop {

        @Test
        void shouldReturnSameListWhenCountIsNegative() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.drop(-1);

            assertSame(list, result);
        }

        @Test
        void shouldReturnSameListWhenCountIsZero() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.drop(0);

            assertSame(list, result);
        }

        @Test
        void shouldReturnEmptyListWhenCountIsGreaterThanListSize() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.drop(10);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldReturnEmptyListWhenCountIsEqualToListSize() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.drop(3);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldReturnSubListWhenCountIsLessThanListSize() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.drop(2);

            assertEquals(List.of(3), result.toList());
        }
    }

    @Nested
    class Append {

        @Test
        void shouldFailWhenValueIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .append(null));
        }

        @Test
        void shouldAppendValueToEmptyList() {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .append(1);

            assertEquals(List.of(1), result.toList());
        }

        @Test
        void shouldAppendValueToNonEmptyList() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .append(4);

            assertEquals(List.of(1, 2, 3, 4), result.toList());
        }

        @Test
        void shouldNotModifyOriginalList() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.append(4);

            assertEquals(List.of(1, 2, 3), list.toList());
            assertEquals(List.of(1, 2, 3, 4), result.toList());
        }
    }

    @Nested
    class Prepend {

        @Test
        void shouldFailWhenValueIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .prepend(null));
        }

        @Test
        void shouldPrependValueToEmptyList() {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .prepend(1);

            assertEquals(List.of(1), result.toList());
        }

        @Test
        void shouldPrependValueToNonEmptyList() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .prepend(0);

            assertEquals(List.of(0, 1, 2, 3), result.toList());
        }

        @Test
        void shouldNotModifyOriginalList() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.prepend(0);

            assertEquals(List.of(1, 2, 3), list.toList());
            assertEquals(List.of(0, 1, 2, 3), result.toList());
        }
    }

    @Nested
    class Concat {

        @Test
        void shouldFailWhenListIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .concat(null));
        }

        @Test
        void shouldConcatWithEmptyList() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .concat(ImmutableList.empty());

            assertEquals(List.of(1, 2, 3), result.toList());
        }

        @Test
        void shouldConcatEmptyListWithNonEmptyList() {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .concat(ImmutableList.of(1, 2, 3));

            assertEquals(List.of(1, 2, 3), result.toList());
        }

        @Test
        void shouldConcatLists() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .concat(ImmutableList.of(4, 5, 6));

            assertEquals(List.of(1, 2, 3, 4, 5, 6), result.toList());
        }

        @Test
        void shouldNotModifyOriginalLists() {
            final ImmutableList<Integer> left = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> right = ImmutableList.of(4, 5, 6);
            final ImmutableList<Integer> result = left.concat(right);

            assertEquals(List.of(1, 2, 3), left.toList());
            assertEquals(List.of(4, 5, 6), right.toList());
            assertEquals(List.of(1, 2, 3, 4, 5, 6), result.toList());
        }
    }

    @Nested
    class Zip {

        @Test
        void shouldFailWhenOtherIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .zip(null));
        }

        @Test
        void shouldReturnEmptyListWhenThisListIsEmpty() {
            final ImmutableList<Tuple> result = ImmutableList.<Integer>empty()
                    .zip(ImmutableList.of("a", "b", "c"));

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldReturnEmptyListWhenOtherListIsEmpty() {
            final ImmutableList<Tuple> result = ImmutableList.of(1, 2, 3)
                    .zip(ImmutableList.<Integer>empty());

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldZipListsWithSameSize() {
            final ImmutableList<Tuple> result = ImmutableList.of(1, 2, 3)
                    .zip(ImmutableList.of("a", "b", "c"));

            assertEquals(
                    List.of(
                            Tuples.of(1, "a"),
                            Tuples.of(2, "b"),
                            Tuples.of(3, "c")),
                    result.toList());
        }

        @Test
        void shouldZipUntilShortestListWhenThisListIsShorter() {
            final ImmutableList<Tuple> result = ImmutableList.of(1, 2)
                    .zip(ImmutableList.of("a", "b", "c"));

            assertEquals(
                    List.of(
                            Tuples.of(1, "a"),
                            Tuples.of(2, "b")),
                    result.toList());
        }

        @Test
        void shouldZipUntilShortestListWhenOtherListIsShorter() {
            final ImmutableList<Tuple> result = ImmutableList.of(1, 2, 3)
                    .zip(ImmutableList.of("a", "b"));

            assertEquals(
                    List.of(
                            Tuples.of(1, "a"),
                            Tuples.of(2, "b")),
                    result.toList());
        }
    }

    @Nested
    class ZipWith {

        @Test
        void shouldFailWhenOtherIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .zipWith(null, Integer::sum));
        }

        @Test
        void shouldFailWhenFunctionIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .zipWith(ImmutableList.of("a", "b", "c"), null));
        }

        @Test
        void shouldFailWhenFunctionReturnsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .zipWith(ImmutableList.of("a", "b", "c"), (left, right) -> null));
        }

        @Test
        void shouldPropagateFunctionException() {
            final RuntimeException ex = new RuntimeException("boom");
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> list.zipWith(ImmutableList.of("a", "b", "c"), (left, right) -> { throw ex; }));

            assertSame(ex, thrown);
        }

        @Test
        void shouldReturnEmptyListWhenThisListIsEmpty() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.<Integer>empty()
                    .zipWith(ImmutableList.of(10, 20, 30), Integer::sum);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldReturnEmptyListWhenOtherListIsEmpty() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .zipWith(ImmutableList.empty(), Integer::sum);

            assertEquals(List.of(), result.toList());
        }

        @Test
        void shouldZipListsWithSameSize() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .zipWith(ImmutableList.of(10, 20, 30), Integer::sum);

            assertEquals(List.of(11, 22, 33), result.toList());
        }

        @Test
        void shouldZipUntilShortestListWhenThisListIsShorter() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2)
                    .zipWith(ImmutableList.of(10, 20, 30), Integer::sum);

            assertEquals(List.of(11, 22), result.toList());
        }

        @Test
        void shouldZipUntilShortestListWhenOtherListIsShorter() throws Throwable {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .zipWith(ImmutableList.of(10, 20), Integer::sum);

            assertEquals(List.of(11, 22), result.toList());
        }
    }

    @Nested
    class Reverse {

        @Test
        void shouldReturnSameListWhenEmpty() {
            final ImmutableList<Integer> list = ImmutableList.empty();
            final ImmutableList<Integer> result = list.reverse();

            assertSame(list, result);
        }

        @Test
        void shouldReturnSameListWhenSingleElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1);
            final ImmutableList<Integer> result = list.reverse();

            assertSame(list, result);
        }

        @Test
        void shouldReverse() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 3)
                    .reverse();

            assertEquals(List.of(3, 2, 1), result.toList());
        }

        @Test
        void shouldNotModifyOriginalList() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.reverse();

            assertEquals(List.of(1, 2, 3), list.toList());
            assertEquals(List.of(3, 2, 1), result.toList());
        }
    }

    @Nested
    class Sort {

        @Test
        void shouldFailWhenComparatorIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .sort(null));
        }

        @Test
        void shouldReturnSameListWhenEmpty() {
            final ImmutableList<Integer> list = ImmutableList.empty();
            final ImmutableList<Integer> result = list.sort(Integer::compareTo);

            assertSame(list, result);
        }

        @Test
        void shouldReturnSameListWhenSingleElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1);
            final ImmutableList<Integer> result = list.sort(Integer::compareTo);

            assertSame(list, result);
        }

        @Test
        void shouldSort() {
            final ImmutableList<Integer> result = ImmutableList.of(3, 2, 1)
                    .sort(Integer::compareTo);

            assertEquals(List.of(1, 2, 3), result.toList());
        }

        @Test
        void shouldNotModifyOriginalList() {
            final ImmutableList<Integer> list = ImmutableList.of(3, 2, 1);
            final ImmutableList<Integer> result = list.sort(Integer::compareTo);

            assertEquals(List.of(3, 2, 1), list.toList());
            assertEquals(List.of(1, 2, 3), result.toList());
        }
    }

    @Nested
    class Distinct {

        @Test
        void shouldReturnSameListWhenEmpty() {
            final ImmutableList<Integer> list = ImmutableList.empty();
            final ImmutableList<Integer> result = list.distinct();

            assertSame(list, result);
        }

        @Test
        void shouldReturnSameListWhenSingleElement() {
            final ImmutableList<Integer> list = ImmutableList.of(1);
            final ImmutableList<Integer> result = list.distinct();

            assertSame(list, result);
        }

        @Test
        void shouldReturnSameListWhenAllElementsAreDistinct() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> result = list.distinct();

            assertSame(list, result);
        }

        @Test
        void shouldReturnDistinctValues() {
            final ImmutableList<Integer> result = ImmutableList.of(1, 2, 2, 3)
                    .distinct();

            assertEquals(List.of(1, 2, 3), result.toList());
        }
    }

    @Nested
    class ToList {

        @Test
        void shouldReturnEmptyListForEmptyList() {
            final ImmutableList<Integer> list = ImmutableList.empty();
            final List<Integer> result = list.toList();

            assertEquals(List.of(), result);
        }

        @Test
        void shouldReturnSameListForNonEmptyList() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final List<Integer> result = list.toList();

            assertSame(list.toList(), result);
        }
    }

    @Nested
    class ToArray {

        @Test
        void shouldReturnEmptyArrayForEmptyList() {
            final ImmutableList<Integer> list = ImmutableList.empty();
            final Object[] result = list.toArray();

            assertArrayEquals(List.of().toArray(), result);
        }

        @Test
        void shouldReturnArrayWithValues() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final Object[] result = list.toArray();

            assertArrayEquals(list.toArray(), result);
        }
    }

    @Nested
    class MkString {

        @Test
        void shouldFailWhenSeparatorIsNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> ImmutableList.of(1, 2, 3)
                            .mkString(null));
        }

        @Test
        void shouldReturnEmptyStringWhenListIsEmpty() {
            final ImmutableList<Integer> list = ImmutableList.empty();
            final String result = list.mkString(",");

            assertEquals("", result);
        }

        @Test
        void shouldJoinValuesUsingSeparator() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);
            final String result = list.mkString(",");

            assertEquals("1,2,3", result);
        }

        @Test
        void shouldJoinSingleValueWithoutSeparator() {
            final ImmutableList<Integer> list = ImmutableList.of(1);
            final String result = list.mkString(",");

            assertEquals("1", result);
        }
    }

    @Nested
    class Equals {

        @Test
        void shouldCompareValues() {
            final ImmutableList<Integer> listA = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> listB = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> listC = ImmutableList.of(1, 2, 4);

            assertEquals(listA, listB);
            assertNotEquals(listA, listC);
            assertNotEquals(listB, listC);
        }
    }

    @Nested
    class HashCode {

        @Test
        void shouldGetHashCode() {
            final ImmutableList<Integer> listA = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> listB = ImmutableList.of(1, 2, 3);
            final ImmutableList<Integer> listC = ImmutableList.of(1, 2, 4);

            assertEquals(listA.hashCode(), listB.hashCode());
            assertNotEquals(listA.hashCode(), listC.hashCode());
            assertNotEquals(listB.hashCode(), listC.hashCode());
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldConvertToString() {
            final ImmutableList<Integer> list = ImmutableList.of(1, 2, 3);

            assertEquals("ImmutableList([1, 2, 3])", list.toString());
        }
    }
}
