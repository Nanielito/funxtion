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
- Tuples come from the external `io.github.nanielito:tuplex` dependency. Use
  `Tuple` and `Tuples` instead of creating local tuple types.

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
- Whenever creating a commit, include a PR-ready title and summary in the
  handoff so the pull request can be opened without asking for another pass.

## Optional WorkForge Integration

The repository can optionally use
[WorkForge](https://github.com/Nanielito/workforge#quick-start) for planning
requirements and change requests. New clones do not necessarily include a
`.workforge/` workspace; it is generated locally after installing and
initializing WorkForge, then ignored by Git because it can contain credentials
and generated provider state.

When the user asks to configure WorkForge:

- Point them to the WorkForge installation and quick-start docs.
- Use `uvx workforge init .workforge --name funxtion --provider github
  --namespace funxtion` as the starting command unless they choose another
  provider or workspace path.
- Keep `.workforge/.env` and `.workforge/output/` out of Git.

When `.workforge/workforge.yaml` already exists and the task involves
requirements, tracked work, issues, or implementation checklists:

- Read `.workforge/README.md` for the workspace-specific workflow.
- Prefer WorkForge for requirement previews, provider item discovery, agent
  context, task completion, item comments, and status moves.
- Preview before external writes. Do not run mutating commands such as
  `create --execute`, `update --execute`, `claim-item`, `move-item`,
  `complete-task`, or `comment-item` unless the user explicitly authorizes it.
- Use `uvx workforge ...` unless the local workspace documents another command.
- Generate implementation context with `agent-context` or `item-context` when
  working from tracked items.
- Treat provider checklist tasks as the implementation checklist. Mark tasks
  complete only after the behavior is implemented and relevant validation passes.
- Leave tracked items in progress while a pull request is open; merge or project
  automation should close or move them to done.

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
