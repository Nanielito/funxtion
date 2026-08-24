# AGENTS.md

Guidance for AI agents and automation working in this repository.

## Project Context

Funxtion is a small Java functional library. It provides:

- `Option<T>` for optional non-null values.
- `Either<L, R>` for right-biased computations with left alternatives.
- `Try<T>` for computations that can fail with non-fatal `Throwable`s.
- `ImmutableList<T>` for immutable, null-rejecting list operations.
- `Checked*` functional interfaces for lambdas that can throw `Throwable`.

The library targets Java 21 bytecode and is built with Gradle. CI validates on
Java 21 and 25.

## Architecture

- Main source lives under `src/main/java/com/nan/funxtion/types`.
- Tests live under `src/test/java/com/nan/funxtion/types`.
- Public functional interfaces live under
  `src/main/java/com/nan/funxtion/types/functional`.
- `ImmutableList` uses a sealed interface with the nested
  `ArrayImmutableList` implementation.
- `Option`, `Either`, and `Try` use sealed interfaces with nested concrete
  implementations.
- Tuples come from the external `com.nan:tuplex` dependency. Use `Tuple` and
  `Tuples` instead of creating local tuple types.

## Implementation Rules

- Keep changes small and focused.
- Prefer existing project patterns over new abstractions.
- Do not add dependencies unless the standard library and existing dependencies
  clearly cannot cover the need.
- Preserve immutability and defensive-copy behavior.
- Public container types should reject `null` values unless an existing API
  explicitly allows them.
- Checked callbacks should propagate or capture exceptions according to the
  surrounding type:
  - `ImmutableList` methods that take `Checked*` generally declare
    `throws Throwable`.
  - `Try` captures non-fatal throwables as `Failure`.
- Fatal JVM errors in `Try` should remain fatal.
- Interrupted computations captured by `Try` should restore the interrupt flag.
- Avoid unrelated refactors while implementing a feature or fix.

## Public API Rules

When adding or changing public APIs:

- Define null-safety explicitly.
- Define exception behavior explicitly.
- Define ordering and immutability semantics when relevant.
- Add or update JavaDoc with `@param`, `@return`, and `@throws`.
- Keep method names consistent with the existing functional style.
- Add focused tests for happy paths, edge cases, null-safety, and exception
  propagation.

## Testing And Validation

Default validation:

```bash
./gradlew test
./gradlew javadoc
```

Use narrower validation only for documentation or workflow-only changes, and
mention skipped validation in the PR notes.

For shell helper changes:

```bash
sh -n scripts/pr-summary.sh
./scripts/pr-summary.sh main
```

## Git Workflow

- Use focused branches:
  - `feature/<short-description>`
  - `fix/<short-description>`
  - `docs/<short-description>`
  - `chore/<short-description>`
- Use Conventional Commits:
  - `feat(types): add immutable list sliding`
  - `fix(types): preserve interrupt flag during recovery`
  - `docs(functional): document checked interfaces`
  - `test(types): cover immutable list grouping`
  - `chore: add pull request workflow helpers`
- Keep commits reviewable and scoped.
- Do not rewrite or revert user changes unless explicitly asked.

## Pull Requests

Use the GitHub PR template. Generate a draft with:

```bash
./scripts/pr-summary.sh main
```

Override the generated title when needed:

```bash
./scripts/pr-summary.sh main "feat(types): add immutable list sliding"
```

Before opening a PR:

- Confirm the title follows Conventional Commit style.
- Confirm the summary reflects the actual diff.
- Mark only validation commands that were actually run.
- Mention skipped validation or follow-up work in notes.

## Agent Guides

Additional process guides live in `.agents/`:

- `.agents/feature-planner.md` for feature planning.
- `.agents/bug-hunter.md` for bug investigation.
- `.agents/pr-writer.md` for PR summaries.
- `.agents/release-writer.md` for release notes.

Use them when the task matches the guide. Keep this file as the high-level
repository operating contract.

## Collaboration Notes

- When the user asks to learn through implementation, provide contracts,
  implementation snippets, and test ideas instead of editing files directly.
- When the user explicitly grants permission to edit, make the smallest useful
  change and validate it.
- Report validation results clearly.
- If the working tree already has changes, assume they belong to the user and
  work around them.
