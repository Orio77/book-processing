---
name: jacoco-module-testing
description: Adds JaCoCo to a Maven project and authors high-quality tests for a user-specified module with coverage gates (80% overall, 100% crucial business logic). Use when the user asks for JaCoCo, module test coverage, coverage thresholds, or thorough tests for a package or bounded context.
disable-model-invocation: true
---

# JaCoCo and module test coverage

## Goal

Ensure JaCoCo is wired into the project, then add tests for the **module the user names** (Java package prefix, Maven submodule, or bounded context). Enforce **at least 80%** line coverage on that module and **100%** line coverage on **crucial business logic** (typically `services`, domain rules, or whatever the user points to as critical).

The **overarching goal is the quality of the tests**.

## Verbatim priorities (from the user)

The tests don't have to pass, all can fail, but if the quality is excellent, that's what matters. The more tests that fail the more bugs are discovered.

Do not fix any errors found by tests, that is someone else's job.

Treat these as hard constraints: optimize for expressive, behavior-focused tests and meaningful assertions—not for green builds or patching production code to satisfy tests.

## When this skill applies

- User names a **module** or **package** to cover (e.g. `book_management`, `com.example.orders`).
- User asks for **JaCoCo**, **coverage report**, **coverage gates**, or **80%/100%** rules.
- User wants **thorough** or **high-quality** tests without asking you to fix failing code.

## JaCoCo (Maven)

1. Add `jacoco-maven-plugin` with a pinned `jacoco.version` property.
2. Executions:
   - `prepare-agent` (default bound to `test`).
   - `report` in phase `test` so `target/site/jacoco/index.html` is produced.
   - `check` in phase `verify` with **two** `rule` blocks on `element` `BUNDLE`:
     - **Module bundle**: `includes` Ant-style patterns for the module bytecode path (slashes), e.g. `com/example/orders/**` → **minimum 0.80** `LINE` `COVEREDRATIO`.
     - **Crucial logic**: narrower `includes` (e.g. `com/example/orders/services/**`) → **minimum 1.00** `LINE` `COVEREDRATIO`.
3. Tell the user to run `mvn verify` for gates; `mvn test` for tests + report only.

Adapt includes to multi-module reactors: scope each submodule’s `check` to that artifact’s relevant packages, or use a parent `check` with explicit includes per module if the user requests it.

## Test design (quality first)

- Prefer **behavioral** tests: public API of services, orchestration, edge cases, error paths, and invariants—not trivial getters unless they encode rules.
- Use **Mockito** + **JUnit 5** for collaborators; **Spring** slice tests (`@WebMvcTest`, etc.) when the module is Spring MVC and package names match the Boot version in use.
- Add **focused** tests for DTOs/models only when they carry non-trivial logic (validation, mapping, `record` compact constructors).
- Name and structure tests so failures **read as specifications** (given/when/then or clear method names).

## Coverage workflow

1. Identify the **module root** (package prefix or Maven `artifactId` / `src` tree).
2. Add or adjust JaCoCo **includes** so `check` measures the right bytecode; align **crucial** includes with what the user calls critical (default: `.../services/**` under that module).
3. Implement tests until `mvn verify` satisfies the gates **or** until further progress requires product changes—in the latter case, **stop**: do not fix production bugs; document what failed and why (assertion vs. compile vs. missing behavior).
4. If tests fail, **do not** “fix” application code to make them pass unless the user explicitly asks to fix bugs in a **separate** request. Failing tests are a valid outcome when quality is high.

## Out of scope (unless the user explicitly asks)

- Fixing production defects, refactoring production code, or changing behavior to satisfy tests.
- Committing or pushing (follow repo / user rules).

## Verification checklist

- [ ] `jacoco-maven-plugin` present with prepare-agent, report, and check.
- [ ] Module and crucial `includes` patterns match the user’s named scope.
- [ ] Tests target real behavior and edge cases, not only happy paths.
- [ ] User knows: `mvn verify` enforces gates; failures may indicate product bugs—left for others to fix.
