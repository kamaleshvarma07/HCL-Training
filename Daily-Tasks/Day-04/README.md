# Day 4 — OOP Concepts and Debugging

## Topics Covered
- Classes, fields, methods, and constructors
- Encapsulation and private fields
- Constructor overloading and chaining using `this()`
- Static and instance members
- Packages and access through getters
- `equals()` and `hashCode()`
- Breakpoints and conditional breakpoints
- Variable inspection using the Variables panel
- Step Over (F10) and debugging

## Hands-on Exercise: BankAccount

Implemented a `BankAccount` class with:
- Private fields for account holder and balance
- Three chained constructors
- Static account counter
- Deposit and withdrawal validation
- `equals()` and `hashCode()` methods
- Validation for invalid transactions and insufficient balance

### Debugging Exercise
- Introduced a bug in the withdrawal calculation.
- Used a conditional breakpoint to stop when the withdrawal amount was `2000.0`.
- Inspected variable values before and after executing the faulty line.
- Identified and fixed the incorrect balance calculation.
- Verified the correct balance and rejection of an insufficient-balance withdrawal.

## Project Task: Personal Expense & Budget Tracker

Created three model classes in the `com.hcltraining.model` package:

- **Account.java** — stores account ID, name, type, and balance.
- **Transaction.java** — stores transaction ID, account ID, type, category, amount, date, and description.
- **Budget.java** — stores budget ID, category, spending limit, month, and year.

Used `BigDecimal` to represent monetary amounts in the project models.

## Deliverables

### Bank Classes
- `BankAccount.java`
- `BankAccountTest.java`

### Debugging Screenshots
- Screenshot showing the conditional breakpoint.
- Screenshot showing variable inspection and the incorrect balance.
- Screenshot showing the corrected program output.

### Project Model Classes
- `Account.java`
- `Transaction.java`
- `Budget.java`

### Build Verification
- Compiled the Maven project using `mvn compile`.
- Result: `BUILD SUCCESS`.
- Six Java source files compiled successfully.

## Git Commit
`day-4: oop-concepts`

## Status
**COMPLETED** ✅