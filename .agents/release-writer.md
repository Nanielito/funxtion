# Release Writer Agent

Use this guide when preparing a release, changelog update, or release notes.

## Inputs

- Commits since the previous release tag.
- Existing `CHANGELOG.md`.
- `cliff.toml` configuration.
- Version being released.
- Validation commands run before release.

## Output

Produce concise release notes with:

- Version and date.
- Highlights grouped by Conventional Commit type.
- Breaking changes, if any.
- Validation performed.
- Follow-up notes, if any.

## Repository Changelog

This repository uses `git-cliff` configuration from `cliff.toml`.

Relevant conventions:

- Tags follow `v<major>.<minor>.<patch>`.
- Conventional Commit types are grouped as:
  - `feat` -> Features
  - `fix` -> Bug Fixes
  - `refactor` -> Refactoring
  - `docs` -> Documentation
  - `test` -> Tests
  - `chore` -> Chore
- Unconventional commits are filtered from generated changelog output.

## Release Notes Template

```md
## vX.Y.Z - YYYY-MM-DD

### Highlights
- 

### Changes
- 

### Validation
- [ ] `./gradlew test`
- [ ] `./gradlew javadoc`

### Notes
- None
```

## Suggested Workflow

1. Inspect the latest tag:
   ```bash
   git describe --tags --abbrev=0
   ```
2. Review commits since the latest tag:
   ```bash
   git log <latest-tag>..HEAD --oneline
   ```
3. Generate or review changelog output with `git-cliff`.
4. Confirm validation:
   ```bash
   ./gradlew test
   ./gradlew javadoc
   ```
5. Prepare release notes using the template above.

## Review Checklist

- Version matches the intended semantic version bump.
- Release notes reflect the actual commits and diff.
- Breaking changes are called out clearly.
- Validation is current.
- Changelog groups match `cliff.toml`.
