package view;

import model.InputFileReader;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collection;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommandTest ensures that Command's search method work properly with the
 * inputs found in testUserInputs.json.
 *
 * @author Liam Tripp
 */
public class CommandTest {

    private InputMatcher inputMatcher;
    private JSONObject inputs;

    @BeforeEach
    void setup() {
        InputFileReader inputFileReader = new InputFileReader("testUserInputs", "json");
        JSONObject jsonObject = inputFileReader.getJSONFileAsObject();
        inputs = (JSONObject) jsonObject.get("inputs");
        inputMatcher = new InputMatcher();
    }

    @ParameterizedTest
    @EnumSource(Command.class)
    void testGetCommandValidInputs(Command command) {
        String commandName = command.getName();
        Command foundCommand = Command.getCommand(commandName);
        Assertions.assertNotNull(foundCommand);
        assertEquals(commandName, foundCommand.getName());
    }

    @Test
    void testGetCommandInvalidInputs() {
        String invalidCommandName = "invalidCommandName";
        Command invalidCommand = Command.getCommand(invalidCommandName);
        assertNull(invalidCommand);
    }

    /**
     * Returns a JSONObject containing the values used to test each Command's
     * Regular Expression.
     *
     * @param commandName the name of the Command being tested
     * @param validity the validity of the command (either valid or invalid)
     * @return a jsonObject containing the valid/invalid tests
     */
    JSONObject getCommandTest(String commandName, String validity) {
        return (JSONObject) ((JSONObject) inputs.get(commandName)).get(validity);
    }

    @ParameterizedTest
    @EnumSource(Command.class)
    @SuppressWarnings("unchecked")
    void testValidInputsForEachCommand(Command command) {
        JSONObject validTests = getCommandTest(command.getName(), "valid");
        Collection<Object> validStatements = (Collection<Object>) validTests.values();

        for (Object object : validStatements) {
            String validStatement = (String) object;
            Matcher matcher = inputMatcher.getMatcher(command.getRegex(), validStatement);
            assertEquals("", inputMatcher.validateMatcher(matcher));
            assertNotEquals("", matcher.group(0));
            assertEquals(validStatement, matcher.group(0));
        }
    }

    @ParameterizedTest
    @EnumSource(Command.class)
    @SuppressWarnings("unchecked")
    void testInvalidInputs(Command command) {
        JSONObject invalidTests = getCommandTest(command.getName(), "invalid");
        Collection<Object> InvalidStatements = (Collection<Object>) invalidTests.values();

        for (Object object : InvalidStatements) {
            String invalidStatements = (String) object;
            Matcher matcher = inputMatcher.getMatcher(command.getRegex(), invalidStatements);
            String error = inputMatcher.validateMatcher(matcher);
            assertNotEquals("", error);
        }
    }
}
