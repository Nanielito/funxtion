#!/usr/bin/env sh
set -eu

base="${1:-}"
title_override="${2:-}"

if [ -z "$base" ]; then
    if git rev-parse --verify origin/main >/dev/null 2>&1; then
        base="origin/main"
    else
        base="main"
    fi
fi

branch="$(git branch --show-current)"
commit_count="$(git rev-list --count "$base"..HEAD)"
changed_files="$(git diff --name-only "$base"...HEAD)"
staged_files="$(git diff --cached --name-only)"
working_files="$(git diff --name-only)"
untracked_files="$(git ls-files --others --exclude-standard)"
all_changed_files="$(printf "%s\n%s\n%s\n%s\n" "$changed_files" "$staged_files" "$working_files" "$untracked_files" | sed '/^$/d' | sort -u)"

workflow_only="false"
if [ -n "$all_changed_files" ]; then
    workflow_only="true"
    for file in $all_changed_files; do
        case "$file" in
            .agents/*|.github/*|scripts/*)
                ;;
            *)
                workflow_only="false"
                ;;
        esac
    done
fi

branch_topic="$branch"
case "$branch" in
    feature/*)
        branch_topic="${branch#feature/}"
        ;;
    feat/*)
        branch_topic="${branch#feat/}"
        ;;
    fix/*)
        branch_topic="${branch#fix/}"
        ;;
    docs/*)
        branch_topic="${branch#docs/}"
        ;;
    chore/*)
        branch_topic="${branch#chore/}"
        ;;
esac
branch_topic="$(printf "%s" "$branch_topic" | tr '-' ' ')"

if [ -n "$title_override" ]; then
    title="$title_override"
elif [ "$commit_count" = "0" ]; then
    if [ -n "$all_changed_files" ]; then
        case "$branch" in
            feature/*|feat/*)
                title="feat: add ${branch_topic}"
                ;;
            fix/*)
                title="fix: address ${branch_topic}"
                ;;
            docs/*)
                title="docs: update ${branch_topic}"
                ;;
            chore/*)
                case "$branch_topic" in
                    *" polish")
                        title="chore: polish ${branch_topic% polish}"
                        ;;
                    *)
                        title="chore: update ${branch_topic}"
                        ;;
                esac
                ;;
            *)
                title="chore: prepare branch changes"
                ;;
        esac
    else
        title="chore: prepare pull request"
    fi
else
    first_subject="$(git log --format=%s "$base"..HEAD | tail -n 1)"
    if [ "$commit_count" = "1" ]; then
        title="$first_subject"
    else
        commit_types="$(git log --format=%s "$base"..HEAD | sed -n 's/^\([a-z][a-z]*\)\(([^)]*)\)\?:.*/\1/p' | sort -u)"
        conventional_count="$(git log --format=%s "$base"..HEAD | sed -n 's/^\([a-z][a-z]*\)\(([^)]*)\)\?:.*/\1/p' | wc -l | tr -d ' ')"
        type_count="$(printf "%s\n" "$commit_types" | sed '/^$/d' | wc -l | tr -d ' ')"
        commit_type="$(printf "%s\n" "$commit_types" | sed '/^$/d' | head -n 1)"
        if [ "$type_count" = "1" ] && [ "$conventional_count" = "$commit_count" ]; then
            case "$commit_type" in
                feat)
                    title="feat: add ${branch_topic}"
                    ;;
                fix)
                    title="fix: address ${branch_topic}"
                    ;;
                docs)
                    title="docs: update public documentation"
                    ;;
                test)
                    title="test: update test coverage"
                    ;;
                refactor)
                    title="refactor: update internal implementation"
                    ;;
                chore)
                    if [ "$workflow_only" = "true" ]; then
                        title="chore: add development workflow helpers"
                    elif [ "${branch_topic% polish}" != "$branch_topic" ]; then
                        title="chore: polish ${branch_topic% polish}"
                    else
                        title="chore: update project tooling"
                    fi
                    ;;
                *)
                    title="chore: prepare branch changes"
                    ;;
            esac
        else
            case "$workflow_only" in
                true)
                    title="chore: add development workflow helpers"
                    ;;
                *)
                    title="chore: prepare branch changes"
                    ;;
            esac
        fi
    fi
fi

if [ -n "$title_override" ]; then
    printf "Suggested title (override):\n%s\n\n" "$title"
else
    printf "Suggested title:\n%s\n\n" "$title"
fi

printf "## Summary\n"
if [ "$commit_count" = "0" ]; then
    printf -- '- No commits found against `%s` yet.\n' "$base"
else
    git log --format='- %s' "$base"..HEAD
fi

printf "\n## Changes\n"
if [ -z "$all_changed_files" ]; then
    printf -- '- No file changes detected against `%s`.\n' "$base"
else
    printf "%s\n" "$all_changed_files" | sed 's/^/- `/; s/$/`/'
fi

printf "\n## Validation\n"
printf -- '- [ ] `./gradlew test`\n'
printf -- '- [ ] `./gradlew javadoc`\n'

printf "\n## Notes\n"
printf -- '- Base: `%s`\n' "$base"
printf -- '- Branch: `%s`\n' "$branch"
if [ -n "$title_override" ]; then
    printf -- '- Title override supplied by user.\n'
fi
if [ -n "$untracked_files" ]; then
    printf -- '- Includes untracked files from the working tree.\n'
fi
if [ -n "$staged_files" ] || [ -n "$working_files" ]; then
    printf -- '- Includes uncommitted changes from the working tree.\n'
fi

printf "\nDiff stat:\n\n"
git diff --stat "$base"...HEAD || true
if [ -n "$staged_files" ] || [ -n "$working_files" ]; then
    printf "\nWorking tree diff stat:\n\n"
    git diff --stat HEAD || true
fi
if [ -n "$untracked_files" ]; then
    printf "\nUntracked files:\n\n"
    printf "%s\n" "$untracked_files"
fi
