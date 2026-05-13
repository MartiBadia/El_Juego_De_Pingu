package controlador.principal;

import controlador.gestor.GestorPartida;
import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.jugador.Foca;
import modelo.tablero.Tablero;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

	// Punto de entrada de la aplicación JavaFX, carga la pantalla inicial y configura la ventana
	@Override
	public void start(Stage primaryStage) throws Exception {
	    // Cargamos el diseño de la pantalla de bienvenida desde el archivo FXML
		Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/PantallaIntro.fxml"));

	    Scene scene = new Scene(root);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("El Juego del Pingüino");
	    primaryStage.centerOnScreen();
	    primaryStage.setFullScreenExitHint("");
	    primaryStage.setFullScreen(true);
	    primaryStage.show();
	}


    // El método main de toda la vida que arranca el motor de JavaFX
    public static void Main(String[] args) {
        launch(args);
    }
}   