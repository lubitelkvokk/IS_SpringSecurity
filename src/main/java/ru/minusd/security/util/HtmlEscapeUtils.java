package ru.minusd.security.util;

/**
 * Экранирование и проверка XSS паттернов
 */
public class HtmlEscapeUtils {
    
    /**
     * Проверяет наличие XSS паттернов
     */
    public static boolean containsXssPatterns(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        String lowercaseInput = input.toLowerCase();
        return lowercaseInput.contains("<script") ||
               lowercaseInput.contains("javascript:") ||
               lowercaseInput.contains("onerror=") ||
               lowercaseInput.contains("onload=") ||
               lowercaseInput.contains("<iframe") ||
               lowercaseInput.contains("<embed") ||
               lowercaseInput.contains("<object") ||
               lowercaseInput.contains("onclick=") ||
               lowercaseInput.contains("onmouseover=");
    }
}
