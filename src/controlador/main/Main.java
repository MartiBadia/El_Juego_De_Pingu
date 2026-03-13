package controlador.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que lanza la aplicación JavaFX.
 * Hereda de Application para gestionar el ciclo de vida de la interfaz.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Se carga el archivo FXML del menú inicial desde la carpeta de recursos.
            // Es importante que la ruta coincida con la estructura del proyecto.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/PantallaMenu.fxml"));
            Parent root = loader.load();
            
            // Se crea la escena con el contenido cargado del FXML.
            Scene scene = new Scene(root);
            
            // Configuración de la ventana principal (Stage).
            primaryStage.setTitle("El Juego de Pingu - Menú Principal");
            primaryStage.setScene(scene);
            
            // Mostrar la ventana.
            primaryStage.show();
            
        } catch(Exception e) {
            System.err.println("Error al iniciar la aplicación JavaFX:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // En lugar de ejecutar el bucle de simulación por consola,
        // iniciamos el entorno gráfico de JavaFX.
        launch(args);
    }
}
