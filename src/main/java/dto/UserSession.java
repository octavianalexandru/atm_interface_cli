package dto;

public class UserSession {
    private String currentUserName;

    public String getCurrentUser() {
        return this.currentUserName;
    }

    public void setCurrentUser(String currentUserName) {
        this.currentUserName = currentUserName;
    }
}
