CREATE TABLE IF NOT EXISTS employee (
	id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	first_name VARCHAR NOT NULL,
	last_name VARCHAR NOT NULL,
	employee_type INTEGER NOT NULL,
	project_fk INTEGER
);

CREATE TABLE IF NOT EXISTS skill (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS project (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS employee2skill (
	employee_id INTEGER NOT NULL,
	skill_id INTEGER NOT NULL,
	foreign key (employee_id) REFERENCES employee(id),
	foreign key (skill_id) REFERENCES skill(id)
);

CREATE INDEX IF NOT EXISTS project_project_name ON project(project_name);
CREATE INDEX IF NOT EXISTS skill_skill_name ON skill(skill_name);
