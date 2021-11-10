package service;

import dao.DatabaseClient;
import dto.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandRunnerTest {

        private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        private final PrintStream originalOut = System.out;
        @BeforeEach
        public void setUpStreams() {
                System.setOut(new PrintStream(outContent));
        }
        @AfterEach
        public void restoreStreams() {
                System.setOut(originalOut);
        }

        DatabaseClient dbClient = new DatabaseClient();
        UserSession session = new UserSession();
        CommandRunner commandRunner = new CommandRunner(dbClient, session);


        @Nested
        class HandleLogin{
                @Test
                public void loginWhenCurrentUserIsAlreadyLoggedInShouldReturnAppropriateMessage(){
                        session.setCurrentUser("Alice");

                        commandRunner.login("Alice");
                        assertEquals("You are already logged in Alice!\n", outContent.toString());
                }

                @Test
                public void loginWhenAnotherUserIsAlreadyLoggedInShouldReturnAppropriateMessage(){
                        session.setCurrentUser("Alice");

                        commandRunner.login("Bob");
                        assertEquals("You need to log out of the current session before you can log in as another user!\n", outContent.toString());
                }

                @Test
                public void loginFirstTimeWhenNoUserIsCurrentlyLoggedInShouldReturnAppropriateMessage(){
                        commandRunner.login("Bob");
                        assertEquals("Hello, Bob! Created new account for you, Bob since it's your first login!\n", outContent.toString());
                }

                @Test
                public void loginWhenUserPreviouslyLoggedInShouldReturnHelloMessageAndAppropriateBalance(){
                        dbClient.addUser("Bob");
                        dbClient.setMoneyAmountToUser("Bob", 10);

                        commandRunner.login("Bob");
                        assertEquals("Hello, Bob!\nYour balance is $10\n", outContent.toString());
                }
        }

        @Nested
        class HandleDeposit{
                @Test
                public void depositWhenNotLoggedInShouldReturnAppropriateMessage(){
                        session.setCurrentUser(null);

                        commandRunner.deposit(20);
                        assertEquals("Please log in!\n", outContent.toString());
                }

                @Test
                public void depositWhenNoDebtShouldAddMoneyInCurrentUserAccount(){
                        dbClient.addUser("Bob");
                        session.setCurrentUser("Bob");

                        commandRunner.deposit(20);
                        assertEquals(dbClient.getUserBalance("Bob"), 20);
                        assertEquals("Your balance is $20\n", outContent.toString());
                }

                @Test
                public void depositWhenInDebtAndUserDepositsMoreMoneyThanDebtShouldTransferDebtToCreditorAndReturnRemainingCurrentBalance(){
                        dbClient.addUser("Bob");
                        dbClient.addUser("Alice");
                        session.setCurrentUser("Bob");
                        dbClient.setDebtTo("Bob", "Alice", 20);
                        dbClient.setDebtFrom("Alice", "Bob", 20);

                        commandRunner.deposit(30);
                        assertEquals(dbClient.getUserBalance("Bob"), 10);
                        assertEquals("Transferred $20 to Alice\nYour balance is $10\n", outContent.toString());
                }

                @Test
                public void depositWhenInDebtAndUserDepositsLessMoneyThanDebtShouldTransferAllMoneyToCreditorAndKeepTrackOfRemainingDebt(){
                        dbClient.addUser("Bob");
                        dbClient.addUser("Alice");
                        session.setCurrentUser("Bob");
                        dbClient.setDebtTo("Bob", "Alice", 20);
                        dbClient.setDebtFrom("Alice", "Bob", 20);

                        commandRunner.deposit(10);
                        assertEquals(dbClient.getUserBalance("Bob"), 0);
                        assertEquals("Transferred $10 to Alice\nOwed $10 to Alice\nYour balance is $0\n", outContent.toString());
                }
        }

        @Nested
        class HandleWithdraw{
               @Test
               public void withdrawWhenNotLoggedInShouldReturnAppropriateMessage(){
                       session.setCurrentUser(null);

                       commandRunner.withdraw(20);
                       assertEquals("Please log in!\n", outContent.toString());
               }

                @Test
                public void withdrawWhenEnoughMoneyInAccountShouldSubstractCorrectSumFromBalance(){
                        dbClient.addUser("Bob");
                        session.setCurrentUser("Bob");
                        dbClient.setMoneyAmountToUser("Bob", 20);

                        commandRunner.withdraw(10);
                        assertEquals(dbClient.getUserBalance("Bob"), 10);
                        assertEquals("Your balance is $10\n", outContent.toString());
                }

                @Test
                public void withdrawWhenNotEnoughMoneyInAccountShouldReturnAppropriateMessage(){
                        dbClient.addUser("Bob");
                        session.setCurrentUser("Bob");
                        dbClient.setMoneyAmountToUser("Bob", 10);

                        commandRunner.withdraw(20);
                        assertEquals(dbClient.getUserBalance("Bob"), 10);
                        assertEquals("You only have $10 left in your account. Please try to withdraw less!\n", outContent.toString());
                }
        }

        @Nested
        class HandleTransfer{
                @Test
                public void transferWhenNotLoggedInShouldReturnAppropriateMessage(){
                        session.setCurrentUser(null);

                        commandRunner.transfer("Alice", 10);
                        assertEquals("Please log in!\n", outContent.toString());
                }

                @Test
                public void transferWhenOtherUserDoesNotExistShouldReturnAppropriateMessage(){
                        session.setCurrentUser("Bob");

                        commandRunner.transfer("Alice", 10);
                        assertEquals("User Alice does not exist!\n", outContent.toString());
                }

                @Test
                public void transferWhenEnoughMoneyInAccountShouldCorrectlyTransferSumToAnotherAccount(){
                        dbClient.addUser("Bob");
                        dbClient.addUser("Alice");
                        session.setCurrentUser("Bob");
                        dbClient.setMoneyAmountToUser("Bob", 20);
                        dbClient.setMoneyAmountToUser("Alice", 40);

                        commandRunner.transfer("Alice", 10);
                        assertEquals(dbClient.getUserBalance("Bob"), 10);
                        assertEquals(dbClient.getUserBalance("Alice"), 50);
                        assertEquals("Transferred $10 to Alice\nYour balance is $10\n", outContent.toString());
                }

                @Test
                public void transferWhenNotEnoughMoneyInAccountShouldCorrectlyTransferSumToAnotherAccountAndKeepTrackOfRemainingDebt(){
                        dbClient.addUser("Bob");
                        dbClient.addUser("Alice");
                        session.setCurrentUser("Bob");
                        dbClient.setMoneyAmountToUser("Bob", 10);
                        dbClient.setMoneyAmountToUser("Alice", 30);

                        commandRunner.transfer("Alice", 20);
                        assertEquals(dbClient.getUserBalance("Bob"), 0);
                        assertEquals(dbClient.getUserBalance("Alice"), 40);
                        assertEquals("Transferred $10 to Alice\nYour balance is $0\nOwed $10 to Alice\n", outContent.toString());
                }
        }

        @Nested
        class HandleLogout{
                @Test
                public void logoutWhenNoUserIsLoggedInShouldReturnAppropriateMessage(){
                        session.setCurrentUser(null);

                        commandRunner.logout();
                        assertEquals("You are already logged out!\n", outContent.toString());

                }

                @Test
                public void logoutWhenAUserIsLoggedInShouldLogOutAndReturnAppropriateMessage(){
                        session.setCurrentUser("Bob");

                        commandRunner.logout();
                        assertEquals("Goodbye, Bob!\n", outContent.toString());

                }
        }

        @Test
        public void fullFlow(){
                commandRunner.login("Bob");
                commandRunner.deposit(20);
                commandRunner.logout();
                commandRunner.login("Alice");
                commandRunner.deposit(30);
                commandRunner.transfer("Bob", 10);
                commandRunner.logout();
                commandRunner.login("Bob");
                commandRunner.withdraw(50);
                commandRunner.withdraw(20);
                commandRunner.transfer("Alice", 50);
                commandRunner.transfer("Alice", 10);
                commandRunner.logout();
                commandRunner.login("Alice");
                commandRunner.logout();
                commandRunner.login("John");
                commandRunner.transfer("Bob", 20);
                commandRunner.logout();
                commandRunner.login("Bob");
                assertEquals(
                        "Hello, Bob! Created new account for you, Bob since it's your first login!\n" +
                        "Your balance is $20\n" +
                        "Goodbye, Bob!\n" +
                        "Hello, Alice! Created new account for you, Alice since it's your first login!\n" +
                        "Your balance is $30\n" +
                        "Transferred $10 to Bob\n" +
                        "Your balance is $20\n" +
                        "Goodbye, Alice!\n" +
                        "Hello, Bob!\n" +
                        "Your balance is $30\n" +
                        "You only have $30 left in your account. Please try to withdraw less!\n" +
                        "Your balance is $10\n" +
                        "Transferred $10 to Alice\n" +
                        "Your balance is $0\n" +
                        "Owed $40 to Alice\n" +
                        "Transferred $0 to Alice\n" +
                        "Your balance is $0\n" +
                        "Owed $50 to Alice\n" +
                        "Goodbye, Bob!\n" +
                        "Hello, Alice!\n" +
                        "Your balance is $30\n" +
                        "Owed $50 from Bob\n" +
                        "Goodbye, Alice!\n" +
                        "Hello, John! Created new account for you, John since it's your first login!\n" +
                        "Transferred $0 to Bob\n" +
                        "Your balance is $0\n" +
                        "Owed $20 to Bob\n" +
                        "Goodbye, John!\n" +
                        "Hello, Bob!\n" +
                        "Your balance is $0\n" +
                        "Owed $50 to Alice\n" +
                        "Owed $20 from John\n",
                        outContent.toString());

        }
}
