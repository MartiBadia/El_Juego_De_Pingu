package vista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.sql.Connection;
import java.util.Optional;

public class PantallaMenu {

    private static Connection conexionBBDD;
    private static String usuarioLogueado; 

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label feedbackLabel;

    @FXML private VBox loginCard;
    @FXML private VBox optionsCard;
    @FXML private VBox configCard;
    @FXML private VBox loadGameCard;
    @FXML private VBox skinSelectionCard;
    @FXML private VBox gamesListContainer;

    @FXML private TextField playerCountField;
    @FXML private TextField sealCountField;
    @FXML private Label errorLabel;
    
    @FXML private Label skinTitle;
    @FXML private Label skinErrorLabel;
    @FXML private javafx.scene.image.ImageView skinPreview;

    // Variables de estado para selección de skins
    private int numHumans;
    private int numSeals;
    private int currentSkinPlayerIndex;
    private ArrayList<String> selectedSkins = new ArrayList<>();
    private ArrayList<modelo.jugador.Jugador> jugadoresTemp = new ArrayList<>();

    private final String[] SKIN_FILES = {"skin_dino.png", "skin_chef.png", "skin_ninja.png", "skin_ghost.png"};
    private int currentCarouselIdx = 0;

    @FXML
    private void initialize() {
        hideAllCards();
        if (loginCard != null) loginCard.setVisible(true);

        if (conexionBBDD == null) {
            conexionBBDD = controlador.gestionbbdd.BBDD.conectarPredeterminado();
        }
    }

    private void hideAllCards() {
        if (loginCard != null) loginCard.setVisible(false);
        if (optionsCard != null) optionsCard.setVisible(false);
        if (configCard != null) configCard.setVisible(false);
        if (loadGameCard != null) loadGameCard.setVisible(false);
        if (skinSelectionCard != null) skinSelectionCard.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = userField.getText();
        String password = passField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText("Introduce usuario y contraseña.");
            return;
        }

        controlador.gestionbbdd.BBDD dbHelper = new controlador.gestionbbdd.BBDD();
        if (dbHelper.loginUsuario(conexionBBDD, username, password)) {
            usuarioLogueado = username;
            feedbackLabel.setText("Login correcto. ¡Hola " + username + "!");
            showOptionsCard();
        } else {
            feedbackLabel.setText("¡Usuario no encontrado!");
            feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    @FXML
    private void handleRegister() {
        String username = userField.getText();
        String password = passField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText("Introduce datos para el registro.");
            return;
        }

        controlador.gestionbbdd.BBDD dbHelper = new controlador.gestionbbdd.BBDD();
        if (dbHelper.existeUsuario(conexionBBDD, username)) {
            feedbackLabel.setText("El usuario ya existe.");
        } else {
            if (dbHelper.registrarUsuario(conexionBBDD, username, password)) {
                feedbackLabel.setText("Se ha registrado el usuario correctamente.");
            } else {
                feedbackLabel.setText("Error al registrar usuario.");
            }
        }
    }

    @FXML
    private void handleLoadGame(ActionEvent event) {
        hideAllCards();
        loadGameCard.setVisible(true);
        refreshGamesList(event);
    }

    private void refreshGamesList(ActionEvent event) {
        gamesListContainer.getChildren().clear();
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        ArrayList<LinkedHashMap<String, String>> partidas = helper.listarPartidas(conexionBBDD, usuarioLogueado);
        
        if (partidas == null || partidas.isEmpty()) {
            gamesListContainer.getChildren().add(new Label("No tienes partidas guardadas."));
        } else {
            for (LinkedHashMap<String, String> p : partidas) {
                String id = p.get("ID_PARTIDA");
                String turnos = p.get("TURNOS");
                String estado = "S".equals(p.get("FINALIZADA")) ? "Terminada" : "En curso";
                
                HBox itemRow = new HBox(10);
                itemRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                itemRow.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 8; -fx-background-radius: 8;");

                Button loadBtn = new Button("Partida #" + id + " | Turnos: " + turnos + " | " + estado);
                loadBtn.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(loadBtn, Priority.ALWAYS);
                loadBtn.setOnAction(e -> cargarPartidaPorId(event, Integer.parseInt(id)));
                
                Button delBtn = new Button("🗑️");
                delBtn.setStyle("-fx-background-color: rgba(255, 107, 107, 0.2); -fx-text-fill: #ff6b6b; -fx-padding: 8;");
                delBtn.setOnAction(e -> confirmarEliminacionPartida(event, Integer.parseInt(id), "Partida #" + id));
                
                itemRow.getChildren().addAll(loadBtn, delBtn);
                gamesListContainer.getChildren().add(itemRow);
            }
        }
    }

    private void confirmarEliminacionPartida(ActionEvent event, int idPartida, String nombrePartida) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres eliminar la partida \"" + nombrePartida + "\"?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
            if (helper.eliminarPartida(conexionBBDD, idPartida)) {
                refreshGamesList(event);
            }
        }
    }

    private void cargarPartidaPorId(ActionEvent event, int idPartida) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/resources/PantallaJuego.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            PantallaJuego controller = loader.getController();
            controller.setConexion(conexionBBDD);
            controller.setUsuario(usuarioLogueado); 
            controller.cargarPartidaEspecifica(idPartida);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        try {
            numHumans = Integer.parseInt(playerCountField.getText());
            numSeals = Integer.parseInt(sealCountField.getText());

            if (numSeals < 1 || (numHumans + numSeals > 4) || numHumans < 1) {
                errorLabel.setText("Máximo 4 entidades y mínimo 1 pingüino.");
                return;
            }

            // Iniciar proceso de selección de skins
            hideAllCards();
            skinSelectionCard.setVisible(true);
            currentSkinPlayerIndex = 1;
            selectedSkins.clear();
            jugadoresTemp.clear();
            currentCarouselIdx = 0;
            skinPreview.setImage(new javafx.scene.image.Image("/resources/skins/" + SKIN_FILES[0]));
            actualizarEstadoSeleccionSkin();
            skinTitle.setText("Jugador " + currentSkinPlayerIndex + ": Elige tu Skin");
            skinErrorLabel.setText("");

        } catch (NumberFormatException e) {
            errorLabel.setText("Introduce números válidos");
        }
    }

    @FXML
    private void handlePrevSkin() {
        currentCarouselIdx = (currentCarouselIdx - 1 + SKIN_FILES.length) % SKIN_FILES.length;
        animateSkinChange(true);
    }

    @FXML
    private void handleNextSkin() {
        currentCarouselIdx = (currentCarouselIdx + 1) % SKIN_FILES.length;
        animateSkinChange(false);
    }

    private void animateSkinChange(boolean reverse) {
        javafx.animation.ScaleTransition scaleOut = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), skinPreview);
        scaleOut.setFromX(1.0); scaleOut.setFromY(1.0);
        scaleOut.setToX(0.1); scaleOut.setToY(0.1);
        
        scaleOut.setOnFinished(e -> {
            skinPreview.setImage(new javafx.scene.image.Image("/resources/skins/" + SKIN_FILES[currentCarouselIdx]));
            actualizarEstadoSeleccionSkin();
            javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), skinPreview);
            scaleIn.setFromX(0.1); scaleIn.setFromY(0.1);
            scaleIn.setToX(1.1); scaleIn.setToY(1.1);
            scaleIn.setOnFinished(e2 -> {
                javafx.animation.ScaleTransition settle = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(100), skinPreview);
                settle.setToX(1.0); settle.setToY(1.0);
                settle.play();
            });
            scaleIn.play();
        });
        
        scaleOut.play();
    }

    @FXML
    private void handleSelectCurrentSkin(ActionEvent event) {
        processSkinSelection(event, SKIN_FILES[currentCarouselIdx]);
    }

    private void actualizarEstadoSeleccionSkin() {
        if (selectedSkins.contains(SKIN_FILES[currentCarouselIdx])) {
            skinErrorLabel.setText("¡Esta skin ya está en uso!");
            skinPreview.setOpacity(0.5); // Feedback visual opcional
        } else {
            skinErrorLabel.setText("");
            skinPreview.setOpacity(1.0);
        }
    }

    private void processSkinSelection(ActionEvent event, String skinFile) {
        if (selectedSkins.contains(skinFile)) {
            skinErrorLabel.setText("¡Esta skin ya está en uso!");
            return;
        }

        selectedSkins.add(skinFile);
        String color = (currentSkinPlayerIndex == 1) ? "Azul" : (currentSkinPlayerIndex == 2) ? "Naranja" : (currentSkinPlayerIndex == 3) ? "Verde" : "Amarillo";
        modelo.jugador.Pinguino p = new modelo.jugador.Pinguino("Jugador " + currentSkinPlayerIndex, color);
        p.setSkin(skinFile);
        p.getInventario().añadirItem(new modelo.items.Dado());
        p.setEsIA(false);
        jugadoresTemp.add(p);

        if (currentSkinPlayerIndex < numHumans) {
            currentSkinPlayerIndex++;
            skinTitle.setText("Jugador " + currentSkinPlayerIndex + ": Elige tu Skin");
            actualizarEstadoSeleccionSkin();
        } else {
            // Todos los humanos han elegido, añadir focas y empezar
            // Preparamos lista de skins de focas disponibles y las barajamos
            ArrayList<String> focaSkins = new ArrayList<>();
            focaSkins.add("foca_skin1.png");
            focaSkins.add("foca_skin2.png");
            focaSkins.add("foca_skin3.png");
            java.util.Collections.shuffle(focaSkins);

            for (int i = 1; i <= numSeals; i++) {
                modelo.jugador.Foca foca = new modelo.jugador.Foca("Foca " + i, "Blanco");
                foca.setEsIA(true);
                // Asignamos una skin única de la lista barajada (usando módulo por si hubiera más de 3 focas)
                String skinUnica = focaSkins.get((i - 1) % focaSkins.size());
                foca.setSkin(skinUnica);
                jugadoresTemp.add(foca);
            }

            modelo.tablero.Tablero tablero = new modelo.tablero.Tablero();
            tablero.generarTableroAleatorio();
            modelo.partida.Partida nuevaPartida = new modelo.partida.Partida(tablero, jugadoresTemp);
            cambiarAPantallaJuego(event, nuevaPartida);
        }
    }

    private void cambiarAPantallaJuego(ActionEvent event, modelo.partida.Partida partida) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/resources/PantallaJuego.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            PantallaJuego controller = loader.getController();
            controller.setConexion(conexionBBDD);
            controller.setUsuario(usuarioLogueado); 
            controller.prepararPartidaPersonalizada(partida);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleLogout() {
        hideAllCards();
        loginCard.setVisible(true);
        usuarioLogueado = null; 
        userField.clear();
        passField.clear();
        feedbackLabel.setText("Sesión cerrada.");
    }

    @FXML public void showOptionsCard() { hideAllCards(); optionsCard.setVisible(true); }
    @FXML public void showConfigCard() { hideAllCards(); configCard.setVisible(true); }
    @FXML private void handleQuitGame() { System.exit(0); }
}