package me.redstoner2019.statserver.stats.util;

public class AuthenticationResult {
    private final String message;
    private final boolean success;
    private final int status;
    private String username;

    public AuthenticationResult(String message, boolean success, int status) {
        this.message = message;
        this.success = success;
        this.status = status;
    }

    public AuthenticationResult(String message, boolean success, int status, String username) {
        this.message = message;
        this.success = success;
        this.status = status;
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }
}
