# Develop console app that:

* expecting .csv file of specific format, parsing it and saving result into the DB;
* on command will output all saved employees in DB

# Requirements:

* two or more relational tables
* for output app should use only one query
* during implementation any libraries / frameworks / technologies can be used (may not be used)
* in the result expecting production ready code

# Example:

## Step 1: input file1.csv:

First Name;Last Name;Employee type(M for manager, D for developer);Project;Technology John;Smith;M;Everything
Everywhere; Martin;Fowler;D;;Java

## Step 2: input file2.csv

First Name;Last Name;Employee type(M for manager, D for developer);Project;Technology Joel;Spolski;D;;Kotlin
John;Doe;M;British Telecom William;Gates;D;;.NET

## Step 3: command to output all employees. Expected result:

John Smith, manager, Everything Everywhere Martin Fowler, developer, Java Joel Spolski, developer, Kotlin John Doe,
manager, British Telecom William Gates, developer, .NET


