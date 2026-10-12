# Day 6 verification results

- Standalone compile: passed with `javac -d <temporary-classes> src\Main.java src\OrderProcessor.java src\exception\InsufficientStockException.java src\exception\InvalidQuantityException.java`.
- Standalone run: passed with `java -cp <temporary-classes> Main`; captured successful processing, malformed quantity with preserved cause, insufficient stock, and audit output in `Run-Output.txt`.
- PEBT build: `mvn clean package` — **BUILD SUCCESS** (Java 21, 15 main source files and the harness compiled; no external dependencies were added).
- PEBT focused runtime harness: `java -cp 'target/classes;target/test-classes' BusinessRulesHarness` — **PASS: invalid amounts, insufficient funds, and menu recovery**. It checks null/zero/negative amounts, rejected withdrawal balance preservation, successful withdrawal, malformed/zero/over-balance menu input, later valid input, and exit.
- `git diff --check` — passed with no whitespace errors.
- Day 5: no `Daily-Tasks/Day-05/` files were modified. The Day 5 PEBT baseline commit remains the branch's existing parent history.
- Copilot Chat was unavailable; no Copilot interaction is claimed. Independently reviewed actual exercise traces are documented in `AI-Stack-Trace-Notes.md`.
