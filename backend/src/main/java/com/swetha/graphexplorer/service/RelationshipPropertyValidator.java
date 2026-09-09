package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.ProficiencyLevel;
import com.swetha.graphexplorer.domain.enums.RelationshipType;
import com.swetha.graphexplorer.exception.InvalidRelationshipException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Validates and normalizes the free-form {@code properties} map on a
 * {@code CreateRelationshipRequest} against the property shape expected for
 * its {@link RelationshipType}. A relationship type's properties are known
 * at compile time (they mirror the {@code @RelationshipProperties} classes
 * in {@code domain.relationship}), so this is a fixed switch rather than a
 * generic/reflective schema — simpler to read and to unit test.
 */
final class RelationshipPropertyValidator {

    private RelationshipPropertyValidator() {
    }

    static Map<String, Object> normalize(RelationshipType type, Map<String, Object> raw) {
        return switch (type) {
            case USER_WORKS_AT_COMPANY -> worksAt(raw);
            case USER_MEMBER_OF_TEAM -> memberOfTeam(raw);
            case USER_HAS_SKILL -> hasSkill(raw);
            case USER_WORKED_ON_PROJECT -> workedOnProject(raw);
            case PROJECT_USES_TECHNOLOGY -> usesTechnology(raw);
            case USER_COLLABORATED_WITH_USER -> collaboratedWith(raw);
        };
    }

    private static Map<String, Object> worksAt(Map<String, Object> raw) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("jobTitle", requireString(raw, "jobTitle"));
        props.put("startDate", requireLocalDate(raw, "startDate"));
        props.put("current", optionalBoolean(raw, "current", false));
        return props;
    }

    private static Map<String, Object> memberOfTeam(Map<String, Object> raw) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("role", requireString(raw, "role"));
        props.put("joinedDate", requireLocalDate(raw, "joinedDate"));
        return props;
    }

    private static Map<String, Object> hasSkill(Map<String, Object> raw) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("proficiencyLevel", requireEnum(raw, "proficiencyLevel", ProficiencyLevel.class).name());
        int years = requireInt(raw, "yearsOfExperience");
        if (years < 0) {
            throw new InvalidRelationshipException("Property 'yearsOfExperience' cannot be negative");
        }
        props.put("yearsOfExperience", years);
        return props;
    }

    private static Map<String, Object> workedOnProject(Map<String, Object> raw) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("role", requireString(raw, "role"));
        props.put("startDate", requireLocalDate(raw, "startDate"));
        props.put("endDate", optionalLocalDate(raw, "endDate"));
        return props;
    }

    private static Map<String, Object> usesTechnology(Map<String, Object> raw) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("usageContext", requireString(raw, "usageContext"));
        return props;
    }

    private static Map<String, Object> collaboratedWith(Map<String, Object> raw) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("context", requireString(raw, "context"));
        props.put("collaborationCount", optionalInt(raw, "collaborationCount", 1));
        return props;
    }

    private static String requireString(Map<String, Object> raw, String key) {
        Object value = raw.get(key);
        if (!(value instanceof String s) || s.isBlank()) {
            throw new InvalidRelationshipException(
                    "Property '%s' is required and must be a non-blank string".formatted(key));
        }
        return s;
    }

    private static LocalDate requireLocalDate(Map<String, Object> raw, String key) {
        LocalDate parsed = optionalLocalDate(raw, key);
        if (parsed == null) {
            throw new InvalidRelationshipException(
                    "Property '%s' is required and must be an ISO date string (yyyy-MM-dd)".formatted(key));
        }
        return parsed;
    }

    private static LocalDate optionalLocalDate(Map<String, Object> raw, String key) {
        Object value = raw.get(key);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String s)) {
            throw new InvalidRelationshipException(
                    "Property '%s' must be an ISO date string (yyyy-MM-dd)".formatted(key));
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new InvalidRelationshipException(
                    "Property '%s' must be a valid ISO date string (yyyy-MM-dd), got '%s'".formatted(key, s));
        }
    }

    private static int requireInt(Map<String, Object> raw, String key) {
        Object value = raw.get(key);
        if (value instanceof Number n) {
            return n.intValue();
        }
        throw new InvalidRelationshipException("Property '%s' is required and must be a number".formatted(key));
    }

    private static int optionalInt(Map<String, Object> raw, String key, int defaultValue) {
        Object value = raw.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        throw new InvalidRelationshipException("Property '%s' must be a number".formatted(key));
    }

    private static boolean optionalBoolean(Map<String, Object> raw, String key, boolean defaultValue) {
        Object value = raw.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        throw new InvalidRelationshipException("Property '%s' must be a boolean".formatted(key));
    }

    private static <E extends Enum<E>> E requireEnum(Map<String, Object> raw, String key, Class<E> enumType) {
        Object value = raw.get(key);
        if (!(value instanceof String s)) {
            throw new InvalidRelationshipException(
                    "Property '%s' is required and must be one of %s".formatted(key, Arrays.toString(enumType.getEnumConstants())));
        }
        try {
            return Enum.valueOf(enumType, s.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InvalidRelationshipException(
                    "Property '%s' must be one of %s, got '%s'".formatted(key, Arrays.toString(enumType.getEnumConstants()), s));
        }
    }
}
