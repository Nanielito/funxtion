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
