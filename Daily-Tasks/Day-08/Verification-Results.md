# Day 8 verification results

- Workbook criteria checked: Master Daily Plan Day 8, Project Catalog JFS-20, Mandatory NFR, RUBRICS, TOC, and SCHEDULE. Day 8 exercise is the Employee Directory; PEBT task is FR4–FR6.
- Standalone compile: `javac -d out src\EmployeeDirectory.java` — passed.
- Standalone run: `java -cp out EmployeeDirectory` — passed. `Run-Output.txt` records Map lookups/counts/reports, ID ordering, top-five salary ordering, and the broken/fixed hash-contract results.
- PEBT build: `mvn clean package` — **BUILD SUCCESS**. Maven compiled 18 main sources and 3 test harness sources; no external dependency was added. Full output: `PEBT-Build-Output.txt`.
- Day 8 PEBT harness: `java -cp 'target\classes;target\test-classes' Day8BudgetIntegrationHarness` — **PASS** for monthly recurrence idempotency, month-end clamping, account balance updates, monthly category totals, and below/exact 80%/exact 100% alert boundaries.
- Regression harnesses: `CollectionsIntegrationHarness` and `BusinessRulesHarness` both **PASS**. Captured output: `PEBT-Run-Output.txt`.
- `git diff --check` — passed.
- `git diff HEAD -- Daily-Tasks/Day-05 Daily-Tasks/Day-06 Daily-Tasks/Day-07` — empty; the prior days' files remain unchanged.
- This project remains an in-memory console application. NFR items requiring authentication, persistence, HTTP APIs, or deployment are not implemented by this Day 8 task and are not claimed as complete.
