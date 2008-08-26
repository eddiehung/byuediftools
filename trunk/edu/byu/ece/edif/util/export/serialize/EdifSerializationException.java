package edu.byu.ece.edif.util.export.serialize;

import java.io.IOException;

public class EdifSerializationException extends IOException {

    public EdifSerializationException() {
        super();
    }

    public EdifSerializationException(String message) {
        super(message);
    }

}
