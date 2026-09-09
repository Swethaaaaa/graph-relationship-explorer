package com.swetha.graphexplorer.exception;

/**
 * Thrown when {@code shortestPath()} finds no connecting path within the
 * configured hop bound between two otherwise-valid entities.
 */
public class NoPathFoundException extends RuntimeException {

    public NoPathFoundException(String message) {
        super(message);
    }
}
