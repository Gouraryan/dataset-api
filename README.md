# Dataset API
### Backend Assignment – JSON Group By and Sort By Operators

## 1. Project Overview

**Dataset API** is a RESTful backend application developed using Java 21, Spring Boot, Spring Data JPA, and MySQL.

This project is the implementation of the assignment **Backend Assignment – JSON Group By and Sort By Operators**.

The application handles JSON dataset records through two main REST APIs:

1. **Insert Record API:** Inserts a JSON record into a specified dataset and persists it in a relational database.
2. **Query API:** Retrieves records from a specified dataset and supports dynamic Group-By and Sort-By operations.

The application supports:

- Inserting JSON records into named datasets.
- Retrieving records from a dataset.
- Grouping records dynamically using a selected field.
- Sorting records in ascending or descending order.
- Combining grouping and sorting operations.
- Persisting records in MySQL.
- Validating incoming JSON requests.
- Returning structured JSON error responses.

The APIs can be tested using Swagger UI or Postman. No frontend is required.

## 2. Live Application and Swagger Documentation

The application is publicly deployed and can be tested directly through Swagger UI.

| Resource | URL |
|---|---|
| Deployed Application | https://dataset-api-production-e7a55628.up.railway.app |
| Swagger UI – Interactive API Documentation | https://dataset-api-production-e7a55628.up.railway.app/swagger-ui/index.html |
| OpenAPI Specification | https://dataset-api-production-e7a55628.up.railway.app/v3/api-docs |
| GitHub Repository | https://github.com/Gouraryan/dataset-api |

### How to test the APIs using Swagger UI

**Step 1: Open Swagger UI**

Open the following URL:

https://dataset-api-production-e7a55628.up.railway.app/swagger-ui/index.html

Swagger UI displays the available REST API endpoints, request parameters, and response structures.

**Step 2: Insert the first employee record**

Expand:

`POST /api/dataset/{datasetName}/record`

1. Click **Try it out**.
2. Enter `employee_dataset` in the `datasetName` field.
3. Enter the following JSON in the request body:

```json
{
  "name": "John Doe",
  "age": 30,
  "department": "Engineering"
}
```

4. Click **Execute**.

Expected response:

HTTP Status: `201 Created`

```json
{
  "message": "Record added successfully",
  "dataset": "employee_dataset",
  "recordId": 1
}
```

The record ID is generated automatically. Its actual value may differ depending on existing database records.

**Step 3: Insert the second employee record**

Repeat the POST operation using the same dataset name:

`employee_dataset`

Request body:

```json
{
  "name": "Jane Smith",
  "age": 25,
  "department": "Engineering"
}
```

Click **Execute**.

**Step 4: Insert the third employee record**

Repeat the POST operation again using:

`employee_dataset`

Request body:

```json
{
  "name": "Alice Brown",
  "age": 28,
  "department": "Marketing"
}
```

Click **Execute**.

You now have three records in the same dataset, which can be used to test the assignment's Group-By and Sort-By operations.

**Important: Understanding dataset names and database tables**

You do not need to manually create an `employee_dataset` or `employees` table in MySQL.

- `employee_dataset` is a logical dataset name supplied through the API URL.
- All records are stored in the application's database table.
- The dataset name is stored with each record and used to identify which dataset it belongs to.
- Hibernate automatically creates or updates the required database table.
- You can use different dataset names, such as `employee_dataset` and `student_dataset`, without creating separate database tables.

For example:

`/api/dataset/employee_dataset/record`

and

`/api/dataset/student_dataset/record`

refer to different logical datasets managed by the same application.

**Step 5: Retrieve records**

Expand:

`GET /api/dataset/{datasetName}/query`

1. Click **Try it out**.
2. Enter `employee_dataset` in the `datasetName` field.
3. Leave the optional query parameters empty.
4. Click **Execute**.

Expected response: `200 OK`.

The API returns the records stored in the selected dataset.

**Step 6: Test Group-By**

Use the same GET endpoint.

Enter:

- `datasetName`: `employee_dataset`
- `groupBy`: `department`

Click **Execute**.

The records are grouped by department. The expected response is shown in Section 10.

**Step 7: Test Sort-By**

Use the same GET endpoint.

Enter:

- `datasetName`: `employee_dataset`
- `sortBy`: `age`
- `order`: `asc`

Click **Execute**.

The records are sorted by age in ascending order. The expected response is shown in Section 10.

**Step 8: Test Group-By and Sort-By together**

Enter:

- `datasetName`: `employee_dataset`
- `groupBy`: `department`
- `sortBy`: `age`
- `order`: `desc`

Click **Execute**.

This performs grouping by department and sorting within each group by age in descending order.

---

## 3. Original Assignment Requirements

**Assignment Name:** Backend Assignment – JSON Group By and Sort By Operators

The assignment requires a Spring Boot application that handles JSON dataset records through two APIs.

### Insert Record API

```http
POST /api/dataset/{datasetName}/record
```

The API accepts a JSON record and persists it in the specified dataset.

### Query API

```http
GET /api/dataset/{datasetName}/query
```

The API retrieves records and supports:

- Group-By using the `groupBy` query parameter.
- Sort-By using the `sortBy` and `order` query parameters.
- Combining Group-By and Sort-By operations.

### Assignment Deliverables

- A fully functional Spring Boot application.
- Java-based implementation.
- Relational database persistence.
- Public Git repository containing the source code.
- API documentation and instructions for running the application.
- REST-compliant APIs that can be tested using Postman.
- Modular code, robust error handling, and automated tests.

## 4. Technology Stack

- Java 21
- Spring Boot 3.3.5
- Spring Data JPA
- MySQL 8.x
- Maven
- JUnit 5
- Mockito
- Swagger / OpenAPI 3
- Postman

## 5. Project Features

- Insert JSON records into named datasets.
- Retrieve records from a specified dataset.
- Sort records in ascending or descending order.
- Group records based on a selected field.
- Combine grouping and sorting operations.
- Automatically generate unique record IDs.
- Persist records using MySQL.
- Validate incoming JSON requests.
- Reject null and blank values.
- Validate required fields and data types.
- Provide structured JSON error responses.
- Centralized exception handling.
- Interactive API documentation using Swagger UI.
- Automated testing using JUnit and Mockito.

## 6. Project Structure

```text
dataset-api/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/dataset/
│   │   │   ├── controller/
│   │   │   │   └── DatasetController.java
│   │   │
│   │   │   ├── dto/
│   │   │   │   ├── InsertRecordResponse.java
│   │   │   │   └── QueryResponse.java
│   │   │
│   │   │   ├── exception/
│   │   │   │   ├── DatasetNotFoundException.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── InvalidRequestException.java
│   │   │
│   │   │   ├── model/
│   │   │   │   ├── DatasetRecord.java
│   │   │   │   └── SortOrder.java
│   │   │
│   │   │   ├── repository/
│   │   │   │   └── DatasetRecordRepository.java
│   │   │
│   │   │   ├── service/
│   │   │   │   ├── DatasetService.java
│   │   │   │   ├── RecordGrouper.java
│   │   │   │   └── RecordSorter.java
│   │   │
│   │   │   └── DatasetApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/example/dataset/
│           ├── DatasetControllerTest.java
│           ├── DatasetServiceTest.java
│           ├── RecordGrouperTest.java
│           └── RecordSorterTest.java
│
├── pom.xml
└── README.md
```

## 7. Prerequisites

Install the following software before running the application locally:

1. Java Development Kit (JDK) 21
2. Apache Maven 3.9 or later
3. MySQL 8.x
4. Postman (optional, for API testing)

Verify the installations:

```bash
java -version
mvn -version
mysql --version
```

## 8. Database Setup

Start your local MySQL server.

For macOS with Homebrew:

```bash
brew services start mysql@8.4
```

For Windows, start the MySQL80 service using Windows Services or MySQL Workbench.

Log in to MySQL:

```bash
mysql -u root -p
```

Create the database:

```sql
CREATE DATABASE dataset_db;
```

Verify the database:

```sql
SHOW DATABASES;
```

Select the database:

```sql
USE dataset_db;
```

### Do I need to create the employee table manually?

No. You only need to create the database `dataset_db`.

The application uses Hibernate with the following configuration:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Hibernate automatically creates or updates the application's required database table when the application starts.

You do not need to create a separate table for `employee_dataset`, `employees`, or any other dataset name.

The dataset name is stored as part of each record and is used to retrieve records belonging to that dataset.

**Note:** Existing database tables and records are preserved when using the `update` configuration. Avoid changing it to `create` or `create-drop` for normal application runs.

## 9. Application Configuration

Open:

```text
src/main/resources/application.properties
```

Configure the database connection:

```properties
spring.application.name=dataset-api

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/dataset_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}

spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=false

spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
```

Configure your local database credentials using environment variables.

For macOS/Linux:

```bash
export DB_USER=root
export DB_PASSWORD='YOUR_MYSQL_PASSWORD'
```

For Windows PowerShell:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

Replace `YOUR_MYSQL_PASSWORD` with your local MySQL password.

Do not commit database passwords, access tokens, or other secrets to GitHub.

## 10. API Reference

Base URL for local execution:

```text
http://localhost:8080
```

### API 1: Insert Record API

**HTTP Method:** POST

**Request URL:**

```http
POST /api/dataset/{datasetName}/record
```

**Example URL:**

```http
POST /api/dataset/employee_dataset/record
```

**Request Header:**

```text
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "John Doe",
  "age": 30,
  "department": "Engineering"
}
```

**Successful Response:**

HTTP Status: `201 Created`

```json
{
  "message": "Record added successfully",
  "dataset": "employee_dataset",
  "recordId": 1
}
```

The record ID is generated automatically by the database. It does not need to be supplied in the request body.

**Validation Rules:**

- Dataset name must contain 1–64 characters.
- Dataset name can contain letters, digits, underscores, and hyphens.
- `name` is required and must be a non-empty string.
- `department` is required and must be a non-empty string.
- `age` is optional but must be a non-negative whole number when provided.
- Null values are not allowed.
- Empty or whitespace-only strings are not allowed.
- Additional JSON fields are accepted during insertion.

### API 2: Query API – Retrieve Records

**HTTP Method:** GET

**Request URL:**

```http
GET /api/dataset/{datasetName}/query
```

**Example URL:**

```http
GET /api/dataset/employee_dataset/query
```

**Successful Response:**

HTTP Status: `200 OK`

The API returns the records stored in the requested dataset. The current response format includes `id`, `name`, `age`, and `department`. Other stored fields are not included in this response format.

### API 3: Query API with Group-By

**Purpose:** Group records based on a selected JSON field.

**Request URL:**

```http
GET /api/dataset/{datasetName}/query
```

**Example URL:**

```http
GET /api/dataset/employee_dataset/query
```

**Query Parameter:**

```text
groupBy=department
```

**Complete Request:**

```http
GET /api/dataset/employee_dataset/query?groupBy=department
```

**Expected Response:**

HTTP Status: `200 OK`

```json
{
  "groupedRecords": {
    "Engineering": [
      {
        "id": 1,
        "name": "John Doe",
        "age": 30,
        "department": "Engineering"
      },
      {
        "id": 2,
        "name": "Jane Smith",
        "age": 25,
        "department": "Engineering"
      }
    ],
    "Marketing": [
      {
        "id": 3,
        "name": "Alice Brown",
        "age": 28,
        "department": "Marketing"
      }
    ]
  }
}
```

**Explanation:**

- The `groupBy` parameter specifies the field used for grouping.
- In this example, records are grouped using the `department` field.
- Records with the same department are placed in the same group.
- Each group contains the matching records.

### API 4: Query API with Sort-By

**Purpose:** Sort records using a selected field and sorting direction.

**Request URL:**

```http
GET /api/dataset/{datasetName}/query
```

**Example URL:**

```http
GET /api/dataset/employee_dataset/query
```

**Query Parameters:**

```text
sortBy=age&order=asc
```

**Complete Request:**

```http
GET /api/dataset/employee_dataset/query?sortBy=age&order=asc
```

**Expected Response:**

HTTP Status: `200 OK`

```json
{
  "sortedRecords": [
    {
      "id": 2,
      "name": "Jane Smith",
      "age": 25,
      "department": "Engineering"
    },
    {
      "id": 3,
      "name": "Alice Brown",
      "age": 28,
      "department": "Marketing"
    },
    {
      "id": 1,
      "name": "John Doe",
      "age": 30,
      "department": "Engineering"
    }
  ]
}
```

**Explanation:**

- `sortBy` specifies the field used for sorting.
- `order` specifies the sorting direction.
- `asc` sorts in ascending order.
- `desc` sorts in descending order.
- If `order` is omitted, ascending order is used by default.

The implementation supports numeric comparison for numeric values and case-insensitive text comparison for other values.

### API 5: Combine Group-By and Sort-By

Grouping and sorting can also be performed together using the same Query API.

**Example Request:**

```http
GET /api/dataset/employee_dataset/query?groupBy=department&sortBy=age&order=desc
```

**Parameters:**

| Parameter | Description |
|---|---|
| `groupBy` | Field used to group records |
| `sortBy` | Field used to sort records |
| `order` | Sorting direction: `asc` or `desc` |

This operation groups records by department and sorts the records within each group by age in descending order.

### Supported Query Parameters

| Parameter | Purpose | Example |
|---|---|---|
| `groupBy` | Groups records using a selected field | `groupBy=department` |
| `sortBy` | Sorts records using a selected field | `sortBy=age` |
| `order` | Defines sorting direction | `order=asc` or `order=desc` |

## 11. Error Handling

The application uses centralized exception handling through `GlobalExceptionHandler`.

Errors are returned as structured JSON responses.

**Example: Invalid Request**

HTTP Status: `400 Bad Request`

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Field 'name' is required",
  "timestamp": "2026-11-19T14:30:00Z"
}
```

**Supported HTTP Status Codes:**

| HTTP Status | Description |
|---|---|
| 400 Bad Request | Invalid input, malformed JSON, or validation failure |
| 404 Not Found | Dataset with no matching records or unknown endpoint |
| 405 Method Not Allowed | Unsupported HTTP method |
| 500 Internal Server Error | Unexpected server-side error |

## 12. Testing

### Manual API Testing

The application can be tested using Swagger UI or Postman.

Recommended test sequence:

1. Insert a valid employee record.
2. Insert additional records into the same dataset.
3. Retrieve all records from the dataset.
4. Test grouping by department.
5. Test sorting by age in ascending order.
6. Test sorting by age in descending order.
7. Test grouping and sorting together.
8. Test invalid dataset names.
9. Test missing required fields.
10. Test null and blank values.
11. Test invalid age values.
12. Test malformed JSON.
13. Test unsupported HTTP methods.
14. Test invalid sorting and grouping fields.

### Automated Testing

The project uses:

- JUnit 5 for automated tests.
- Mockito for mocking repository dependencies.
- Spring Boot Test for controller testing.

Run the test suite:

```bash
mvn clean test
```

The test classes include:

- `DatasetControllerTest`
- `DatasetServiceTest`
- `RecordSorterTest`
- `RecordGrouperTest`

The previously executed test suite completed with 27 tests, 0 failures, and 0 errors.

## 13. Build the Application

To generate the executable JAR:

```bash
mvn clean package
```

The generated JAR will be available inside:

```text
target/
```

Run the packaged application:

```bash
java -jar target/dataset-api-1.0.0.jar
```

Ensure that the database environment variables are configured before running the JAR.

## 14. Design and Architecture

The application follows a layered architecture to separate responsibilities and maintain modularity.

| Layer / Component | Responsibility |
|---|---|
| Controller | Handles HTTP requests and responses |
| Service | Contains business logic and validation |
| Repository | Handles database operations |
| Model | Represents database entities |
| DTO | Defines API response structures |
| Exception | Handles application errors |
| RecordSorter | Implements sorting functionality |
| RecordGrouper | Implements grouping functionality |

The project uses dependency injection and the Spring Data JPA repository pattern to improve separation of concerns and testability.

## 15. Future Improvements

Potential enhancements include:

- Pagination for large datasets.
- More flexible response-field selection.
- API versioning.
- Authentication and authorization.
- Additional validation and integration tests.

## 16. Conclusion

Dataset API is a Java-based REST backend developed to fulfil the **Backend Assignment – JSON Group By and Sort By Operators**.

It provides two main APIs to insert and retrieve JSON records, with dynamic Group-By and Sort-By functionality.

The application uses Spring Boot, MySQL, layered architecture, centralized exception handling, automated testing, and Swagger/OpenAPI documentation.

The deployed application can be tested directly through Swagger UI or Postman without requiring a frontend interface.
