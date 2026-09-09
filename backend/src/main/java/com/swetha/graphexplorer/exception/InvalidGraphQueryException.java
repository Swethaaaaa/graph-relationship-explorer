package com.swetha.graphexplorer.exception;

/**
 * Thrown for structurally invalid graph-query input: a traversal depth
 * outside the allowed range, or comparing an entity against itself where
 * that's meaningless (e.g. common-connections of a user with themselves).
 */
public class InvalidGraphQueryException extends RuntimeException {

    public InvalidGraphQueryException(String message) {
        super(message);
    }
}
