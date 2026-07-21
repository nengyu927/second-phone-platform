package com.example.secondphone.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class AppleProductModelNormalizer {
    private static final Pattern IPHONE = Pattern.compile("(?i)\\biphone\\s+(?:SE(?:\\s*\\([^)]*generation\\))?|\\d{1,2}(?:\\s+(?:pro\\s+max|pro|plus|mini))?)");
    private static final Pattern WATCH = Pattern.compile("(?i)\\bapple\\s+watch\\s+(?:series\\s+\\d{1,2}|SE(?:\\s+\\d)?|ultra(?:\\s+\\d)?)");
    private static final Pattern IPAD = Pattern.compile("(?i)\\bipad(?:\\s+(?:air|mini|pro))?(?:\\s+\\d{1,2})?");

    public Optional<String> normalize(String productName) {
        if (productName == null || productName.isBlank()) return Optional.empty();
        String compact = productName.replaceAll("[＿_]", " ").replaceAll("\\s+", " ").trim();
        Matcher matcher = IPHONE.matcher(compact);
        if (matcher.find()) return Optional.of(canonical(matcher.group(), "iPhone"));
        matcher = WATCH.matcher(compact);
        if (matcher.find()) return Optional.of(canonical(matcher.group(), "Apple Watch"));
        matcher = IPAD.matcher(compact);
        if (matcher.find()) return Optional.of(canonical(matcher.group(), "iPad"));
        return Optional.empty();
    }

    public String productType(String normalizedModelName) {
        if (normalizedModelName.startsWith("iPhone")) return "iPhone";
        if (normalizedModelName.startsWith("Apple Watch")) return "Apple Watch";
        if (normalizedModelName.startsWith("iPad")) return "iPad";
        return "Apple 裝置";
    }

    private String canonical(String value, String prefix) {
        String result = prefix + value.substring(prefix.length());
        return result.replaceAll("(?i)\\bpro max\\b", "Pro Max")
                .replaceAll("(?i)\\bpro\\b", "Pro")
                .replaceAll("(?i)\\bplus\\b", "Plus")
                .replaceAll("(?i)\\bmini\\b", "mini")
                .replaceAll("(?i)\\bseries\\b", "Series")
                .replaceAll("(?i)\\bultra\\b", "Ultra")
                .replaceAll("(?i)\\bair\\b", "Air")
                .replaceAll("(?i)\\bse\\b", "SE")
                .replaceAll("\\s+", " ").trim();
    }
}
