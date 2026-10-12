# Day 6 — AI-assisted stack-trace review

## Copilot availability

Copilot Chat is not available through this environment, so no Copilot conversation or output is claimed here. The following are actual traces captured from running `Main` in this exercise. The explanations, suggested fixes, and verdicts are an independent code-and-runtime review, not Copilot output.

## Trace 1 — malformed quantity (chained cause)

```text
exception.InvalidQuantityException: Quantity must be a whole number.
	at OrderProcessor.processOrder(OrderProcessor.java:40)
	at Main.lambda$main$1(Main.java:8)
	at Main.demonstrate(Main.java:16)
	at Main.main(Main.java:8)
Caused by: java.lang.NumberFormatException: For input string: "many"
	at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)
	at java.base/java.lang.Integer.parseInt(Integer.java:662)
	at java.base/java.lang.Integer.parseInt(Integer.java:778)
	at OrderProcessor.processOrder(OrderProcessor.java:38)
	... 3 more
```

**Root cause and relevant frames:** `Integer.parseInt` cannot parse the input `many`. Its `NumberFormatException` originates in `OrderProcessor.processOrder(String)` and is wrapped at line 40 as the unchecked `InvalidQuantityException`. The calls then flow through `Main.demonstrate` to `Main.main`.

**Suggested fix:** Validate/parse the user input at the boundary and translate malformed input into `InvalidQuantityException`, retaining the caught exception as its cause. The caller can then show a useful message while diagnostics retain the parser failure.

**Independent verdict:** Correct and verified. The run prints the `InvalidQuantityException` followed by `Caused by: NumberFormatException`; `InvalidQuantityException` calls `super(message, cause)`, preserving the original cause. The multi-catch handles this invalid-input case.

## Trace 2 — requested quantity exceeds stock

```text
exception.InsufficientStockException: Insufficient stock for notebook: requested 8, available 3.
	at OrderProcessor.processOrder(OrderProcessor.java:25)
	at Main.lambda$main$2(Main.java:9)
	at Main.demonstrate(Main.java:16)
	at Main.main(Main.java:9)
```

**Root cause and relevant frames:** The simulated order asks for 8 notebooks after the successful order leaves 3. `OrderProcessor.processOrder(int)` detects `quantity > availableStock` at line 25 and throws the checked `InsufficientStockException`. The caller frames show the error-case lambda, shared demonstration handler, and entry point.

**Suggested fix:** Check requested stock before subtracting anything, report the requested and available quantities, and let the caller handle the checked business failure. Do not decrement stock on a rejected order.

**Independent verdict:** Correct and verified. The stock comparison and throw occur before the decrement in `OrderProcessor`; runtime output reports 8 requested and 3 available. The `finally` audit line still runs after the failure.

The complete captured run is in `Run-Output.txt`.
