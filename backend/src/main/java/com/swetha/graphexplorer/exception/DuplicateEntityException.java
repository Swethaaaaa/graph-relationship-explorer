package com.swetha.graphexplorer.exception;

/**
 * Thrown when creating an entity would violate a business-key uniqueness
 * rule (e.g. a User email or Company name that already exists).
 */
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}
