# ArrayList vs LinkedList timing run

Captured from the actual `LibraryWaitlist` run on 2026-10-12. Each operation was measured once after list setup; these are illustrative results, not a benchmark guarantee.

| Operation | ArrayList (ms) | LinkedList (ms) |
| --- | ---: | ---: |
| Insert 2,000 items at index 0 into a list starting with 8,000 items | 6.414 | 0.727 |
| Read all 4,000 positions using `get(index)` | 0.855 | 13.991 |

The insertion case favors linked-list insertion at the front; indexed access favors `ArrayList`. JVM warmup, machine load, and hardware affect these measurements. `Run-Output.txt` preserves the exact printed timing from this run.
