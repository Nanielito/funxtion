# PR Writer Agent

Use this guide when preparing a pull request description from the current branch.

## Inputs

- Commit subjects from `git log <base>..HEAD --oneline`.
- Diff summary from `git diff --stat <base>...HEAD`.
- Changed files from `git diff --name-only <base>...HEAD`.
- Validation commands run by the developer.

## Output

Produce a concise PR title and body using the repository PR template:

```md
## Summary
- 

## Changes
- 

## Validation
- [ ] `./gradlew test`
- [ ] `./gradlew javadoc`

## Notes
- 
```

## Title Rules

- Prefer Conventional Commit style.
- Use the dominant commit type when the branch has multiple commits:
  - `feat`: new user-facing behavior or API.
  - `fix`: bug fixes.
  - `docs`: documentation-only changes.
  - `test`: tests-only changes.
  - `refactor`: internal behavior-preserving changes.
  - `chore`: build, tooling, metadata, or maintenance.
- Include a scope when it is clear, for example `docs(types): ...`.
- Keep titles short and imperative or descriptive.

## Body Rules

- Summary should describe the outcome, not every file touched.
- Changes should group related work by behavior or concern.
- Validation should list commands actually run and mark them checked.
- Notes should mention caveats, follow-ups, or breaking changes. Use `None` when there are no notes.
- Avoid dumping raw diffs or long commit lists unless explicitly requested.

## Review Checklist

- Confirm the title matches the dominant change.
- Confirm the body reflects the diff, not only commit messages.
- Confirm validation commands are current.
- Mention migration or release notes only when relevant.

## Local Helper

Use the local summary helper to draft PR content:

```bash
./scripts/pr-summary.sh main
```

Pass a second argument to override the suggested title:

```bash
./scripts/pr-summary.sh main "chore: add development workflow helpers"
```
