# Development Agents

This directory documents repeatable agent workflows for this repository.

The goal is to keep development work consistent from idea to release:

```text
Issue -> Plan -> Implementation -> Pull Request -> Release
```

## Available Agents

| Agent | Use When | Output |
| --- | --- | --- |
| [feature-planner](feature-planner.md) | A new API, behavior, or workflow improvement needs planning | Contract, semantics, implementation plan, tests, docs, commits |
| [bug-hunter](bug-hunter.md) | A bug report, regression, or failing test needs investigation | Reproduction, suspected cause, fix strategy, regression tests |
| [pr-writer](pr-writer.md) | A branch is ready for review | PR title, summary, changes, validation, notes |
| [release-writer](release-writer.md) | A release or changelog entry needs preparation | Release notes, changelog review, validation checklist |

## Recommended Flow

1. Start with an issue template:
   - Feature work uses `.github/ISSUE_TEMPLATE/feature_request.md`.
   - Bug work uses `.github/ISSUE_TEMPLATE/bug_report.md`.
2. Use the matching planning agent:
   - Features go through `feature-planner`.
   - Bugs go through `bug-hunter`.
3. Implement the change in a focused branch.
4. Use `pr-writer` or `scripts/pr-summary.sh` to draft the pull request.
5. After merge, use `release-writer` when preparing release notes.

## Branch And Commit Guidance

- Prefer focused branches named by intent, for example:
  - `feature/immutable-list-sliding`
  - `fix/try-interrupted-recovery`
  - `docs/public-api-javadocs`
- Use Conventional Commits:
  - `feat(types): add immutable list sliding`
  - `fix(types): preserve interrupt flag during recovery`
  - `docs(functional): document checked interfaces`
  - `chore: add pull request workflow helpers`

## Validation Defaults

Use these commands unless the task clearly needs a narrower or broader validation set:

```bash
./gradlew test
./gradlew javadoc
```

For PR drafting:

```bash
./scripts/pr-summary.sh main
```

## Agent Behavior

- Read the issue, commits, and diff before producing output.
- Prefer repository conventions over generic advice.
- Keep plans and summaries concise but specific.
- Mention uncertainty explicitly.
- Do not hide missing validation.
