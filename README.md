# hermes-credit

The credit service of [Hermes](https://github.com/Batchuka/spec-hermes), a demo of an equipment financing process. It scores a proposal and records the analyst's decision (approval or rejection, with a reason).

Analyses are structured and every decision must be kept, so they live in **MySQL**.

| | |
| --- | --- |
| Database | MySQL, schema `credit` |
| Starts with | UC-02: score a proposal and record the decision (#1) |

## Run locally

Start MySQL first ([hermes-infra](https://github.com/Batchuka/hermes-infra), folder `local`). Then:

```powershell
./mvnw spring-boot:run
```

## Work

Issues and versions are on the [Hermes project](https://github.com/users/Batchuka/projects/13). The workflow is in [spec-hermes](https://github.com/Batchuka/spec-hermes/blob/main/WORKFLOW.md).
