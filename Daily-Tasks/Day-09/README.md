# Day 9 — Generics and resource management

## Standalone Generic Toolkit

`src/Pair.java` demonstrates two independent type parameters. `src/Box.java` bounds `T` to `Number`, so the box can safely use numeric operations. `GenericToolkit.max` bounds its method type to `Comparable<T>`. The copy method uses `? extends Number` for the source (producer) and `? super Number` for the destination (consumer), following PECS. The CSV reader uses try-with-resources; two custom `AutoCloseable` resources show that resources close in reverse acquisition order. Java generics use type erasure: generic type arguments are checked by the compiler and are not retained as distinct runtime classes.

From `Daily-Tasks/Day-09` in PowerShell:

```powershell
javac -Xlint:all -d out src\Pair.java src\Box.java src\TracedResource.java src\GenericToolkit.java
java -cp out GenericToolkit
```

The program reads `products.csv` from the current directory. Pass another CSV path as the first argument to use a different file. `Run-Output.txt` contains the verified output.

## PEBT integration

PEBT uses `CrudRepository<T, ID>` and `InMemoryRepository<T, ID>` for accounts, transactions, recurring rules, and budgets. The service keeps its existing ordered transaction ledger and business-rule validation while using the generic repositories for entity storage and ID lookup. At startup it reads `seed-accounts.csv`; all rows are parsed and validated before any are added. Money remains `BigDecimal`. `GenericsRepositoryHarness` checks repository CRUD, successful CSV loading, and rejection of invalid CSV without partially changing service state.

PEBT build and harness results are in `PEBT-Verification-Results.md`.
