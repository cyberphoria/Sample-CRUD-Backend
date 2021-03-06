package view;

import model.Database;
import model.Item;
import model.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import presenter.DatabasePresenter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DatabaseCLITest ensures that DatabaseCLI's CRUD methods work properly
 * with the DatabasePresenter.
 *
 * @author Liam Tripp
 */
public class DatabaseCLITest {

    private Database database;
    private DatabasePresenter databasePresenter;
    private DatabaseCLI databaseCLI;
    private Item testItem;

    @BeforeEach
    void setup() {
        database = new Database();
        database.initializeDatabase();
        databasePresenter = new DatabasePresenter();
        databasePresenter.addDatabase(database);
        databaseCLI = new DatabaseCLI();
        databaseCLI.addPresenter(databasePresenter);
        testItem = new Item(1, "testName", "100.99", 1);
    }

    @AfterEach
    void tearDown() {
        databaseCLI.quit();
    }

    /**
     * Creates the testItem.
     */
    void createItem() {
        String[] itemValues = testItem.getValuesAsArray();
        String[] itemValuesExceptId = Arrays.copyOfRange(itemValues, 1, itemValues.length);
        String values = String.join(" ", itemValuesExceptId);

        String userInput = "CREATE " + values;
        databaseCLI.processInput(userInput);
    }

    @Test
    void testCreateOneItem() {
        createItem();
        assertEquals(1, database.getSizeOfTable(Table.ITEMS.getName()));
    }

    @Test
    void testReadFromEmptyTable() {
        String userInput = "READ " + Table.ITEMS.getName();
        String consoleOutput = databaseCLI.processInput(userInput);
        assertTrue(consoleOutput.contains("ERROR"));
    }

    @Test
    void testReadFromTableWithItems() {
        createItem();
        String readStatement = "READ " + Table.ITEMS.getName();
        String consoleOutput = databaseCLI.processInput(readStatement);
        assertFalse(consoleOutput.contains("ERROR"));
    }

    @Test
    void testUpdateOneItemOneAttribute() {
        createItem();
        List<Item> items = database.selectFromTable(Table.ITEMS.getName(), "*");
        Item originalItem = items.get(0);

        String newName = "newTestName";
        String updateStatement = "UPDATE 1 name = '" + newName + "'";
        String consoleOutput = databaseCLI.processInput(updateStatement);
        assertFalse(consoleOutput.contains("ERROR"));

        originalItem.setName(newName);
        items = database.selectFromTable(Table.ITEMS.getName(), "*");
        Item updatedItem = items.get(0);
        assertEquals(originalItem, updatedItem);
    }

    @Test
    void testUpdateItemInvalid() {
        createItem();

        int errorId = 10000;
        String newName = "newTestName";
        String updateStatement = "UPDATE " + errorId + " name = '" + newName + "'";
        String consoleOutput = databaseCLI.processInput(updateStatement);
        assertTrue(consoleOutput.contains("ERROR"));
    }

    @Test
    void testDeleteOneItem() {
        createItem();
        String deleteStatement = "DELETE " + testItem.getId();
        String consoleOutput = databaseCLI.processInput(deleteStatement);
        assertFalse(consoleOutput.contains("ERROR"));
        assertEquals(0, database.getSizeOfTable(Table.ITEMS.getName()));
    }

    @Test
    void testDeleteItemProducesNull() {
        createItem();

        int errorId = 1000;
        String deleteStatement = "DELETE " + errorId;
        String consoleOutput = databaseCLI.processInput(deleteStatement);
        assertTrue(consoleOutput.contains("ERROR"));
        assertEquals(1, database.getSizeOfTable(Table.ITEMS.getName()));
    }

    @Test
    void testRestoreOneItemReturnsCorrectItem() {
        testDeleteOneItem();

        String restoreStatement = "RESTORE " + testItem.getId();
        String restoredItemMessage = databaseCLI.processInput(restoreStatement);
        String[] itemValues = testItem.getValuesAsArray();
        itemValues[1] = "'" + itemValues[1] + "'"; // put apostrophes around name
        String values = String.join(", ", itemValues);
        assertTrue(restoredItemMessage.contains(values));
        assertFalse(restoredItemMessage.contains("ERROR"));
    }

    @Test
    void testRestoreOneItemPlacesItemInCorrectTable() {
        testDeleteOneItem();
        String restoreStatement = "RESTORE " + testItem.getId();
        databaseCLI.processInput(restoreStatement);
        assertEquals(0, database.getSizeOfTable(Table.DELETED_ITEMS.getName()));
        assertEquals(1, database.getSizeOfTable(Table.ITEMS.getName()));
    }

    @Test
    void testRestoreItemInvalid() {
        testDeleteOneItem();
        int errorId = 1000;

        String restoreStatement = "RESTORE " + errorId;
        String consoleOutput = databaseCLI.processInput(restoreStatement);
        assertTrue(consoleOutput.contains("ERROR"));
    }
}
