package backend;

import model.Database;
import presenter.DatabasePresenter;
import view.DatabaseCLI;

/**
 * DatabaseBackend provides the main method used for interacting with the
 * database.
 *
 * @author Liam Tripp
 */
public class DatabaseBackend {

    private final Database database;
    private final DatabasePresenter databasePresenter;
    private final DatabaseCLI databaseCLI;

    /**
     * Constructor for DatabaseBackend.
     */
    public DatabaseBackend() {
        database = new Database();
        databasePresenter = new DatabasePresenter();
        databaseCLI = new DatabaseCLI();

        database.initializeDatabase();
        database.populateDatabase();
        databasePresenter.addDatabase(database);
        databaseCLI.addPresenter(databasePresenter);
    }

    /**
     * Executes DatabaseBackend program.
     */
    public void execute() {
        database.initializeDatabase();
        database.populateDatabase();

        databaseCLI.introduction();
        while (databaseCLI.userWantsToQuit() == false) {
            databaseCLI.promptUserForInput();
        }
    }

    public static void main(String[] args) {
        DatabaseBackend databaseBackend = new DatabaseBackend();
        databaseBackend.execute();
    }
}
