package io.confluent.ksql.client;

/**
 * Exception class for KSQLDB client operations.
 */
public class KSQLException extends Exception {
    
    public KSQLException(String message) {
        super(message);
    }
    
    public KSQLException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public KSQLException(Throwable cause) {
        super(cause);
    }
}