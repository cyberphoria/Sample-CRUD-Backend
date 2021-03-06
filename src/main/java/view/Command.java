package view;

import model.Table;

/**
 * Command contains the commands used for the DatabaseCLI.
 *
 * @author Liam Tripp
 */
public enum Command {
    CREATE("(CREATE) (\\w+) (\\d+(?:\\.\\d+)?) (\\d+)"), // name price stock
    READ("(READ) (" + String.join("|",
            Table.ITEMS.getName(),
            Table.DELETED_ITEMS.getName()) + ")"),
    UPDATE("(UPDATE) (\\d+) (name = '\\w+'|price = \\d+\\.\\d+|stock = \\d+)"),
    DELETE("(DELETE) (\\d+) ?(.+)?"),
    RESTORE("(RESTORE) (\\d+)"),
    HELP("(help)"),
    TABLES("(tables)"),
    QUIT("(quit)");

    private final String regex;

    /**
     * Constructor for Command.
     *
     * @param regex the regular expression for the
     */
    Command(String regex) {
        this.regex = regex;
    }

    /**
     * Returns the Regular Expression associated with the Command.
     *
     * @return the Regular Expression associated with the Command.
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Returns the Command as a String.
     *
     * @return a String with the Command's name as a String
     */
    public String getName() {
        return name().toLowerCase();
    }

    /**
     * Matches and returns a command given a String.
     *
     * @param commandName the name of the command to be retrieved
     * @return the Command if the commandName is valid, null otherwise
     */
    public static Command getCommand(String commandName) {
        try {
            return valueOf(commandName.trim().toUpperCase());
        } catch (IllegalArgumentException iae) {
            System.err.println("Command does not exist");
            return null;
        }
    }
}
