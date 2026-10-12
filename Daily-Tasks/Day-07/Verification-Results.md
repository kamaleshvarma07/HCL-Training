# Day 7 verification results

- Standalone: `javac -d <temporary-classes> src\LibraryWaitlist.java` and `java -cp <temporary-classes> LibraryWaitlist` — both passed. The captured output demonstrates each planned collection, safe overdue removal, caught concurrent modification, and the timing run in `Run-Output.txt`.
- PEBT build: `mvn clean package` — **BUILD SUCCESS**; Maven compiled 16 main source files and both dependency-free harness classes. No dependencies were added.
- Day 7 FR1–FR3 harness: `java -cp 'target/classes;target/test-classes' CollectionsIntegrationHarness` — **PASS** for opening-balance account creation, income/expense CRUD, duplicate and retired ID rejection, invalid amounts, insufficient-funds rollback, FIFO queued transactions, balance recalculation, and menu recovery.
- Day 6 regression harness: `java -cp 'target/classes;target/test-classes' BusinessRulesHarness` — **PASS** for invalid amounts, insufficient funds, and the preserved quick-expense menu recovery path.
- Interactive PEBT example: `PEBT-Run-Output.txt` records account creation, income and expense entry, transaction edit and delete, and final balance verification.
- `git diff --check` — passed after the final review.
- Day 5 practice and all Day 6 task files match the preserved Day 6 commit; the Day 6 commit remains in branch history.
- Timing results are one local run and vary by machine and JVM conditions; see `Timing-Results.md`.
