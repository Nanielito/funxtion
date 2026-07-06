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

## Build And Test

```bash
./gradlew test
```

Build artifacts:

```bash
./gradlew build
```

## Contributing

Development workflow, branch conventions, validation, and pull request guidance
are documented in [CONTRIBUTING.md](CONTRIBUTING.md).

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

Partitioning returns a two-value tuple where position `1` contains matching
values and position `2` contains non-matching values:

```java
import com.nan.funxtion.types.ImmutableList;
import com.nan.tuplex.Tuple;

Tuple partition = ImmutableList.of(1, 2, 3, 4)
        .partition(value -> value % 2 == 0);

Object even = partition.get(1); // ImmutableList([2, 4])
Object odd = partition.get(2);  // ImmutableList([1, 3])
```

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

The Gradle build is configured for Maven publication and GitHub Packages. A
remote repository and credentials are not required for local development.

When publishing is configured, credentials are read from:

- `GITHUB_ACTOR`
- `GITHUB_TOKEN`

## License

Funxtion is released under the MIT License. See [LICENSE](LICENSE).
