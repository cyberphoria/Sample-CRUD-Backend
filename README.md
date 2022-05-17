# Sample CRUD Database

A project for Shopify's Fall 2022 Backend Engineer Intern Challenge. This project involves independently creating a simple CRUD application. The additional requirement satisfied for the challenge is: `When deleting, allow deletion comments and undeletion`.

## How to Run

The app has been prepared to run without the user installing anything with an online IDE called `Replit.` For more details on how to run the app this way, see the [Replit Instructions](#replit-instructions) section.

Instructions on how to run the app from terminal are provided in [General Instructions](#general-instructions).

### Replit Instructions

[![Run on Repl.it](https://repl.it/badge/github/cyberphoria/Sample-CRUD-Backend)](https://replit.com/@liamtripp/Sample-CRUD-Backend)

The badge above directs the user to the app on Replit. Once there, click the large green button. The app will ask you in the `Shell` tab to confirm before proceeding. Various packages will be downloaded before the application begins. 

See [Application Commands](#application-commands) for the available commands in the application. See [Shell Commands](#shell-commands) for commands you can use to interact with the shell and the application itself.

### General Instructions

The app requires at least [JDK 17](https://www.oracle.com/java/technologies/downloads/) and [Maven](https://maven.apache.org/download.cgi) 3.8.x to run. [Graphviz](https://graphviz.org/download/) must also be installed for a plugin this app uses (See [Once those are installed, the app can be run manually by executing the two commands below in a terminal navigated to app's folder. If you don't know what the `cd` is, read about what it is [here](https://en.wikipedia.org/wiki/Cd_(command)#Usage), and enter `cd yourFilePathToProjectFolder` in terminal.

```mvn clean install```

```mvn compile exec:java```

See [Maven Commands](#maven-commands) for more details on the above commands. 

#### Shell Commands

These are meant for a Linux shell. Note that `file` refers to `java`, `javac`, or `mvn`.

* `Ctrl+C` (keyboard) - cancel a process in action
* `kill 1` - restart the application
* `[file] -version` - check a framework's version of a file, without the brackets
* `command -v [file]` - check a language's filepath of a file, without the brackets

#### Maven Commands 

These commands interact with the application itself. They can be run on any Operating System. On Replit, the first two are done with the green 'run' button.

* `mvn clean install` - download the packages for the app to Replit
* `mvn compile exec:java` - execute the application
* `mvn test` - run the app's unit tests

#### Application Commands

A list of Commands the user may use in the application is provided below. The square brackets should be omitted for all commands.

* `CREATE [name] [dollar.cents] [stock]` - insert a row into the table `items`. The attribute `name` must be one word with alphanumeric characters

* `READ [tableName]` - view the rows from one of the following tables: `items`, `deleted_items`

* `UPDATE [id] [columnName] = [value]` - update a value corresponding to a column name in the table items. Text values must be quoted like 'this' (ex: `update name = 'Mint)Os'`)

* `DELETE [id] [optionalComment]` - delete a row in the table `items` while providing an optional comment

* `RESTORE [id]` - restores a row with the provided id to its corresponding table

* `HELP` - view the list of valid commands

* `TABLES` - view the list of tables

* `QUIT` - exit the application

Note that the `UPDATE` command is currently limited to updating one value on one item at a time.

## Design

This program emulates an online store manager. The method to run this program is found in the file `DatabaseBackend`. It begins a loop that prompts the user for input in the form of a [Command](#application-commands) and displays an appropriate output. This loop continues until the user enters the `Command` `QUIT`.

The tables for this project are `items` and `deleted_items`. Both contain Items which each have an id, name, price, and stock. DeletedItem, a subclass of Item, may contain an optional comment.

When an Item is deleted from a table using the `DELETE` Command, it is inserted into the `deleted_items` table. The `RESTORE` Command deletes the item from the `deleted_items` table, returning the DeletedItem to its original table as an item.

Commands are captured by pattern-matching with Regex. The captured inputs are passed and formatted for an SQL command before being executed. The Regex capturing also prevents the user from entering input that doesn't match the allowed format. However, it also limits the user's use of the program to whatever is hard-coded.

The design pattern used for the GUI is [Model-View-Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter). This separates responsibilities for different functions among different classes. 

See [Classes](#classes) for more details on how the application is designed. See [Tests](#tests) for descriptions of the unit tests performed.

### Classes

Below is a brief overview. The documents will be in the folder `target/apidocs`.

#### Class Descriptions

<img src="images/all_classes.png" alt="Class Descriptions">

#### Package Dependencies

<img src="images/package-dependencies.png" alt="Package Overview">

### Diagrams

<details>
  <summary><b>Show Package Diagrams</b></summary>

#### Backend

<img src="images/backend.png" alt="Backend">

#### View

<img src="images/view.png" alt="View">

#### Presenter

<img src="images/presenter.png" alt="Presenter">

#### Model

<img src="images/model.png" alt="Model">

</details>

## Resources

- `DDL.sql` contains the SQL statements used to define the database schema.
- `items.json` contains the information used to populate the table `items`.
- `testUserInpust.json` - contains valid and invalid inputs that a user might enter. Used for testing with CommandTest.

## Tests

Rigorous unit testing that was used throughout development to verify project functions. Below is a descriptions of the test files.

 - `DatabaseTest` ensures the Database's CRUD methods work properly.
 - `DatabasePresenterTest` ensures the DatabasePresenter's CRUD methods work properly with the Database.
 - `DatabaseCLITest` ensures that DatabaseCLI's CRUD methods work properly with the DatabasePresenter.
 - `CommandTest` ensures that Command's search method work properly with the inputs found in `testUserInputs.json`.
 - `ItemTest` ensures that Item's price conversion methods work properly.
 - `InputFileReaderTest` ensures that inputs files are read properly.

## Technologies

As this project is managed with [Maven](https://maven.apache.org/), the plugins and dependencies and plugins used are contained in the file `pom.xml`. Alternatively, an up-to-date list of dependencies can be found [on GitHub](https://github.com/cyberphoria/Sample-CRUD-Backend/network/dependencies). However, it does not include plugins.

If the user is running the application using the [General Instructions](#general-instructions], full API documentation including UML Class Diagrams can be generated using UMLDoclet. [Graphviz](https://graphviz.org/download/) must be installed for this. Run `mvn install` in terminal to activate [UMLDoclet](https://github.com/talsma-ict/umldoclet).

- [SQLite](https://github.com/xerial/sqlite-jdbc) - a relational database that is self-contained, meaning it does not require client-server configuration like MySQL or PostgreSQL
- [JSONSimple](https://github.com/fangyidong/json-simple) - JSON file manipulation
- [UMLDoclet](https://github.com/talsma-ict/umldoclet) - that generates interactive Javadoc pages and UML Class Diagrams for packages. It is executed when the user runs ```mvn install```

It currently lacks integration as a web application. My background is mainly in backend-type languages. Therefore, I built a Java command-line application instead.

## Project Demonstrates:

- Emphasis on unit testing to verify program functions 
- Extensive technical and user documentation
- Knowledge of SQL
- Application of basic design patterns (specifically, [Model-View-Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)
- Knowledge of GitHub
- Application of error detection and handling
- Sample of coding and development style

### Takeaways 

It would be fun to learn to make a CRUD application using more frontend technologies.
