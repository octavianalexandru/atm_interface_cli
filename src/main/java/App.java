import controller.CommandHandler;
import enums.Command;

import java.util.Scanner;

public class App {
    public static void main(String[] args){
        CommandHandler commandHandler = new CommandHandler();
        Scanner scanner = new Scanner(System.in);
        while(true){
            String inputString = scanner.nextLine();
            String[] inputStringSplitBySpaces = inputString.trim().split(" ");
            Command command = commandHandler.evaluateCommand(inputStringSplitBySpaces);
            commandHandler.handleCommand(command, inputStringSplitBySpaces);
        }
    }
}
