package utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationManager {
    private static Locale currentLocale = new Locale("es");
    private static ResourceBundle messages = ResourceBundle.getBundle("resources.i18n.messages", currentLocale);

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        messages = ResourceBundle.getBundle("resources.i18n.messages", currentLocale);
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static String get(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static String get(String key, Object... args) {
        try {
            String pattern = messages.getString(key);
            return java.text.MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return key;
        }
    }

    public static ResourceBundle getBundle() {
        return messages;
    }
}
