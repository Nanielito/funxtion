# Contributing

Thanks for helping improve Funxtion.

This project uses a lightweight workflow centered on focused issues, small
branches, Conventional Commits, and repeatable validation.

## Development Flow

1. Start from an issue or clear task.
2. Create a focused branch.
3. Make the smallest coherent change.
4. Add or update tests and JavaDoc when behavior or public API changes.
5. Run validation.
6. Open a pull request using the repository template.

## Issues

Use the GitHub issue templates when possible:

- Feature requests: `.github/ISSUE_TEMPLATE/feature_request.md`
- Bug reports: `.github/ISSUE_TEMPLATE/bug_report.md`

Feature requests should describe the proposed behavior, expected API, tests,
and documentation needs.

Bug reports should include current behavior, expected behavior, reproduction
steps, evidence, and environment details.

## Branches

Use focused branch names that describe the intent:

```text
feature/immutable-list-sliding
fix/try-interrupted-recovery
docs/public-api-javadocs
chore/pr-summary-title-polish
```

## Commits

Use Conventional Commits:

```text
feat(types): add immutable list sliding
fix(types): preserve interrupt flag during recovery
docs(functional): document checked interfaces
test(types): cover immutable list grouping
chore: add pull request workflow helpers
```

Prefer small commits that can be reviewed independently.

## Validation

Run the relevant validation before opening a pull request.

For most changes:

```bash
./gradlew test
./gradlew javadoc
```

For documentation-only or workflow-only changes, narrower validation may be
enough. For example:

```bash
sh -n scripts/pr-summary.sh
./scripts/pr-summary.sh main
```

If a validation command was not run, mention that in the pull request notes.

## Pull Requests

GitHub automatically loads `.github/pull_request_template.md` when creating a
pull request.

You can generate a draft title and body from the current branch with:

```bash
./scripts/pr-summary.sh main
```

Pass a second argument to override the suggested title:

```bash
./scripts/pr-summary.sh main "chore: polish pull request summary titles"
```

Before requesting review, confirm:

- The PR title follows Conventional Commit style.
- The summary describes the outcome of the change.
- The changes section reflects the actual diff.
- Validation checkboxes match commands that were actually run.
- Notes call out caveats, skipped validation, or follow-up work.

## Agent Guides

Reusable development guides live in `.agents/`:

- `.agents/feature-planner.md` for feature planning.
- `.agents/bug-hunter.md` for bug investigation.
- `.agents/pr-writer.md` for pull request summaries.
- `.agents/release-writer.md` for release notes.

These files are meant to make repeatable collaboration easier. They are
process documentation, not required tooling.

## Public API Changes

When changing public APIs:

- Define null-safety and exception behavior.
- Add focused tests for happy paths and edge cases.
- Add regression tests for bug fixes.
- Update JavaDoc with `@param`, `@return`, and `@throws` where applicable.
- Run `./gradlew javadoc` and resolve warnings.

## Releases

Release notes and changelog generation follow `cliff.toml`.

Commits should remain Conventional Commit compatible so generated changelogs can
group changes correctly.
