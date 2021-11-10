package dao;

import dto.UserData;

import java.util.HashMap;
import java.util.Map;

public class DatabaseClient {
    private Map<String, UserData> users = new HashMap<>();

    public int getUserBalance(String userName){
        return users.get(userName).getBalance();
    }

    public boolean userExists(String userName){
        return users.get(userName) != null;
    }

    public void addUser(String userName){
        users.put(userName, new UserData(userName, 0));
    }

    public void setMoneyAmountToUser(String userName, int amount){
        users.get(userName).setBalance(amount);
    }

    public void setDebtTo(String debtorUserName, String creditorUserName, int amount){
        users.get(debtorUserName).getDebtToRegistry().put(creditorUserName, amount);
    }

    public void setDebtFrom(String creditorUserName, String debtorUserName, int amount){
        users.get(creditorUserName).getDebtFromRegistry().put(debtorUserName, amount);
    }

    public Map<String, Integer> getDebtToRegistry(String creditorUserName){
        return users.get(creditorUserName).getDebtToRegistry();
    }

    public Map<String, Integer> getDebtFromRegistry(String debtorUserName){
        return users.get(debtorUserName).getDebtFromRegistry();
    }
}
