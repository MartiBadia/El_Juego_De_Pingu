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

public class main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
	    // Solo esta línea para cargar el FXML
		Parent root = FXMLLoader.load(getClass().getResource("/resources/PantallaMenu.fxml"));

	    Scene scene = new Scene(root);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("El Juego del Pingüino");
	    primaryStage.centerOnScreen();
	    primaryStage.fullScreenProperty();
	    primaryStage.show();
	}


    public static void main(String[] args) {
        launch(args);
    }
}