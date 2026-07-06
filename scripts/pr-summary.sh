#!/usr/bin/env sh
set -eu

base="${1:-}"

if [ -z "$base" ]; then
    if git rev-parse --verify origin/main >/dev/null 2>&1; then
        base="origin/main"
    else
        base="main"
    fi
fi

branch="$(git branch --show-current)"
commit_count="$(git rev-list --count "$base"..HEAD)"

if [ "$commit_count" = "0" ]; then
    title="chore: prepare pull request"
else
    first_subject="$(git log --format=%s "$base"..HEAD | tail -n 1)"
    if [ "$commit_count" = "1" ]; then
        title="$first_subject"
    else
        first_type="$(printf "%s" "$first_subject" | sed -n 's/^\([a-z][a-z]*\)\(([^)]*)\)\?:.*/\1/p')"
        case "$first_type" in
            feat|fix|docs|test|refactor|chore)
                title="$first_type: update ${branch}"
                ;;
            *)
                title="chore: update ${branch}"
                ;;
        esac
    fi
fi

printf "Suggested title:\n%s\n\n" "$title"

printf "## Summary\n"
if [ "$commit_count" = "0" ]; then
    printf -- '- No commits found against `%s` yet.\n' "$base"
else
    git log --format='- %s' "$base"..HEAD
fi

printf "\n## Changes\n"
changed_files="$(git diff --name-only "$base"...HEAD)"
untracked_files="$(git ls-files --others --exclude-standard)"
all_changed_files="$(printf "%s\n%s\n" "$changed_files" "$untracked_files" | sed '/^$/d' | sort -u)"
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
if [ -n "$untracked_files" ]; then
    printf -- '- Includes untracked files from the working tree.\n'
fi

printf "\nDiff stat:\n\n"
git diff --stat "$base"...HEAD || true
if [ -n "$untracked_files" ]; then
    printf "\nUntracked files:\n\n"
    printf "%s\n" "$untracked_files"
fi
