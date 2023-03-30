CREATE DATABASE employeesdb;

\c employeesdb;

CREATE TABLE employees (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255),
  surname VARCHAR(255),
  date_of_birth DATE,
  salary NUMERIC(10, 2),
  phone_number VARCHAR(15),
  job VARCHAR(255),
  title VARCHAR(255)
);

INSERT INTO employees (name, surname, date_of_birth, salary, phone_number, job, title)
VALUES ('John', 'Doe', '1980-03-21', 80000.00, '555-123-4567', 'Software Engineer', 'Senior Engineer'),
       ('Jane', 'Smith', '1985-08-15', 75000.00, '555-987-6543', 'Data Analyst', 'Analyst'),
       ('Alice', 'Johnson', '1990-06-11', 90000.00, '555-444-5555', 'Product Manager', 'Manager'),
       ('Bob', 'Brown', '1983-12-05', 95000.00, '555-222-3333', 'UX Designer', 'Senior Designer'),
       ('Charlie', 'Davis', '1978-11-30', 100000.00, '555-111-9999', 'CTO', 'Executive'),
       ('Diana', 'Garcia', '1992-04-16', 70000.00, '555-888-7777', 'Web Developer', 'Developer'),
       ('Eva', 'Miller', '1987-07-03', 85000.00, '555-666-5555', 'QA Engineer', 'Senior QA'),
       ('Frank', 'Wilson', '1982-10-25', 60000.00, '555-333-2222', 'Technical Support', 'Support Specialist'),
       ('Grace', 'Rodriguez', '1993-02-09', 55000.00, '555-777-8888', 'Marketing Specialist', 'Specialist'),
       ('Henry', 'Martinez', '1989-09-27', 65000.00, '555-666-9999', 'Sales Manager', 'Manager');