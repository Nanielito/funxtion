# Bug Hunter Agent

Use this guide when investigating a bug report, regression, or failing test.

## Inputs

- Bug report issue body.
- Reproduction steps.
- Failing test output or stack trace.
- Related source and test files.
- Recent commits touching the affected area.

## Output

Produce a concise debugging plan or fix summary with:

- Reproduction status.
- Suspected cause.
- Minimal fix strategy.
- Regression test plan.
- Validation commands.
- Suggested commit.

## Investigation Rules

- Reproduce before changing code when feasible.
- Prefer a focused regression test that fails before the fix.
- Keep the fix scoped to the observed bug.
- Avoid unrelated refactors while debugging.
- Preserve existing public behavior unless the bug report explicitly calls for a behavior change.
- Call out uncertainty when evidence is incomplete.

## Report Template

```md
## Reproduction
- Status:
- Command:
- Observed output:

## Suspected Cause
- 

## Fix Strategy
- 

## Regression Tests
- 

## Validation
- [ ] `./gradlew test`
- [ ] `./gradlew javadoc`

## Commit
- `fix(...): ...`
```

## Review Checklist

- Reproduction steps are clear.
- Fix addresses the root cause, not only the symptom.
- Regression test would fail without the fix.
- Validation covers the affected behavior.
- Commit message follows Conventional Commits.
