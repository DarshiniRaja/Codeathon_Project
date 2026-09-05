# Employee / Student Skill Gap Analyzer — Backend

Complete production-ready Spring Boot backend for the Employee / Student Skill Gap Analyzer system.

---

## 1. Architecture & Tech Stack

* **Language**: Java 21 / 25
* **Framework**: Spring Boot 4.x / 3.x compatible
* **Modules**:
  * Spring Web (REST API controllers, exception handling)
  * Spring Data JPA & Hibernate (ORM, relational queries)
  * MySQL Connector / J (MySQL 8.0 support)
  * Spring Boot Test & JUnit 5 (comprehensive unit and integration test suite)
* **Build Tool**: Maven / Maven Wrapper

### Layered Architecture

```text
Controller Layer (thin REST API boundaries, status codes & DTO responses)
      ↓
Service Layer (business logic, validation, deterministic gap & match calculation)
      ↓
Repository Layer (Spring Data JPA data access interfaces)
      ↓
Entity Layer (JPA entities with relational mapping & constraints)
      ↓
MySQL Database (skill_gap_db)
```

---

## 2. Database Domain Model

### Entities & Relational Design

1. **`students`**:
   * `id` (BIGINT, Primary Key, Auto Increment)
   * `name` (VARCHAR, NOT NULL)
   * `email` (VARCHAR, NOT NULL, UNIQUE)
   * Relationship: `@OneToMany` with `StudentSkill`
2. **`skills`**:
   * `id` (BIGINT, Primary Key, Auto Increment)
   * `name` (VARCHAR, NOT NULL, UNIQUE)
3. **`student_skills`**:
   * `id` (BIGINT, Primary Key, Auto Increment)
   * `student_id` (BIGINT, Foreign Key -> `students.id`)
   * `skill_id` (BIGINT, Foreign Key -> `skills.id`)
   * `proficiency` (INT, 1 to 5)
   * Constraint: `UNIQUE (student_id, skill_id)`
4. **`jobs`**:
   * `id` (BIGINT, Primary Key, Auto Increment)
   * `title` (VARCHAR, NOT NULL)
   * `description` (VARCHAR(1000))
   * Relationship: `@OneToMany` with `JobSkill`
5. **`job_skills`**:
   * `id` (BIGINT, Primary Key, Auto Increment)
   * `job_id` (BIGINT, Foreign Key -> `jobs.id`)
   * `skill_id` (BIGINT, Foreign Key -> `skills.id`)
   * `required_level` (INT, 1 to 5)
   * `mandatory` (BOOLEAN, default false)
   * Constraint: `UNIQUE (job_id, skill_id)`
6. **`applications`**:
   * `id` (BIGINT, Primary Key, Auto Increment)
   * `student_id` (BIGINT, Foreign Key -> `students.id`)
   * `job_id` (BIGINT, Foreign Key -> `jobs.id`)
   * `match_percent` (DOUBLE, 2 decimal precision)
   * `status` (VARCHAR, e.g. "APPLIED")
   * `created_at` (DATETIME, NOT NULL)
7. **`recommendations`**:
   * `id` (BIGINT, Primary Key, Auto Increment)
   * `student_id` (BIGINT, Foreign Key -> `students.id`)
   * `job_id` (BIGINT, Foreign Key -> `jobs.id`)
   * `skill_name` (VARCHAR, NOT NULL)
   * `current_level` (INT)
   * `required_level` (INT)
   * `priority` (VARCHAR: "HIGH" | "MEDIUM")
   * `reason` (VARCHAR(1000))
   * `created_at` (DATETIME, NOT NULL)

---

## 3. Skill Gap & Match Percentage Calculation

### 3.1 Gap Formula
For every required skill defined on a job:
* If student has the skill: `currentLevel = studentProficiency` (1 to 5)
* If student does not have the skill: `currentLevel = 0`
* `gap = max(requiredLevel - currentLevel, 0)`
* Status:
  * If `currentLevel >= requiredLevel`: `status = "MATCHED"`
  * If `currentLevel < requiredLevel`: `status = "GAP"`

### 3.2 Match Percentage Formula (Deterministic Weighted Formula)
For each required skill on the job:
$$\text{achievement} = \min\left(\frac{\text{currentLevel}}{\text{requiredLevel}}, 1.0\right)$$

Skill Weights:
* **Mandatory Skill (`mandatory == true`)**: $\text{weight} = 2.0$
* **Optional Skill (`mandatory == false`)**: $\text{weight} = 1.0$

Score Calculation:
$$\text{weightedScore} = \sum (\text{achievement} \times \text{weight})$$
$$\text{totalWeight} = \sum \text{weight}$$

$$\text{matchPercent} = \begin{cases} 0.0 & \text{if } \text{totalWeight} = 0 \\ \text{round}\left(\frac{\text{weightedScore}}{\text{totalWeight}} \times 100, 2\right) & \text{otherwise} \end{cases}$$

#### Verified Example (from prompt specifications):
* **Java** (mandatory = true, weight = 2): current = 4, required = 5
  * $\text{achievement} = 4 / 5 = 0.8$, $\text{score} = 0.8 \times 2 = 1.6$
* **SQL** (mandatory = true, weight = 2): current = 5, required = 5
  * $\text{achievement} = 5 / 5 = 1.0$, $\text{score} = 1.0 \times 2 = 2.0$
* **HTML** (mandatory = false, weight = 1): current = 3, required = 4
  * $\text{achievement} = 3 / 4 = 0.75$, $\text{score} = 0.75 \times 1 = 0.75$

* $\text{Total Score} = 1.6 + 2.0 + 0.75 = 4.35$
* $\text{Total Weight} = 2 + 2 + 1 = 5.0$
* $\text{Match Percent} = \frac{4.35}{5.0} \times 100 = 87.0\%$

---

## 4. Recommendation Priority & Dynamic Reason

Recommendations are generated **only** for skills where $\text{currentLevel} < \text{requiredLevel}$.

### Priority:
* If skill is mandatory: `"HIGH"`
* If skill is optional: `"MEDIUM"`

### Dynamic Reason:
```text
"Current {SkillName} level is {currentLevel} but the required level is {requiredLevel}. Improve {SkillName} proficiency by {gap} {level/levels}."
```

---

## 5. REST API Documentation

### Common Error Response Format
All errors return a consistent JSON response:
```json
{
  "timestamp": "2026-09-05T12:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Student with id 10 not found",
  "path": "/api/students/10"
}
```

---

### Student Endpoints

#### `POST /api/students`
* **Status**: `201 Created`
* **Request**:
  ```json
  {
    "name": "Ravi Kumar",
    "email": "ravi@example.com"
  }
  ```
* **Response**:
  ```json
  {
    "id": 1,
    "name": "Ravi Kumar",
    "email": "ravi@example.com"
  }
  ```
* **Validation**:
  * Name cannot be empty (`400 Bad Request`)
  * Email cannot be empty and must be valid format (`400 Bad Request`)
  * Email must be unique (`400 Bad Request` / Duplicate)

#### `GET /api/students`
* **Status**: `200 OK`
* **Response**: List of `StudentResponseDTO`

#### `GET /api/students/{id}`
* **Status**: `200 OK`
* **Errors**: `404 Not Found` if student does not exist.

---

### Skill Endpoints

#### `POST /api/skills`
* **Status**: `201 Created`
* **Request**:
  ```json
  {
    "name": "Java"
  }
  ```
* **Response**:
  ```json
  {
    "id": 1,
    "name": "Java"
  }
  ```

#### `GET /api/skills`
* **Status**: `200 OK`
* **Response**: List of `SkillResponseDTO`

#### `GET /api/skills/{id}`
* **Status**: `200 OK`
* **Errors**: `404 Not Found` if skill does not exist.

---

### Student Skill Endpoints

#### `POST /api/students/{id}/skills`
* **Status**: `201 Created`
* **Request**:
  ```json
  {
    "skillId": 1,
    "proficiency": 4
  }
  ```
* **Response**:
  ```json
  {
    "id": 1,
    "studentId": 1,
    "skillId": 1,
    "skillName": "Java",
    "proficiency": 4
  }
  ```
* **Validation**:
  * Proficiency must be between 1 and 5 (`400 Bad Request`)
  * Student must exist (`404 Not Found`)
  * Skill must exist (`404 Not Found`)
  * Duplicate assignment rejected (`400 Bad Request`)

#### `GET /api/students/{id}/skills`
* **Status**: `200 OK`
* **Response**: List of `StudentSkillResponseDTO`
* **Errors**: `404 Not Found` if student does not exist.

---

### Job Endpoints

#### `POST /api/jobs`
* **Status**: `201 Created`
* **Request**:
  ```json
  {
    "title": "Java Backend Developer",
    "description": "Develop Spring Boot REST APIs"
  }
  ```
* **Response**:
  ```json
  {
    "id": 1,
    "title": "Java Backend Developer",
    "description": "Develop Spring Boot REST APIs"
  }
  ```

#### `GET /api/jobs`
* **Status**: `200 OK`
* **Response**: List of `JobResponseDTO` with embedded required skills.

#### `GET /api/jobs/{id}`
* **Status**: `200 OK`
* **Response**: `JobResponseDTO` including list of required skills.
* **Errors**: `404 Not Found` if job does not exist.

---

### Job Required Skills Endpoints

#### `POST /api/jobs/{id}/skills`
* **Status**: `201 Created`
* **Request**:
  ```json
  {
    "skillId": 1,
    "requiredLevel": 5,
    "mandatory": true
  }
  ```
* **Response**:
  ```json
  {
    "id": 1,
    "jobId": 1,
    "skillId": 1,
    "skillName": "Java",
    "requiredLevel": 5,
    "mandatory": true
  }
  ```
* **Validation**:
  * Required level must be between 1 and 5 (`400 Bad Request`)
  * Job must exist (`404 Not Found`)
  * Skill must exist (`404 Not Found`)
  * Duplicate skill assignment rejected (`400 Bad Request`)

#### `GET /api/jobs/{id}/skills`
* **Status**: `200 OK`
* **Response**: List of `JobSkillResponseDTO`

---

### Skill Gap Analysis Endpoint

#### `GET /api/students/{studentId}/jobs/{jobId}/skill-gap`
* **Status**: `200 OK`
* **Response**:
  ```json
  {
    "studentId": 1,
    "jobId": 10,
    "matchPercent": 76.5,
    "skills": [
      {
        "skill": "Java",
        "currentLevel": 4,
        "requiredLevel": 5,
        "gap": 1,
        "status": "GAP",
        "mandatory": true
      },
      {
        "skill": "SQL",
        "currentLevel": 5,
        "requiredLevel": 4,
        "gap": 0,
        "status": "MATCHED",
        "mandatory": true
      }
    ]
  }
  ```
* **Errors**:
  * `404 Not Found` if student or job does not exist.

---

### Recommendations Endpoint

#### `GET /api/students/{studentId}/jobs/{jobId}/recommendations`
* **Status**: `200 OK`
* **Response**:
  ```json
  [
    {
      "skill": "Java",
      "currentLevel": 3,
      "requiredLevel": 5,
      "priority": "HIGH",
      "reason": "Current Java level is 3 but the required level is 5. Improve Java proficiency by 2 levels."
    }
  ]
  ```
* **Errors**:
  * `404 Not Found` if student or job does not exist.

---

### Application Endpoints

#### `POST /api/applications`
* **Status**: `201 Created`
* **Request**:
  ```json
  {
    "studentId": 1,
    "jobId": 10
  }
  ```
* **Response**:
  ```json
  {
    "id": 25,
    "studentId": 1,
    "jobId": 10,
    "matchPercent": 76.5,
    "status": "APPLIED"
  }
  ```
* **Errors**:
  * `404 Not Found` if student or job does not exist.
  * `400 Bad Request` if duplicate application or invalid payload.

#### `GET /api/applications`
* **Status**: `200 OK`
* **Response**: List of `ApplicationResponseDTO`

#### `GET /api/applications/{id}`
* **Status**: `200 OK`
* **Errors**: `404 Not Found` if application does not exist.

---

## 6. How to Build & Run

### Prerequisites
* Java 21+ installed
* MySQL 8.0 running on localhost:3306 with database `skill_gap_db`

### Running Tests
```bash
./mvnw clean test
```

### Packaging Runnable JAR
```bash
./mvnw clean package
```

### Running the Application
```bash
java -jar target/skillgap-0.0.1-SNAPSHOT.jar
```
Or via Maven:
```bash
./mvnw spring-boot:run
```
The application will start on port `8080` and seed initial demo data if the database is fresh.
