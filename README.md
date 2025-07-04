# Employee Management Web Application

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/Sai-kishore-veeranki/Employee_management_system_2/main?label=build)
![GitHub issues](https://img.shields.io/github/issues/Sai-kishore-veeranki/Employee_management_system_2)
![Last Commit](https://img.shields.io/github/last-commit/Sai-kishore-veeranki/Employee_management_system_2)
<!-- Replace YOUR_USERNAME and YOUR_REPOSITORY with your actual GitHub details -->

A robust Spring Boot application designed to efficiently manage employee data, including registration, viewing, adding, updating, and deleting employee records. It features secure user authentication and authorization using JWT.

## 📖 Overview

This application provides a RESTful API for managing employee information. Key functionalities include:

*   **User Registration:** New users can register with their details.
*   **User Authentication:** Registered users can log in to obtain a JSON Web Token (JWT) for secure access to protected resources.
*   **Employee Management:** Authenticated users can perform CRUD (Create, Read, Update, Delete) operations on employee records.
*   **Pagination:** Employee listings support pagination for efficient data retrieval.

## 🚀 Getting Started

These instructions will guide you through setting up and running the Employee Management Web Application on your local machine.

### Prerequisites

Ensure you have the following software installed on your system:

*   **Java Development Kit (JDK):** Version 21 or higher.
    *   [Download JDK](https://www.oracle.com/java/technologies/downloads/)
*   **Maven:** Version 3.6.3 or higher.
    *   [Download Maven](https://maven.apache.org/download.cgi)
*   **MySQL Database:** Version 8.0 or higher.
    *   [Download MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
*   **Git:** For cloning the repository.
    *   [Download Git](https://git-scm.com/downloads)

### Database Setup

1.  **Create a MySQL Database:**
    Open your MySQL client (e.g., MySQL Workbench, command line) and create a new database.
    ```sql
    CREATE DATABASE employee_management_db;
    ```

2.  **Configure Database Connection:**
    Locate the `application.properties` file in `src/main/resources/application.properties`. Update the following properties with your MySQL database credentials:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/employee_management_db?useSSL=false&serverTimezone=UTC
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password
    spring.jpa.hibernate.ddl-auto=update # This will automatically create tables
    ```
    *Note: `spring.jpa.hibernate.ddl-auto=update` is convenient for development as it creates/updates tables automatically. For production, consider `validate` or `none` and use Flyway/Liquibase for schema migrations.*

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
    cd employee-management-webapp
    ```
    *Replace `YOUR_USERNAME` and `YOUR_REPOSITORY` with the actual GitHub username and repository name.*

2.  **Build the project:**
    Navigate to the project's root directory (`employee-management-webapp`) and build the application using Maven:
    ```bash
    mvn clean install
    ```
    This command compiles the code, runs tests, and packages the application into a JAR file in the `target/` directory.

### Running the Application

After successful installation, you can run the application:

1.  **Execute the JAR file:**
    ```bash
    java -jar target/employee-management-webapp-0.0.1-SNAPSHOT.jar
    ```
    The application will start on port `8080` by default. You should see logs indicating that Spring Boot has started.

## 💡 How to Use and Access the API

The application exposes a set of RESTful endpoints. You can interact with these endpoints using tools like Postman, Insomnia, or `curl`.

### 1. User Registration

*   **Endpoint:** `POST /api/register`
*   **Description:** Register a new user account.
*   **Request Body (JSON):**
    ```json
    {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "password": "securepassword123"
    }
    ```
*   **Example `curl` command:**
    ```bash
    curl -X POST http://localhost:8080/api/register \
    -H "Content-Type: application/json" \
    -d '{
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "password": "securepassword123"
    }'
    ```

### 2. User Login (Obtain JWT)

*   **Endpoint:** `POST /api/auth/login`
*   **Description:** Authenticate a user and receive a JWT for subsequent requests.
*   **Request Body (JSON):**
    ```json
    {
        "email": "john.doe@example.com",
        "password": "securepassword123"
    }
    ```
*   **Example `curl` command:**
    ```bash
    curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
        "email": "john.doe@example.com",
        "password": "securepassword123"
    }'
    ```
*   **Response:** You will receive a JSON response containing the JWT. Copy this token.
    ```json
    {
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTY3ODkwNTYwMCwiZXhwIjoxNjc4OTA5MjAwfQ.YOUR_JWT_TOKEN_HERE",
        "type": "Bearer"
    }
    ```

### 3. Accessing Protected Endpoints (Employee Management)

For all employee management endpoints, you must include the obtained JWT in the `Authorization` header of your requests.

*   **Header:** `Authorization: Bearer YOUR_JWT_TOKEN`
    *Replace `YOUR_JWT_TOKEN` with the actual token you received from the login endpoint.*

#### **Add New Employee**

*   **Endpoint:** `POST /api/employees`
*   **Description:** Create a new employee record.
*   **Request Body (JSON):**
    ```json
    {
        "firstName": "Jane",
        "lastName": "Smith",
        "email": "jane.smith@example.com"
    }
    ```
*   **Example `curl` command:**
    ```bash
    curl -X POST http://localhost:8080/api/employees \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer YOUR_JWT_TOKEN" \
    -d '{
        "firstName": "Jane",
        "lastName": "Smith",
        "email": "jane.smith@example.com"
    }'
    ```

#### **Get All Employees (Paginated)**

*   **Endpoint:** `GET /api/employees`
*   **Description:** Retrieve a paginated list of all employee records.
*   **Query Parameters (Optional):**
    *   `page`: Page number (0-indexed, default 0)
    *   `size`: Number of records per page (default 10)
    *   `sort`: Sorting criteria (e.g., `firstName,asc` or `email,desc`)
*   **Example `curl` command:**
    ```bash
    curl -X GET "http://localhost:8080/api/employees?page=0&size=5&sort=lastName,asc" \
    -H "Authorization: Bearer YOUR_JWT_TOKEN"
    ```

#### **Get Employee by ID**

*   **Endpoint:** `GET /api/employees/{id}`
*   **Description:** Retrieve a single employee record by their ID.
*   **Example `curl` command:**
    ```bash
    curl -X GET http://localhost:8080/api/employees/1 \
    -H "Authorization: Bearer YOUR_JWT_TOKEN"
    ```

#### **Update Employee**

*   **Endpoint:** `PUT /api/employees/{id}`
*   **Description:** Update an existing employee record.
*   **Request Body (JSON):**
    ```json
    {
        "firstName": "Janet",
        "lastName": "Smith",
        "email": "janet.smith@example.com"
    }
    ```
*   **Example `curl` command:**
    ```bash
    curl -X PUT http://localhost:8080/api/employees/1 \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer YOUR_JWT_TOKEN" \
    -d '{
        "firstName": "Janet",
        "lastName": "Smith",
        "email": "janet.smith@example.com"
    }'
    ```

#### **Delete Employee**

*   **Endpoint:** `DELETE /api/employees/{id}`
*   **Description:** Delete an employee record by their ID.
*   **Example `curl` command:**
    ```bash
    curl -X DELETE http://localhost:8080/api/employees/1 \
    -H "Authorization: Bearer YOUR_JWT_TOKEN"
    ```

### 🌐 Swagger UI (API Documentation)

For interactive API documentation, you can access the Swagger UI once the application is running:

*   **URL:** `http://localhost:8080/swagger-ui.html`

This interface allows you to explore all available endpoints, their request/response schemas, and even test them directly from your browser.