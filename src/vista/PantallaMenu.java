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

import controlador.gestor.GestorUsuarios;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PantallaMenu {

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label mensajeLoginLabel;

    private GestorUsuarios gestorUsuarios;

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
        gestorUsuarios = new GestorUsuarios();
        // Aseguramos que solo el login sea visible al inicio
        loginCard.setVisible(true);
        optionsCard.setVisible(false);
        configCard.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = userField.getText().trim();
        String password = passField.getText();

        System.out.println("Login button pressed. User: " + username);

        if (username.isEmpty() || password.isEmpty()) {
            mensajeLoginLabel.setText("Por favor, introduce usuario y contraseña.");
            mensajeLoginLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        if (gestorUsuarios.validarLogin(username, password)) {
            mensajeLoginLabel.setText("Inicia sesión para jugar");
            mensajeLoginLabel.setStyle(""); // Restablecer
            showOptionsCard();
        } else {
            mensajeLoginLabel.setText("Credenciales inválidas o no registrado.");
            mensajeLoginLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    @FXML
    private void handleLogout() {
        loginCard.setVisible(true);
        optionsCard.setVisible(false);
        configCard.setVisible(false);
        userField.clear();
        passField.clear();
        if (mensajeLoginLabel != null) {
            mensajeLoginLabel.setText("Inicia sesión para jugar");
            mensajeLoginLabel.setStyle("");
        }
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
        String username = userField.getText().trim();
        String password = passField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mensajeLoginLabel.setText("Introduce un usuario y contraseña.");
            mensajeLoginLabel.setStyle("-fx-text-fill: #feca57;");
            return;
        }
        
        if (username.contains(",") || password.contains(",")) {
            mensajeLoginLabel.setText("El usuario/contraseña no pueden tener comas.");
            mensajeLoginLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        if (gestorUsuarios.registrarUsuario(username, password)) {
            mensajeLoginLabel.setText("¡Usuario '" + username + "' registrado!");
            mensajeLoginLabel.setStyle("-fx-text-fill: #1dd1a1;");
        } else {
            mensajeLoginLabel.setText("Error: El usuario '" + username + "' ya existe.");
            mensajeLoginLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    @FXML
    private void handleExitAppDialog() {
        Stage stage = new Stage();
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED); 

        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(25);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: #ff0000; -fx-padding: 40; -fx-border-color: #8b0000; -fx-border-width: 6; -fx-background-radius: 10; -fx-border-radius: 10;");

        // Imagen central y grande
        ImageView image = null;
        try {
            image = new ImageView(new javafx.scene.image.Image(getClass().getResource("/resources/alerta_salidaa.png").toExternalForm()));
            image.setFitWidth(350); 
            image.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Imagen alerta_salidaa no encontrada");
        }

        Label label = new Label("¿ESTÁS SEGURO DE QUE QUIERES ABANDONAR EL JUEGO?");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 5, 0.5, 0, 0);");

        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(30);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button btnYes = new Button("SÍ, SALIR");
        btnYes.setStyle("-fx-background-color: #111; -fx-text-fill: #ff4757; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 12 25; -fx-border-color: #ff4757; -fx-border-width: 2; -fx-border-radius: 5;");
        btnYes.setOnAction(e -> System.exit(0));

        Button btnNo = new Button("NO, QUEDARME");
        btnNo.setStyle("-fx-background-color: #111; -fx-text-fill: #1dd1a1; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 12 25; -fx-border-color: #1dd1a1; -fx-border-width: 2; -fx-border-radius: 5;");
        btnNo.setOnAction(e -> stage.close());

        buttons.getChildren().addAll(btnYes, btnNo);
        if (image != null) {
            root.getChildren().add(image);
        }
        root.getChildren().addAll(label, buttons);

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML private void handleQuitGame() { System.exit(0); }
}