# Day 6 — Exception Handling

## Standalone Order Processor

This is an isolated Java exercise; it does not connect to a store or process real orders.

- `InsufficientStockException` is checked and `InvalidQuantityException` is unchecked.
- `OrderProcessor.processOrder` declares the checked error with `throws` and raises errors with `throw`.
- Invalid text is wrapped with its original `NumberFormatException` cause.
- `Main` demonstrates successful processing, multi-catch, and a `finally` audit message for success and failures.

From this directory, compile and run with:

```powershell
javac -d out src\Main.java src\OrderProcessor.java src\exception\InsufficientStockException.java src\exception\InvalidQuantityException.java
java -cp out Main
```

The actual output and stack traces are captured in `Run-Output.txt`. The trace explanations and independent correctness checks are in `AI-Stack-Trace-Notes.md`.

## PEBT integration

PEBT adds `InvalidTransactionAmountException` for null/non-positive transaction amounts and `InsufficientFundsException` for attempted withdrawals above the account balance. The menu catches failed input/operations and returns to the menu. Monetary checks and balance updates use `BigDecimal`.

The dependency-free `BusinessRulesHarness` checks invalid amounts, the insufficient-funds rule and state preservation, successful withdrawal, and menu recovery through a later successful expense and exit.
