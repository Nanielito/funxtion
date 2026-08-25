# Funxtion

Funxtion is a small functional library for Java. It provides core algebraic
and collection types for writing explicit, composable code:

- `Option<T>` for optional non-null values.
- `Either<L, R>` for computations that can produce one of two values.
- `Try<T>` for computations that can fail with a non-fatal `Throwable`.
- `ImmutableList<T>` for immutable, null-rejecting list operations.
- `Checked*` functional interfaces for lambdas that can throw `Throwable`.

Funxtion uses [Tuplex](https://github.com/nanielito/tuplex) for tuple-backed
operations such as `ImmutableList.partition(...)`.

## Requirements

- Java 21+ (published bytecode targets Java 21; CI builds on Java 21 and 25)

## Installation

Artifacts are published to Maven Central and mirrored to GitHub Packages.

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.nanielito:funxtion:0.2.0")
}
```

The same coordinates are available from the `nanielito/maven-packages` GitHub
Packages registry for authenticated GitHub users.

## Build And Test

```bash
./gradlew test
./gradlew javadoc
```

Build artifacts:

```bash
./gradlew build
```

## Contributing

Open an issue before proposing a change, then send a pull request following
the workflow in [CONTRIBUTING.md](CONTRIBUTING.md).

This repository can also be paired with
[WorkForge](https://github.com/Nanielito/workforge#quick-start) to keep
requirements and change requests organized. WorkForge can be installed and
initialized locally when contributors want a structured flow for planning items,
agent context, and implementation checklists. Its workspace is local-only and
ignored by Git.

## Examples

### Option

```java
import com.nan.funxtion.types.Option;

Option<Integer> result = Option.some(10)
        .map(value -> value * 2)
        .filter(value -> value > 10);

int value = result.getOrElse(0);
```

`Option.some(...)` rejects `null`. Use `Option.ofNullable(...)` when working
with nullable values.

### Either

```java
import com.nan.funxtion.types.Either;

Either<String, Integer> result = Either.<String, Integer>right(10)
        .map(value -> value * 2);

String message = result.fold(
        error -> "Error: " + error,
        value -> "Value: " + value);
```

`Either` is right-biased: `map` and `flatMap` transform `Right` values and
leave `Left` values unchanged.

### Try

```java
import com.nan.funxtion.types.Try;

Try<Integer> result = Try.of(() -> Integer.parseInt("42"))
        .map(value -> value * 2)
        .recover(error -> 0);
```

`Try.of(...)` captures non-fatal throwables as `Failure`. Fatal JVM errors are
rethrown, and interrupted computations restore the thread interrupt flag.

### ImmutableList

```java
import com.nan.funxtion.types.ImmutableList;

ImmutableList<Integer> values = ImmutableList.of(1, 2, 3)
        .append(4)
        .prepend(0)
        .filter(value -> value % 2 == 0);

ImmutableList<Integer> more = values.concat(ImmutableList.of(6, 8));
```

`ImmutableList` rejects `null`, defensively copies incoming values, and exposes
an unmodifiable `List` through `toList()`.

Common query and search operations return booleans or `Option` values instead
of sentinel values:

```java
import com.nan.funxtion.types.ImmutableList;
import com.nan.funxtion.types.Option;

import java.util.List;

ImmutableList<Integer> values = ImmutableList.of(1, 2, 3, 2);

boolean hasValues = values.containsAll(List.of(1, 3)); // true
Option<Integer> first = values.indexOf(2);             // Some(1)
Option<Integer> last = values.lastIndexOf(2);          // Some(3)
Option<Integer> missing = values.indexOf(9);           // None
```

Transformations preserve order and return new immutable lists:

```java
import com.nan.funxtion.types.ImmutableList;
import com.nan.funxtion.types.Option;

ImmutableList<Integer> doubled = ImmutableList.of(1, 2, 3)
        .map(value -> value * 2);

ImmutableList<Integer> evens = ImmutableList.of(1, 2, 3, 4)
        .filter(value -> value % 2 == 0);

ImmutableList<Integer> expanded = ImmutableList.of(1, 2, 3)
        .flatMap(value -> ImmutableList.of(value, value * 10));

ImmutableList<String> evenLabels = ImmutableList.of(1, 2, 3, 4)
        .collect(value -> value % 2 == 0
                ? Option.some("even-" + value)
                : Option.none());
```

Partitioning returns a two-value tuple where position `1` contains matching
values and position `2` contains non-matching values. Prefix splitting follows
the same tuple convention:

```java
import com.nan.funxtion.types.ImmutableList;
import com.nan.tuplex.Tuple;

Tuple partition = ImmutableList.of(1, 2, 3, 4)
        .partition(value -> value % 2 == 0);

Object even = partition.get(1); // ImmutableList([2, 4])
Object odd = partition.get(2);  // ImmutableList([1, 3])

Tuple span = ImmutableList.of(1, 2, 3, 1)
        .span(value -> value < 3);

Object prefix = span.get(1); // ImmutableList([1, 2])
Object suffix = span.get(2); // ImmutableList([3, 1])
```

Grouping returns an unmodifiable map and preserves the order in which keys first
appear:

```java
import com.nan.funxtion.types.ImmutableList;

import java.util.Map;

Map<String, ImmutableList<Integer>> grouped = ImmutableList.of(1, 2, 3, 4)
        .groupBy(value -> value % 2 == 0 ? "even" : "odd");

ImmutableList<Integer> odd = grouped.get("odd");   // ImmutableList([1, 3])
ImmutableList<Integer> even = grouped.get("even"); // ImmutableList([2, 4])
```

Reduction and scanning operations support both left and right traversal:

```java
import com.nan.funxtion.types.ImmutableList;
import com.nan.funxtion.types.Option;

int sum = ImmutableList.of(1, 2, 3)
        .foldLeft(0, Integer::sum);

Option<Integer> reduced = ImmutableList.of(1, 2, 3)
        .reduceRight((value, acc) -> value - acc); // Some(2)

ImmutableList<Integer> scanned = ImmutableList.of(1, 2, 3)
        .scanLeft(0, Integer::sum);                // ImmutableList([0, 1, 3, 6])
```

Slicing can use counts or predicates:

```java
import com.nan.funxtion.types.ImmutableList;

ImmutableList<Integer> prefix = ImmutableList.of(1, 2, 3, 1)
        .takeWhile(value -> value < 3); // ImmutableList([1, 2])

ImmutableList<Integer> suffix = ImmutableList.of(1, 2, 3, 1)
        .dropWhile(value -> value < 3); // ImmutableList([3, 1])
```

Combination and window operations cover zipping, separators, and fixed-size
windows:

```java
import com.nan.funxtion.types.ImmutableList;
import com.nan.tuplex.Tuple;

ImmutableList<Tuple> pairs = ImmutableList.of(1, 2)
        .zip(ImmutableList.of("a", "b"));

ImmutableList<Integer> sums = ImmutableList.of(1, 2)
        .zipWith(ImmutableList.of(10, 20), Integer::sum); // ImmutableList([11, 22])

ImmutableList<String> separated = ImmutableList.of("a", "b", "c")
        .intersperse("-"); // ImmutableList([a, -, b, -, c])

ImmutableList<ImmutableList<Integer>> windows = ImmutableList.of(1, 2, 3, 4)
        .sliding(2);
// ImmutableList([ImmutableList([1, 2]), ImmutableList([2, 3]), ImmutableList([3, 4])])
```

Ordering helpers return immutable results or `Option` when a value may be
absent:

```java
import com.nan.funxtion.types.ImmutableList;
import com.nan.funxtion.types.Option;

ImmutableList<Integer> sorted = ImmutableList.of(3, 1, 2)
        .sort(Integer::compareTo);

Option<Integer> min = sorted.min(Integer::compareTo); // Some(1)
Option<Integer> max = sorted.max(Integer::compareTo); // Some(3)
```

For Java interoperability and effectful checked callbacks:

```java
import com.nan.funxtion.types.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.Set;

List<String> labels = ImmutableList.of(1, 2, 3)
        .stream()
        .map(value -> "value-" + value)
        .toList();

Set<Integer> unique = ImmutableList.of(1, 2, 2, 3)
        .toSet();

Map<String, Integer> lengths = ImmutableList.of("a", "bb", "aa")
        .toMap(value -> value.substring(0, 1), String::length);

StringBuilder builder = new StringBuilder();
ImmutableList.of("a", "b", "c")
        .forEach(builder::append);
```

`toSet()` and `toMap(...)` return unmodifiable collections. Both preserve
first-occurrence order; duplicated map keys keep their first position and use
the last mapped value.

### Checked Functions

```java
import com.nan.funxtion.types.functional.CheckedFunction;

CheckedFunction<String, Integer> parse = Integer::parseInt;
CheckedFunction<String, Integer> doubled = parse.andThen(value -> value * 2);

int value = doubled.apply("21");
```

The `Checked*` interfaces mirror common Java functional interfaces while
allowing checked or unchecked exceptions to be thrown.

## Publishing

The Gradle build is configured for Maven publication. Local development does
not require publishing credentials.

The release workflow publishes each tagged release to:

- Maven Central
- GitHub Packages under `nanielito/maven-packages`

Publishing credentials are read from repository secrets:

- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `GH_PACKAGES_TOKEN`
- `SIGNING_KEY`
- `SIGNING_PASSWORD`

## License

Funxtion is released under the MIT License. See [LICENSE](LICENSE).
