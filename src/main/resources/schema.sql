-- Create person table if not exists
CREATE TABLE IF NOT EXISTS person (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    age INTEGER,
    department_id VARCHAR(255),
    PRIMARY KEY (id)
);

-- Create department table
CREATE TABLE IF NOT EXISTS department (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id)
);

-- Add foreign key constraint if it doesn't exist
-- Note: H2 doesn't support IF NOT EXISTS for constraints, so this might fail on repeated runs
-- In a real application, use a migration tool like Flyway or Liquibase
ALTER TABLE person 
    ADD CONSTRAINT IF NOT EXISTS fk_person_department 
    FOREIGN KEY (department_id) 
    REFERENCES department(id); 