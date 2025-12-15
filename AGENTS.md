# Repository Guidelines

## Project Structure & Modules

- Spring Boot service with Java 21; main code lives in `src/main/java/me/gg/pinit` and config in `src/main/resources` (
  `application.yml`).
- Tests sit in `src/test/java/me/gg/pinit`; keep package mirrors of main code.
- Build tooling is Gradle (`build.gradle`, `settings.gradle`, `gradlew` wrapper). No submodules at this time.

## Build, Test, and Development

- `./gradlew clean build` — compile, run checks, and produce the runnable JAR.
- `./gradlew test` — run the JUnit/Spring Boot test suite.
- `./gradlew bootRun` — start the app locally using `application.yml` (override secrets via env or
  `application-local.yml` if added).

## Coding Style & Naming

- Java 21, 4-space indentation; follow standard Spring idioms.
- Package prefix `me.gg.pinit`; class names `PascalCase`, methods/fields `camelCase`, constants `UPPER_SNAKE_CASE`.
- Prefer Lombok for boilerplate already in use; keep constructor injection where possible.
- Keep configuration keys kebab-case in YAML; avoid committing real secrets.

## Testing Guidelines

- Frameworks: JUnit + Spring Boot test starters (`spring-boot-starter-*-test`).
- Naming: mirror package structure; test classes end with `Tests`; method names describe behavior (
  `methodName_shouldDoThing`).
- Fast cycle: `./gradlew test`; add focused slices where possible to avoid booting the full context.
- Aim for coverage on security flows, token handling, and data access boundaries.

## Commit & Pull Requests

- Follow Conventional Commit flavor used so far (`chore: ...`, `feat: ...`, `fix: ...`); keep short, imperative
  subjects.
- PRs should state purpose, key changes, and impact; link issues if applicable.
- Include test notes (`Tests: ./gradlew test`) and configs touched; add screenshots only if user-visible behavior
  changes (rare here).

## Security & Configuration

- Do not commit real credentials; replace secrets in `application.yml` with env-driven values before pushing.
- Prefer local overrides (`APPLICATION_SECRET`, `SPRING_DATASOURCE_*`, or a git-ignored `application-local.yml`) when
  running dev instances.
- Rotate JWT secrets and OAuth client secrets promptly; document any new environment variables in the PR description.***
