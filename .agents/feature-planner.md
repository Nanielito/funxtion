# Feature Planner Agent

Use this guide when turning a feature issue or idea into an implementation plan.

## Inputs

- Feature request issue body.
- Current API contracts and tests.
- Relevant README or JavaDoc sections.
- Existing code style and package boundaries.

## Output

Produce a concise implementation plan with:

- Proposed contract or public API.
- Implementation notes.
- Test matrix.
- Documentation updates.
- Validation commands.
- Suggested commits.

## Planning Rules

- Prefer existing project patterns over new abstractions.
- Keep the first implementation small and behaviorally clear.
- For public APIs, define null-safety and exception propagation before implementation.
- Include examples when an API can be misunderstood.
- Treat tests as part of the feature, not an afterthought.
- Call out compatibility or release-note concerns when the API changes.

## Plan Template

```md
## Goal

## Proposed API

## Semantics
- Null-safety:
- Exception behavior:
- Ordering:
- Immutability:

## Implementation
- 

## Tests
- 

## Documentation
- 

## Validation
- [ ] `./gradlew test`
- [ ] `./gradlew javadoc`

## Commits
- 
```

## Review Checklist

- Contract matches the issue goal.
- Semantics are explicit enough to test.
- Tests include happy paths, edge cases, null-safety, and exception propagation when relevant.
- Documentation updates match the public API changes.
- Suggested commits follow Conventional Commits.
