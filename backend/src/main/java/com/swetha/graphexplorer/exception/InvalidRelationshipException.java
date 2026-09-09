package com.swetha.graphexplorer.exception;

/**
 * Thrown when a relationship creation request is structurally invalid:
 * wrong node type for the given relationship type, missing/malformed
 * relationship properties, or a semantically nonsensical relationship
 * (e.g. a user "collaborating with" themselves).
 */
public class InvalidRelationshipException extends RuntimeException {

    public InvalidRelationshipException(String message) {
        super(message);
    }
}
