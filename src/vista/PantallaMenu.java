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

public class PantallaMenu {

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML
    private void initialize() {
        // This method is called automatically after the FXML is loaded
        // You can set initial values or add listeners here
        System.out.println("pantallaPrincipalController initialized");
    }

    @FXML
    private void handleNewGame() {
        System.out.println("New Game clicked");
        // TODO
    }

    @FXML
    private void handleSaveGame() {
        System.out.println("Save Game clicked");
        // TODO
    }

    @FXML
    private void handleLoadGame() {
        System.out.println("Load Game clicked");
        // TODO
    }

    @FXML
    private void handleQuitGame() {
        System.out.println("Quit Game clicked");
        // TODO
        System.exit(0);
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = userField.getText();
        String password = passField.getText();

        System.out.println("Login button pressed. User: " + username);

        if (!username.isEmpty() && !password.isEmpty()) {
            try {
                // Intentamos encontrar el recurso de la forma más compatible posible
                java.net.URL fxmlUrl = PantallaMenu.class.getResource("/resources/PantallaJuego.fxml");
                
                if (fxmlUrl == null) {
                    fxmlUrl = PantallaMenu.class.getClassLoader().getResource("resources/PantallaJuego.fxml");
                }
                
                if (fxmlUrl == null) {
                    fxmlUrl = PantallaMenu.class.getResource("PantallaJuego.fxml");
                }

                System.out.println("DEBUG: URL del FXML: " + fxmlUrl);

                if (fxmlUrl == null) {
                    System.err.println("CRITICAL ERROR: No se encuentra PantallaJuego.fxml. Revisa que esté en src/resources y que el proyecto esté refrescado.");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();
                Scene scene = new Scene(root);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("El Juego de Pingu - Partida");
                stage.show();

            } catch (Exception e) {
                System.err.println("Error fatal al cargar la pantalla de juego:");
                e.printStackTrace();
            }
        } else {
            System.out.println("Por favor, introduce usuario y contraseña.");
        }
    }


    @FXML
    private void handleRegister() {
        System.out.println("Register pressed");
        // TODO
    }
}