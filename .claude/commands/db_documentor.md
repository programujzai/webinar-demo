# Purpose

This command build focused documentation of current database schema.

Engine: PostgresSQL 15

# Flow:

### 1. Get connectivity parameters from @/be/src/main/resources/application-local.yml

### 2. Use bash script to connect to database and run required queries to get all information about current state of database schema

### 3. Documentation requirements:

- List of all schemas,
- All schemas tables,
- All tables columns, constrains, indexes

EXCLUDE_SCHEMAS: (
'db_owner',
'db_accessadmin',
'db_securityadmin',
'db_ddladmin',
'db_datareader',
'db_datawriter',
'db_denydatareader',
'db_denydatawriter',
'guest',
'INFORMATION_SCHEMA',
'sys'
)

### 4. Format the data to shape of readable, and accessible markdown

### 5. Write into AI_DOCS/db_documentation.md