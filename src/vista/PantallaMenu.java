package vista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.util.ArrayList;

public class PantallaMenu {

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    // Nuevos campos para las tarjetas
    @FXML private javafx.scene.layout.VBox loginCard;
    @FXML private javafx.scene.layout.VBox optionsCard;
    @FXML private javafx.scene.layout.VBox configCard;

    // Campos de configuración de partida
    @FXML private TextField playerCountField;
    @FXML private TextField cpuCountField;
    @FXML private TextField sealCountField;

    @FXML
    private void initialize() {
        System.out.println("PantallaMenu initialized");
        // Aseguramos que solo el login sea visible al inicio
        loginCard.setVisible(true);
        optionsCard.setVisible(false);
        configCard.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = userField.getText();
        String password = passField.getText();

        System.out.println("Login button pressed. User: " + username);

        // Validación simple para la demo
        if (!username.isEmpty() && !password.isEmpty()) {
            showOptionsCard();
        } else {
            System.out.println("Por favor, introduce usuario y contraseña.");
        }
    }

    @FXML
    private void handleLogout() {
        loginCard.setVisible(true);
        optionsCard.setVisible(false);
        configCard.setVisible(false);
        userField.clear();
        passField.clear();
    }

    @FXML
    public void showOptionsCard() {
        loginCard.setVisible(false);
        optionsCard.setVisible(true);
        configCard.setVisible(false);
    }

    @FXML
    public void showConfigCard() {
        loginCard.setVisible(false);
        optionsCard.setVisible(false);
        configCard.setVisible(true);
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        try {
            int numHumans = Integer.parseInt(playerCountField.getText());
            int numCPUs = Integer.parseInt(cpuCountField.getText());
            int numSeals = Integer.parseInt(sealCountField.getText());

            // Validaciones básicas
            if (numHumans + numCPUs < 1) {
                System.err.println("Debe haber al menos un pingüino.");
                return;
            }

            // Crear la partida
            ArrayList<modelo.jugador.Jugador> jugadores = new ArrayList<>();
            
            // Añadir Humanos
            for (int i = 1; i <= numHumans; i++) {
                modelo.jugador.Pinguino p = new modelo.jugador.Pinguino("Jugador " + i, "Azul");
                p.getInventario().añadirItem(new modelo.items.Dado());
                p.setEsIA(false);
                jugadores.add(p);
            }

            // Añadir CPUs
            for (int i = 1; i <= numCPUs; i++) {
                modelo.jugador.Pinguino cpu = new modelo.jugador.Pinguino("CPU " + i, "Gris");
                cpu.getInventario().añadirItem(new modelo.items.Dado());
                cpu.setEsIA(true);
                jugadores.add(cpu);
            }

            // Añadir Focas (siempre son IA)
            for (int i = 1; i <= numSeals; i++) {
                modelo.jugador.Foca foca = new modelo.jugador.Foca("Foca " + i, "Blanco");
                foca.setEsIA(true);
                jugadores.add(foca);
            }

            modelo.tablero.Tablero tablero = new modelo.tablero.Tablero();
            tablero.generarTableroAleatorio();

            modelo.partida.Partida nuevaPartida = new modelo.partida.Partida(tablero, jugadores);

            // Cambiar a la pantalla de juego
            cambiarAPantallaJuego(event, nuevaPartida);

        } catch (NumberFormatException e) {
            System.err.println("Por favor introduce números válidos.");
        }
    }

    private void cambiarAPantallaJuego(ActionEvent event, modelo.partida.Partida partida) {
        try {
            java.net.URL fxmlUrl = PantallaMenu.class.getResource("/resources/PantallaJuego.fxml");
            if (fxmlUrl == null) fxmlUrl = PantallaMenu.class.getClassLoader().getResource("resources/PantallaJuego.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // Pasar la partida al controlador de PantallaJuego
            PantallaJuego controller = loader.getController();
            controller.prepararPartidaPersonalizada(partida);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("El Juego de Pingu - Partida");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoadGame(ActionEvent event) {
        // En una implementación real, aquí mostraríamos un diálogo para elegir qué partida cargar.
        // Por ahora cargamos la ID 1 si existe, o mostramos un mensaje.
        System.out.println("Cargando partida...");
        try {
            // Reutilizamos la lógica de cambio de pantalla pero indicando que cargue
            java.net.URL fxmlUrl = PantallaMenu.class.getResource("/resources/PantallaJuego.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            PantallaJuego controller = loader.getController();
            // Le pedimos que cargue la partida 1 (para la demo)
            controller.cargarPartidaEspecifica(1);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        System.out.println("Register pressed");
    }

    @FXML private void handleQuitGame() { System.exit(0); }
}