package controller;

import dao.DatabaseClient;
import dto.UserSession;
import enums.Command;
import service.CommandRunner;

public class CommandHandler {

    DatabaseClient dbClient = new DatabaseClient();
    UserSession session = new UserSession();
    CommandRunner commandRunner = new CommandRunner(dbClient, session);

    public Command evaluateCommand(String[] inputStringSplitBySpaces){
        if(inputStringSplitBySpaces[0].equals("login") && inputStringSplitBySpaces.length == 2)
            return Command.LOGIN;
        else if(inputStringSplitBySpaces[0].equals("deposit") && inputStringSplitBySpaces.length == 2)
            return Command.DEPOSIT;
        else if(inputStringSplitBySpaces[0].equals("withdraw") && inputStringSplitBySpaces.length == 2)
            return Command.WITHDRAW;
        else if(inputStringSplitBySpaces[0].equals("transfer") && inputStringSplitBySpaces.length == 3)
            return Command.TRANSFER;
        else if(inputStringSplitBySpaces[0].equals("logout") && inputStringSplitBySpaces.length == 1)
            return Command.LOGOUT;
        else
            return Command.INVALID_COMMAND;
    }

    public void handleCommand(Command command, String[] inputStringSplitBySpaces){
        switch(command){
            case LOGIN:
                commandRunner.login(inputStringSplitBySpaces[1]);
                break;
            case DEPOSIT:
                commandRunner.deposit(Integer.parseInt(inputStringSplitBySpaces[1]));
                break;
            case WITHDRAW:
                commandRunner.withdraw(Integer.parseInt(inputStringSplitBySpaces[1]));
                break;
            case TRANSFER:
                commandRunner.transfer(inputStringSplitBySpaces[1], Integer.parseInt(inputStringSplitBySpaces[2]));
                break;
            case LOGOUT:
                commandRunner.logout();
                break;
            default:
                commandRunner.unkownCommand();
                break;
        }

    }
}
