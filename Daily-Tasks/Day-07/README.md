# Day 7 — Collections Framework

## Library Waitlist

This standalone Java demo uses:

- `ArrayList` for the catalogue and timing probes, where ordered iteration and indexed reads matter.
- `LinkedHashSet` for de-duplicated registrations with insertion order.
- `ArrayDeque` as the FIFO waitlist `Queue` implementation.
- `PriorityQueue` for faculty-first holds, with request order as the tie-breaker.
- `HashSet` when only fast membership matters, and `TreeSet` when sorted unique output is useful.
- `Iterator.remove()` for safe removal of overdue loans and `ListIterator` for a safe in-place catalogue insertion.

The program also triggers and catches a `ConcurrentModificationException` on a separate demonstration list. It does not use that unsafe modification pattern in the actual catalogue or waitlist.

Compile and run from this directory:

```powershell
javac -d out src\LibraryWaitlist.java
java -cp out LibraryWaitlist
```

`Run-Output.txt` and `Timing-Results.md` contain the captured run and timing table. Measurements are illustrative: JVM warmup, machine load, and hardware affect timings.

## PEBT FR1–FR3

The PEBT project stores accounts and ledger transactions in ordered `ArrayList`s, uses `LinkedHashSet` IDs to reject duplicate accounts/transactions while retaining deterministic order, and queues pending ledger entries in an `ArrayDeque` through the `Queue` interface. The service replays each account's transactions from its opening balance after add, edit, and delete operations, using `BigDecimal` throughout.

The existing Day 6 quick-expense menu interaction remains available on option 2 for account 1. Enter `manage` there to open the Day 7 add/edit/delete/list submenu. Account creation is available from option 1; balances are viewed from option 3.

`CollectionsIntegrationHarness` checks account creation, transaction CRUD, duplicate IDs, invalid amounts, insufficient-funds rollback, FIFO pending work, running balances, and menu recovery. The harness is run explicitly because the project has no JUnit dependency.
