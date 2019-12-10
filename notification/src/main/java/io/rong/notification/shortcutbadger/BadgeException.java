package io.rong.notification.shortcutbadger;

public class BadgeException extends Exception {
    public BadgeException(String message) {
        super(message);
    }

    public BadgeException(String message, Exception e) {
        super(message, e);
    }
}
