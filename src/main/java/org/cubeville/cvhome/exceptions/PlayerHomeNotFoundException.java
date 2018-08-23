package org.cubeville.cvhome.exceptions;

@SuppressWarnings("serial")
public class PlayerHomeNotFoundException extends Exception {
    
    public PlayerHomeNotFoundException() {
        super("");
    }
    
    public PlayerHomeNotFoundException(String message) {
        super(message);
    }
}
