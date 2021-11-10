package controller;

import enums.Command;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandHandlerTest {

    public static Stream<Arguments> correctCommandsInputAndOutput() {
        return Stream.of(
            Arguments.arguments((new String[]{"login", "alice"}), Command.LOGIN),
            Arguments.arguments((new String[]{"deposit", "10"}), Command.DEPOSIT),
            Arguments.arguments((new String[]{"withdraw", "10"}), Command.WITHDRAW),
            Arguments.arguments((new String[]{"transfer", "alice", "10"}), Command.TRANSFER),
            Arguments.arguments((new String[]{"logout"}), Command.LOGOUT)
        );
    }

    public static Stream<Arguments> incorrectCommandsInputAndOutput() {
        return Stream.of(
            Arguments.arguments((new String[]{"login"}), Command.INVALID_COMMAND),
            Arguments.arguments((new String[]{"deposit", "10", "50"}), Command.INVALID_COMMAND),
            Arguments.arguments((new String[]{"transfer", "2"}), Command.INVALID_COMMAND),
            Arguments.arguments((new String[]{"logout", "2"}), Command.INVALID_COMMAND),
            Arguments.arguments((new String[]{"login", "2", "Alice"}), Command.INVALID_COMMAND),
            Arguments.arguments((new String[]{"transfer", "2", "5", "Bob"}), Command.INVALID_COMMAND)
        );
    }



    @ParameterizedTest
    @MethodSource("correctCommandsInputAndOutput")
    public void evaluateCommandCorrectTest(String[] commandInput, Command commandValueExpected){
        CommandHandler commandEvaluator = new CommandHandler();

        assertEquals(commandEvaluator.evaluateCommand(commandInput), commandValueExpected);
    }

    @ParameterizedTest
    @MethodSource("incorrectCommandsInputAndOutput")
    public void evaluateCommandIncorrectTest(String[] commandInput, Command commandValueExpected){
        CommandHandler commandHandler = new CommandHandler();

        assertEquals(commandHandler.evaluateCommand(commandInput), commandValueExpected);
    }
}

