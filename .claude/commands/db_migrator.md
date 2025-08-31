# Purpose

This command helps to create migration file for database schema changes.
Database Engine is PostgresSQL 15, and backend is using Flyway for version control.

# Flow

### 1. Verify if there are any changes in the model in the project

- Get local diff with remote current branch
- Get diff with main branch
- If there are changes in model files which are stored in @be/src/main/kotlin/ai/programujz/demo/domain/model get the
  difference between current state and previous.

### 2. Verify current version number

- Check folder /be/src/main/resources/db/migration
- Get the latest version number from the migration files

Naming convention for migration files is V<version>__<description>.sql
Where version is in format major.minor.patch.

### 3. Create new migration files

Decide how many migration files are needed based on the changes in the model.
Keep each Model change in separate migration file.
For each migration file:

- Increment the version number (e.g. if last was V1.0.0, next should be V1.0.1 for patch version)
- Create descriptive but simple description for the migration file

### 4. Create SQL statements

Migration conventions:

#### Use UUID as primary key for all tables (use extension uuid-ossp)

```postgresql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE table_name
(
    id   uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL
);
```

#### Inline references

```postgresql
CREATE TABLE orders
(
    id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id uuid REFERENCES customers (id)
)
```

#### If there is update on the Repository - Search - Consider creating index on the column

#### Class that is extending Auditable should have columns created_at, updated_at, created_by


