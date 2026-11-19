# Dataset API (Spring Boot + MySQL)

Insert JSON records into named datasets and query them with dynamic group-by / sort-by.

## Prerequisites
- Java 21, Maven 3.9+, MySQL 8+

## Run
1. Start MySQL (the database `dataset_db` is auto-created).
2. Set credentials if not root/root:
   - Windows PowerShell: `$env:DB_USER="root"; $env:DB_PASSWORD="yourpass"`
   - macOS/Linux: `export DB_USER=root DB_PASSWORD=yourpass`
3. `mvn spring-boot:run` (app runs on http://localhost:8080)
4. Tests: `mvn test`

## Endpoints

### POST /api/dataset/{datasetName}/record
Body: any JSON object. Returns `201`:
```json
{ "message": "Record added successfully", "dataset": "employee_dataset", "recordId": 1 }
```

### GET /api/dataset/{datasetName}/query
| Param   | Required | Notes                          |
|---------|----------|--------------------------------|
| groupBy | no       | field name -> `groupedRecords` |
| sortBy  | no       | field name -> `sortedRecords`  |
| order   | no       | `asc` (default) or `desc`      |

If both groupBy and sortBy are given, records are grouped and each group is sorted.
With neither, all records are returned under `records`.

### Errors
All errors return `{status, error, message, timestamp}`:
`400` invalid JSON / dataset name / order, `404` unknown dataset, `500` unexpected.

## Design
Controller -> Service -> Repository, with `RecordGrouper` and `RecordSorter` as separate single-responsibility components. Global error handling via `@RestControllerAdvice`.
