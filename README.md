# batch-demo

This Application demonstrates the spring batching application.

It has 3 steps. Out of which 2 steps (Step-1 & Step-2) run parallelly and 3rd flow runs once the
parallel flow completes.

| Task Name | Predecessor       |
|:----------|:------------------|
| `Step-1`  |                   |
| `Step-2`  |                   |
| `Step-3`  | `Step-1 & Step-2` |

