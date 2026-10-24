package com.raxim.myscoutee.common.util;

import java.util.Collection;
import java.util.List;
import org.springframework.util.StringUtils;

/** Shared trim-only text operations; callers retain their distinct null/default policies. */
public final class TextValues {
    private TextValues() {}
    public static String trim(String value) { return value == null ? "" : value.trim(); }
    public static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) if (StringUtils.hasText(value)) return value.trim();
        return null;
    }
    public static List<String> distinctIds(Collection<String> values) {
        return values == null ? List.of() : values.stream().map(TextValues::trim)
                .filter(StringUtils::hasText).distinct().toList();
    }
    public static String firstTwoInitials(String value) {
        String normalized = trim(value);
        if (!StringUtils.hasText(normalized)) return "";
        StringBuilder result = new StringBuilder();
        for (String part : normalized.split("\\s+")) {
            if (!part.isEmpty()) result.append(Character.toUpperCase(part.charAt(0)));
            if (result.length() >= 2) break;
        }
        return result.toString();
    }

    public static List<String> trimmedStrings(Collection<String> values) {
        if (values == null) return List.of();
        List<String> result = new java.util.ArrayList<>();
        for (String value : values) {
            String text = trim(value);
            if (StringUtils.hasText(text)) result.add(text);
        }
        return result;
    }

}
