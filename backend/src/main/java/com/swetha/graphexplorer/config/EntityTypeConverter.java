package com.swetha.graphexplorer.config;

import com.swetha.graphexplorer.domain.enums.EntityType;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * Lets {@code /api/graph/{entityType}/...} accept a lowercase, URL-friendly
 * entity type ("user", "company") and converts it to {@link EntityType}. A
 * failure here is surfaced by Spring as a MethodArgumentTypeMismatchException,
 * which GlobalExceptionHandler already turns into a 400 INVALID_PARAMETER —
 * so an unknown entity type never reaches a service or repository.
 */
public class EntityTypeConverter implements Converter<String, EntityType> {

    @Override
    public EntityType convert(@NonNull String source) {
        return EntityType.valueOf(source.trim().toUpperCase(Locale.ROOT));
    }
}
