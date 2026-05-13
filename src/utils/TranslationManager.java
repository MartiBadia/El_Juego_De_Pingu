package utils;

import java.util.Locale;
import java.util.ResourceBundle;

// Clase encargada de gestionar los idiomas y las traducciones de toda la aplicación
public class TranslationManager {
    // Idioma por defecto: Español
    private static Locale currentLocale = new Locale("es");
    // Cargador de archivos .properties con los textos traducidos
    private static ResourceBundle messages = ResourceBundle.getBundle("resources.i18n.messages", currentLocale);

    // Cambia el idioma de la aplicación en tiempo de ejecución
    public static void setLocale(Locale locale) {
        currentLocale = locale;
        messages = ResourceBundle.getBundle("resources.i18n.messages", currentLocale);
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    // Obtiene un texto traducido mediante su clave (ej: "menu.start")
    public static String get(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key; // Si no existe, devuelve la propia clave para no romper nada
        }
    }

    // Obtiene un texto y permite inyectar variables (ej: "¡Hola {0}!")
    public static String get(String key, Object... args) {
        try {
            String pattern = messages.getString(key);
            return java.text.MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return key;
        }
    }

    // Devuelve el paquete completo de mensajes para uso directo en FXML o controladores
    public static ResourceBundle getBundle() {
        return messages;
    }
}
