# Day 3 - Control Flow + Maven

## Topics Covered

- if-else and switch
- while, do-while, for and enhanced for loops
- break and continue
- Maven project structure
- pom.xml
- Maven lifecycle
- Maven profiles

## Hands-on

### ATM Simulator

Implemented an ATM simulator using:

- do-while menu
- switch-case
- input validation
- PIN attempts with break
- continue for invalid input
- enhanced for loop for mini statement

## Project Task

Converted the Personal Expense & Budget Tracker into a Maven project.

Implemented a console menu with options for all 8 functional requirements:

1. Create Account
2. Manage Transactions
3. View Account Balance
4. Manage Recurring Transactions
5. Set Monthly Budget
6. View Budget Alerts
7. Import CSV
8. View Spending Insights
9. Exit

## Maven

Created a standard Maven project structure with:

- pom.xml
- src/main/java
- src/test/java

Successfully tested:

```text
mvn validate
mvn compile
mvn package
mvn clean package
mvn package -Pdev
mvn package -Pprod

All Maven builds completed successfully.

## Maven Profiles

Configured two Maven profiles:

- dev - development environment
- prod - production environment

Both profiles were tested successfully.

## Deliverables

- Maven Project – Personal Expense & Budget Tracker
- Console Menu Screenshot – `menu-screenshot.png`
- GitHub Repository – [https://github.com/kamaleshvarma07/HCL-Training](https://github.com/kamaleshvarma07/HCL-Training)

## Completion Status

- Control flow concepts completed
- ATM Simulator completed
- Maven project structure completed
- Maven lifecycle commands tested successfully
- Dev/Prod Maven profiles tested successfully
- Personal Expense & Budget Tracker console menu completed
- Maven JAR package generated successfully
- Day 3 deliverables completed