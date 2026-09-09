package com.swetha.graphexplorer.exception;

import com.swetha.graphexplorer.domain.enums.EntityType;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(EntityType entityType, String id) {
        super("%s with id '%s' was not found".formatted(entityType.label(), id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
