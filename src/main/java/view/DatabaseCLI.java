package view;

import model.DeletedItem;
import model.Item;
import model.Table;
import presenter.DatabasePresenter;

import java.util.*;
import java.util.regex.Matcher;

/**
 * DatabaseCLI is a command-line interface that allows users to interact with
 * the Database.
 *
 * @author Liam Tripp
 */
public class DatabaseCLI {

    /** DatabaseCLI interacts with the model through databasePresenter */
    private DatabasePresenter databasePresenter;
    private final InputMatcher inputMatcher;
    private final Scanner scanner;
    private boolean userWantsToQuit;

    /**
     * Constructor for Database CLI.
     */
    public DatabaseCLI() {
        inputMatcher = new InputMatcher();
        userWantsToQuit = false;
        scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);
    }

    /**
     * Indicates whether the user wants to terminate the program.
     *
     * @return true if the user wants to quit, false otherwise.
     */
    public boolean userWantsToQuit() {
        return userWantsToQuit;
    }

    /**
     * Adds a databasePresenter to the DatabaseCLI (View).
     *
     * @param databasePresenter the database presenter through which the
     */
    public void addPresenter(DatabasePresenter databasePresenter) {
        this.databasePresenter = databasePresenter;
    }

    /**
     * Produces an introduction messages for the user.
     */
    public void introduction() {
        String introduction = "\nWelcome to Liam Tripp's Backend CRUD Sample.\n\n";
        introduction += "The database for this program emulates an online store manager.\n";
        introduction += "It contains Items which each have: " +
                String.join(", ", Item.getAttributeNamesAsArray()) + "\n";
        introduction += "The following are the commands you may choose from:\n";
        introduction += help() + "\n\n";
        introduction += "You may read from any of the following " + tables() + "\n";
        System.out.println(introduction);
    }

    /**
     * Asks user for input and delegates to methods, printing output statement.
     */
    public void promptUserForInput() {
        System.out.print("Enter command: ");
        String initialInput = scanner.nextLine().toLowerCase().trim();
        System.out.println();
        String consoleOutput = processInput(initialInput);

        System.out.println(consoleOutput);
        System.out.println();
    }

    /**
     * Processes an input, matching and executing it.
     *
     * @param userInput the user's input
     * @return output message if input is valid, error message otherwise
     */
    public String processInput(String userInput) {
        Matcher matcher = inputMatcher.matchInputToCommandRegex(userInput);
        String matcherError = inputMatcher.validateMatcher(matcher);
        if (!matcherError.equals("")) {
            return matcherError + "\nError text: " + userInput;
        }
        String consoleOutput = executeInput(matcher);
        return consoleOutput;
    }

    /**
     * Executes a Command given its associated SQL information.
     *
     * @param commandMatcher contains the Command and the user's input
     * @return a statement indicating the operation and its level of success
     */
    String executeInput(Matcher commandMatcher) {
        String commandAsString = commandMatcher.group(1);
        Command command = Command.getCommand(commandAsString);
        if (command == null) {
            String errorMessage = "ERROR: Please enter a valid command. Enter '" +
                    Command.HELP.getName() + "' for a list of them.";
            return errorMessage;
        }

        String consoleOutput;
        switch (command) {
            case CREATE -> consoleOutput = createItem(commandMatcher);
            case READ -> consoleOutput = read(commandMatcher);
            case UPDATE -> consoleOutput = updateItem(commandMatcher);
            case DELETE -> consoleOutput = delete(commandMatcher);
            case RESTORE -> consoleOutput = restore(commandMatcher);
            case HELP -> consoleOutput = help();
            case TABLES -> consoleOutput = tables();
            case QUIT -> consoleOutput = quit();
            default -> consoleOutput = "ERROR: unhandled command."; // shouldn't be seen in normal program execution
        }
        return consoleOutput;
    }

    /**
     * Creates an Item and returns a String containing information about that item.
     *
     * @param matcher the matcher containing the user's command
     * @return a String containing information about the completed item
     */
    public String createItem(Matcher matcher) {
        Item item;
        item = new Item(matcher);
        databasePresenter.createItem(item);

        String values = String.join(", ", item.getValuesAsArray());
        // get rid of invalid id
        values = values.replace("-1, ", "");
        return "Created item: " + values;
    }

    /**
     * Returns the contents of the specified table as a String.
     *
     * @param matcher contains the READ command and tableName to be read
     * @return a String containing the contents of the table
     */
    public String read(Matcher matcher) {
        // matcher.group(1) is "read"
        String tableName = matcher.group(2);
        List<Item> items = databasePresenter.readFromTable(tableName);
        if (items.isEmpty()) {
            return "ERROR: " + tableName + " is empty.";
        }

        StringBuilder consoleOutput = new StringBuilder();
        consoleOutput.append("Table ").append(tableName).append(" contains:\n");

        String[] attributeNames;
        if (tableName.equals(Table.ITEMS.getName())) {
            attributeNames = Item.getAttributeNamesAsArray();
        } else {
            attributeNames = DeletedItem.getAttributeNamesAsArray();
        }

        String bar = " | ";
        String attributeNamesBarSeparated = String.join(bar, attributeNames);
        consoleOutput.append(attributeNamesBarSeparated).append("\n");
        for (Item item : items) {
            String[] values = item.getValuesAsArray();
            String valuesBarSeparated = String.join(bar, values);
            consoleOutput.append(valuesBarSeparated).append("\n");
        }
        return consoleOutput.toString();
    }

    /**
     * Updates an Item and returns a String indicating the level of success.
     *
     * @param matcher the matcher containing the user's command
     * @return a String indicating the completion success
     */
    public String updateItem(Matcher matcher) {
        // matcher.group(1) is "update"
        String itemId = matcher.group(2);
        String columnValuePair = matcher.group(3);
        Item item = databasePresenter.updateItem(itemId, columnValuePair);
        if (item == null) {
            return "ERROR: Item " + itemId + " was not able to be updated.";
        }
        String values = String.join(", ", item.getValuesAsArray());
        return "Update item " + itemId + " to have values: " + values;
    }

    /**
     * Deletes an Item from a specified table.
     *
     * @param matcher contains the user's command and table to be deleted
     * @return a String indicating the deletion of the Item
     */
    public String delete(Matcher matcher) {
        // matcher.group(1) is "delete"
        String itemId = matcher.group(2);
        String comment = "";
        if (matcher.groupCount() > 2) {
            comment = Objects.requireNonNullElse(matcher.group(3), "");
        }

        Item item = databasePresenter.deleteItem(itemId, comment);
        if (item == null) {
            return "ERROR: could not delete item with id " + itemId;
        }

        String values = String.join(", ", item.getValuesAsArray());
        return "Deleted item " + itemId + " with values: " + values;
    }

    /**
     * Restores a DeletedItem to its corresponding table.
     *
     * @param matcher contains the user's command and itemId of the item to restore
     * @return a string indicating the completion of restoring the item
     */
    public String restore(Matcher matcher) {
        // matcher.group(1) is "restore"
        String itemId = matcher.group(2);
        Item restoredItem = databasePresenter.restoreItem(itemId);

        if (restoredItem == null) {
            return "ERROR: Item does not exist in the table " + Table.DELETED_ITEMS.getName();
        }
        return "Restored item: " + restoredItem;
    }

    /**
     * Returns a list of valid user commands.
     *
     * @return a list of valid user commands
     */
    public String help() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Omit the square brackets seen in all of the following commands:\n\n");
        String itemsEnding = " the table " + Table.ITEMS.getName();

        stringBuilder.append("`CREATE [name] [dollar.cents] [stock]` - insert a row into").append(itemsEnding).append("\n");
        stringBuilder.append("`READ [tableName]` - view the rows from one of the following ").append(tables()).append("\n");
        stringBuilder.append("`UPDATE [id] [columnName] = [value]` - update a value corresponding to a column name in").append(itemsEnding).append(". Text values must be quoted like 'this'\n");
        stringBuilder.append("`DELETE [id] [optionalComment]` - delete a row in").append(itemsEnding).append(" while providing an optional comment\n");
        stringBuilder.append("`RESTORE [id]` - restores a row with the provided id to its corresponding table\n");
        stringBuilder.append("`HELP` - view the list of valid commands\n");
        stringBuilder.append("`TABLES` - view the list of tables\n");
        stringBuilder.append("`QUIT` - exit the command-line interface");
        return stringBuilder.toString();
    }

    /**
     * Returns a list of the tables in the Database.
     *
     * @return a list of the tables in the Database as a String
     */
    public String tables() {
        return "tables: " + String.join(", ", Table.ITEMS.getName(), Table.DELETED_ITEMS.getName());
    }

    /**
     * Prepares the program to terminate and returns a string indicating the
     * end of the program.
     *
     * @return a string indicating the end of the program
     */
    public String quit() {
        userWantsToQuit = true;
        databasePresenter.terminateDatabase();
        scanner.close();
        return "Exiting program.";
    }
}
