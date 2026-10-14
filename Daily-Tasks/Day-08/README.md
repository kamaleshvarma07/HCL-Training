# Day 8 — Maps, Comparable, and Comparator

## Standalone Employee Directory exercise

`EmployeeDirectory.java` demonstrates the workbook's Map and sorting requirements:

- `HashMap` provides employee ID lookups and department frequency counts where iteration order is not part of the lookup contract.
- `LinkedHashMap` preserves first-seen department order for a predictable summary.
- `TreeMap` sorts department names for the grouped report.
- `Employee implements Comparable<Employee>` sorts naturally by ID.
- A chained `Comparator` sorts salary descending, then employee name ascending, and prints the top five.
- The hash-contract example shows an intentionally broken key whose `equals` compares IDs but whose inherited `hashCode` does not, followed by a corrected key that overrides both consistently.

Run from this directory:

```powershell
javac -d out src\EmployeeDirectory.java
java -cp out EmployeeDirectory
```

The captured output is in `Run-Output.txt`. `Verification-Results.md` records the commands and observed checks.

## PEBT integration (JFS-20 FR4–FR6)

The separate PEBT project adds monthly recurring rules, monthly category budgets, and threshold reporting. Recurrences clamp a requested day to the last day of shorter months; each `(rule ID, occurrence date)` is tracked so rerunning is idempotent. A due transaction is only marked applied after the normal ledger operation succeeds, retaining the existing insufficient-funds handling.

Budget keys use a `TreeMap` ordered by month and normalized category. Transaction/account ID lookups use `LinkedHashMap` for direct lookup while retaining predictable order. Monthly expense totals use `BigDecimal` and include only expense entries in the requested `YearMonth`; the existing project uses one in-memory ledger and has no user ownership identity, authentication, or persistence model, so these new operations follow that same scope.

`Day8BudgetIntegrationHarness` checks recurring idempotency and month-end clamping, balance updates through the existing transaction path, category totals by month, and below/exact 80%/exact 100% threshold boundaries.
