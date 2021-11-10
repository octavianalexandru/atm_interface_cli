package dto;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    private String userName;
    private int balance;
    private Map<String, Integer> debtToRegistry;
    private Map<String, Integer> debtFromRegistry;

    public UserData(String userName, int balance) {
        this.userName = userName;
        this.balance = balance;
        debtToRegistry = new HashMap<>();
        debtFromRegistry = new HashMap<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Map<String, Integer> getDebtToRegistry() {
        return debtToRegistry;
    }

    public Map<String, Integer> getDebtFromRegistry() {
        return debtFromRegistry;
    }
}
