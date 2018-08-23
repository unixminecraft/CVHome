package org.cubeville.cvhome.exceptions;

@SuppressWarnings("serial")
public class NoAdminPermissionException extends Exception {
    
    public NoAdminPermissionException() {
        super("");
    }
    
    public NoAdminPermissionException(String message) {
        super("message");
    }
}