# Разработать консольную программу, которая:

* принимая на вход .csv файл определённого формата, разбирает его и сохраняет содержимое в базу данных;
* по команде выводит на экран данные всех сотрудников, имеющиеся в базе.

# При реализации нужно учесть следующее требования:

* данные в базе должны храниться в двух или более таблицах
* для вывода данных на экран должно происходить не более одного обращения к базе данных
* при реализации могут использоваться любые библиотеки/фреймворки/технологии (могут не использоваться)
* несмотря на то, что задача тестовая, код должен быть таким же, как если бы завтра он шёл в продакшен

# Пример:

## Шаг 1: на вход подаётся файл file1.csv:

First Name;Last Name;Employee type(M for manager, D for developer);Project;Technology John;Smith;M;Everything
Everywhere; Martin;Fowler;D;;Java

## Шаг 2: на вход подаётся файл file2.csv

First Name;Last Name;Employee type(M for manager, D for developer);Project;Technology Joel;Spolski;D;;Kotlin
John;Doe;M;British Telecom William;Gates;D;;.NET

## Шаг 3: даётся команда на вывод всех сотрудников. Ожидаемый результат:

John Smith, manager, Everything Everywhere Martin Fowler, developer, Java Joel Spolski, developer, Kotlin John Doe,
manager, British Telecom William Gates, developer, .NET


