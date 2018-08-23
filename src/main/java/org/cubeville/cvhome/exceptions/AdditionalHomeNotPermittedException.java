package org.cubeville.cvhome.exceptions;

@SuppressWarnings("serial")
public class AdditionalHomeNotPermittedException extends Exception {
    
    public AdditionalHomeNotPermittedException() {
        super("");
    }
    
    public AdditionalHomeNotPermittedException(String message) {
        super(message);
    }
}
