package service;

import dao.DatabaseClient;
import dto.UserSession;

import java.util.Iterator;
import java.util.Map;


public class CommandRunner {

    private DatabaseClient dbClient;
    private UserSession session;

    public CommandRunner(DatabaseClient dbClient, UserSession session) {
        this.dbClient = dbClient;
        this.session = session;
    }

    public void login(String userName){
        if(session.getCurrentUser() != null){
            if(userName.equals(session.getCurrentUser()))
                System.out.println("You are already logged in " + userName + "!");
            else
                System.out.println("You need to log out of the current session before you can log in as another user!");
        } else if(dbClient.userExists(userName)){
            session.setCurrentUser(userName);
            System.out.println("Hello, " + userName + "!");
            System.out.println("Your balance is " + "$" + dbClient.getUserBalance(session.getCurrentUser()));
            Map<String, Integer> currentUserDebtToRegistry =  dbClient.getDebtToRegistry(userName);
            Map<String, Integer> currentUserDebtFromRegistry =  dbClient.getDebtFromRegistry(userName);
            if(!currentUserDebtToRegistry.isEmpty()){
                currentUserDebtToRegistry.forEach((creditorUserName, debtAmount) -> {
                    if(debtAmount != 0)
                        System.out.println("Owed $" + debtAmount + " to " + creditorUserName);
                });
            }
            if(!currentUserDebtFromRegistry.isEmpty()){
                currentUserDebtFromRegistry.forEach((debtorUserName, debtAmount) -> {
                    if(debtAmount != 0)
                        System.out.println("Owed $" + debtAmount + " from " + debtorUserName);
                });
            }
        } else {
            dbClient.addUser(userName);
            session.setCurrentUser(userName);
            System.out.println("Hello, " + userName + "! Created new account for you, " + userName + " since it's your first login!");
        }
    }

    public void deposit(int amount){
        String currentUser = session.getCurrentUser();
        if(currentUser == null){
            System.out.println("Please log in!");
        } else {
            Map<String, Integer> currentUserDebtToRegistry = dbClient.getDebtToRegistry(currentUser);
            if(!currentUserDebtToRegistry.isEmpty()){
                Iterator<Map.Entry<String, Integer>> iter = currentUserDebtToRegistry.entrySet().iterator();
                while(iter.hasNext()) {
                    Map.Entry<String,Integer> entry = iter.next();
                    String creditorUserName = entry.getKey();
                    int debtAmount = entry.getValue();
                    if (amount > debtAmount) {
                        dbClient.setMoneyAmountToUser(currentUser, amount - debtAmount);
                        dbClient.setMoneyAmountToUser(creditorUserName, dbClient.getUserBalance(creditorUserName) + debtAmount);
                        iter.remove();
                        dbClient.getDebtFromRegistry(creditorUserName).remove(currentUser);
                        System.out.println("Transferred $" + debtAmount + " to " + creditorUserName);
                        amount = amount - debtAmount;
                    } else {
                        dbClient.setMoneyAmountToUser(currentUser, 0);
                        dbClient.setMoneyAmountToUser(creditorUserName, dbClient.getUserBalance(creditorUserName) + amount);
                        System.out.println("Transferred $" + amount + " to " + creditorUserName);
                        int stillOwingAmount = debtAmount - amount;
                        System.out.println("Owed $" + stillOwingAmount + " to " + creditorUserName);
                        dbClient.setDebtTo(currentUser, creditorUserName, stillOwingAmount);
                        dbClient.setDebtFrom(creditorUserName, currentUser, stillOwingAmount);
                    }
                }
            } else {
                dbClient.setMoneyAmountToUser(currentUser, dbClient.getUserBalance(currentUser) + amount);
            }
            System.out.println("Your balance is $" + dbClient.getUserBalance(currentUser));
        }
    }

    public void withdraw(int amount){
        String currentUser = session.getCurrentUser();

        if(currentUser == null)
            System.out.println("Please log in!");
        else {
            int currentBalance = dbClient.getUserBalance(currentUser);
            if(amount <= currentBalance){
                dbClient.setMoneyAmountToUser(currentUser,currentBalance - amount);
                currentBalance = currentBalance - amount;
                System.out.println("Your balance is $" + currentBalance);
            } else {
                System.out.println("You only have $" + currentBalance + " left in your account. Please try to withdraw less!");
            }
        }
    }

    public void transfer(String userToTransfer, int amount){
        String currentUser = session.getCurrentUser();
        if(currentUser == null){
            System.out.println("Please log in!");
        } else {
            if(!dbClient.userExists(userToTransfer)){
                System.out.println("User " + userToTransfer + " does not exist!");
            } else {
                int currentUserBalance = dbClient.getUserBalance(currentUser) - amount;
                if (currentUserBalance < 0) {
                    int debtAmount = currentUserBalance * -1;
                    int amountToTransfer = amount - debtAmount;
                    currentUserBalance = 0;
                    dbClient.setMoneyAmountToUser(currentUser, 0);
                    dbClient.setMoneyAmountToUser(userToTransfer, dbClient.getUserBalance(userToTransfer) + amountToTransfer);
                    int existingDebt = 0;
                    if(!(dbClient.getDebtToRegistry(currentUser).get(userToTransfer) == null))
                        existingDebt = dbClient.getDebtToRegistry(currentUser).get(userToTransfer);
                    dbClient.setDebtTo(currentUser, userToTransfer, debtAmount + existingDebt);
                    dbClient.setDebtFrom(userToTransfer, currentUser, debtAmount + existingDebt);
                    System.out.println("Transferred $" + amountToTransfer + " to " + userToTransfer);
                    System.out.println("Your balance is $" + currentUserBalance);
                    System.out.println("Owed $" + dbClient.getDebtToRegistry(currentUser).get(userToTransfer) + " to " + userToTransfer);
                } else {
                    dbClient.setMoneyAmountToUser(userToTransfer, dbClient.getUserBalance(userToTransfer) + amount);
                    dbClient.setMoneyAmountToUser(currentUser, currentUserBalance);
                    System.out.println("Transferred $" + amount + " to " + userToTransfer);
                    System.out.println("Your balance is $" + currentUserBalance);
                }
            }

        }
    }

    public void logout(){
        if(session.getCurrentUser() == null){
            System.out.println("You are already logged out!");
        } else {
            System.out.println("Goodbye, " + session.getCurrentUser() + "!");
            session.setCurrentUser(null);
        }
    }

    public void unkownCommand(){
        System.out.println("Invalid command! Please consult the manual for properly using the interface!");
    }
}
