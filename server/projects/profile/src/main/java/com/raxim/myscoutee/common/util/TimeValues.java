package com.raxim.myscoutee.common.util;

import java.time.*;
import java.time.format.DateTimeParseException;
import org.springframework.util.StringUtils;

/** The existing UTC sort and optional ISO timestamp policies, shared without changing fallbacks. */
public final class TimeValues {
    private TimeValues() {}
    public static Instant max(Instant left, Instant right) {
        if (left == null) return right;
        if (right == null) return left;
        return left.isAfter(right) ? left : right;
    }
    public static Instant parseInstant(String value) {
        String normalized = TextValues.trim(value);
        if (!StringUtils.hasText(normalized)) return null;
        try { return Instant.parse(normalized); }
        catch (DateTimeParseException invalid) { return null; }
    }
    public static long sortableDate(String value) {
        if (!StringUtils.hasText(value)) return 0L;
        String normalized = value.trim();
        try { return Instant.parse(normalized).toEpochMilli(); }
        catch (DateTimeParseException invalidInstant) {
            try { return LocalDateTime.parse(normalized).toInstant(ZoneOffset.UTC).toEpochMilli(); }
            catch (DateTimeParseException invalidTime) {
                try { return LocalDate.parse(normalized).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(); }
                catch (DateTimeParseException invalidDate) { return 0L; }
            }
        }
    }

    public static Instant parseExactInstant(String value) {
        try { return StringUtils.hasText(value) ? Instant.parse(value) : null; }
        catch (RuntimeException invalid) { return null; }
    }

}
