package vista;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.sql.Connection;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Locale;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PantallaMenu {

    private static Connection conexionBBDD;
    private static String usuarioLogueado;
    private static MediaPlayer musicPlayer;

    // ── Modo de autenticación ──
    private enum AuthMode { CREATE_PLAYER, LOAD_GAME, LOAD_PLAYER_AUTH }
    private AuthMode currentAuthMode = AuthMode.CREATE_PLAYER;
    private int idPartidaPendienteCargar = -1;
    private ArrayList<String> humanPlayersToAuth = new ArrayList<>();
    private int currentLoadPlayerIdx = 0;

    @FXML private StackPane rootPane;
    @FXML private BorderPane mainContent;
    @FXML private ImageView gameTitleImage;
    @FXML private ImageView backgroundImage;
    @FXML private Canvas snowCanvas;

    // Auth card por jugador
    @FXML private VBox playerAuthCard;
    @FXML private Label playerAuthInfoLabel;
    @FXML private TextField userField;
    @FXML private PasswordField passField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button authBackBtn;
    @FXML private Label feedbackLabel;

    @FXML private VBox optionsCard;
    @FXML private VBox configCard;
    @FXML private VBox loadGameCard;
    @FXML private VBox skinSelectionCard;
    @FXML private VBox gamesListContainer;
    @FXML private VBox audioCard;
    @FXML private VBox rankingCard;
    @FXML private VBox rankingParticipacionContainer;
    @FXML private VBox rankingVictoriasContainer;

    @FXML private Label playerCountLabel;
    @FXML private Label sealCountLabel;
    @FXML private Label errorLabel;

    @FXML private Label skinTitle;
    @FXML private Label skinErrorLabel;
    @FXML private javafx.scene.image.ImageView skinPreview;

    // --- Componentes para traducción ---
    @FXML private Label loginTitle;
    @FXML private Label userLabel;
    @FXML private Label passLabel;

    @FXML private Label optionsTitle;
    @FXML private Button createGameBtn;
    @FXML private Button loadSavedGameBtn;
    @FXML private Label optionsHint;

    @FXML private Label configTitle;
    @FXML private Label penguinsLabel;
    @FXML private Label sealsLabel;
    @FXML private Button configBackBtn;
    @FXML private Button configStartBtn;
    @FXML private Label configHint;

    @FXML private Label skinNameLabel;
    @FXML private Button skinConfirmBtn;
    @FXML private Button skinCancelBtn;

    @FXML private Label loadTitle;
    @FXML private Button loadBackBtn;

    @FXML private javafx.scene.control.Slider volumeSlider;
    @FXML private Button muteButton;
    @FXML private Label volumeIconLabel;

    // --- Estado interno ---
    private Random rand = new Random();

    // Usuarios logueados por cada jugador humano
    private ArrayList<String> loggedInUsers = new ArrayList<>();

    // Variables de estado para selección de skins
    private int numHumans;
    private int numSeals;
    private int currentSkinPlayerIndex;
    private ArrayList<String> selectedSkins = new ArrayList<>();
    private ArrayList<modelo.jugador.Jugador> jugadoresTemp = new ArrayList<>();

    private final String[] SKIN_FILES = {"skin_dino.png", "skin_chef.png", "skin_ninja.png", "skin_ghost.png"};
    private int currentCarouselIdx = 0;

    // ══════════ Sistema de nieve ══════════
    private AnimationTimer snowTimer;
    private static final int MAX_SNOWFLAKES = 150;
    private double[] snowX, snowY, snowSpeed, snowSize, snowDrift;

    private ResourceBundle messages;

    @FXML
    private void initialize() {
        // Animación de entrada: Fade-In (Suavizado total)
        if (rootPane != null) {
            rootPane.setOpacity(0);
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.millis(800), rootPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }

        // Animación de entrada: Slide-Down (Cae del cielo)
        if (mainContent != null) {
            mainContent.setTranslateY(-900);
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(1400), mainContent);
            slideIn.setToY(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);
            slideIn.play();
        }
        
        if (gameTitleImage != null) {
            gameTitleImage.setTranslateY(-500);
            TranslateTransition titleSlide = new TranslateTransition(Duration.millis(1000), gameTitleImage);
            titleSlide.setDelay(Duration.millis(300)); // El título cae un poco después
            titleSlide.setToY(0);
            titleSlide.setInterpolator(Interpolator.EASE_OUT);
            titleSlide.play();
        }

        hideAllCards();
        showOptionsCard();

        try {
            if (conexionBBDD == null || conexionBBDD.isClosed() || !conexionBBDD.isValid(2)) {
                conexionBBDD = controlador.gestionbbdd.BBDD.conectarPredeterminado();
            }
        } catch (Exception e) {
            conexionBBDD = controlador.gestionbbdd.BBDD.conectarPredeterminado();
        }

        // Bind del fondo al tamaño del contenedor
        if (backgroundImage != null && rootPane != null) {
            backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
        }

        // Bind del canvas de nieve al tamaño del contenedor
        if (snowCanvas != null && rootPane != null) {
            snowCanvas.widthProperty().bind(rootPane.widthProperty());
            snowCanvas.heightProperty().bind(rootPane.heightProperty());
        }

        // Iniciar el efecto de nieve
        iniciarNieve();

        // Cargar idioma inicial
        updateUITexts();

        // Iniciar música de menú
        playMenuMusic();

        // Configurar slider de volumen
        if (volumeSlider != null) {
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (musicPlayer != null) {
                    musicPlayer.setVolume(newVal.doubleValue());
                    updateVolumeIcon(newVal.doubleValue());
                }
            });
        }
    }

    private void updateVolumeIcon(double volume) {
        if (volumeIconLabel == null) return;
        if (volume == 0) volumeIconLabel.setText("🔇");
        else if (volume < 0.3) volumeIconLabel.setText("🔈");
        else if (volume < 0.7) volumeIconLabel.setText("🔉");
        else volumeIconLabel.setText("🔊");
    }

    private void playMenuMusic() {
        try {
            if (musicPlayer != null) {
                musicPlayer.stop();
            }
            String path = getClass().getResource("/resources/audio/Ascent_to_the_White_Peak (1).mp3").toExternalForm();
            Media media = new Media(path);
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.5);
            musicPlayer.play();
        } catch (Exception e) {
            System.err.println("No se pudo cargar la música de menú: " + e.getMessage());
        }
    }

    private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    private void updateUITexts() {
        // Enlace sync con TranslationManager
        messages = utils.TranslationManager.getBundle();
        if (messages == null) return;
        
        Locale currentLocale = utils.TranslationManager.getLocale();

        // Menu Bar - Eliminado

        // Player Auth Card
        if (loginTitle != null) loginTitle.setText(messages.getString("card.login.title"));
        if (userLabel != null) userLabel.setText(messages.getString("card.login.user"));
        if (passLabel != null) passLabel.setText(messages.getString("card.login.pass"));
        if (loginButton != null) loginButton.setText(messages.getString("card.login.enter"));
        if (registerButton != null) registerButton.setText(messages.getString("card.login.register"));

        // Options Card
        if (optionsTitle != null) optionsTitle.setText(messages.getString("card.options.title"));
        if (createGameBtn != null) createGameBtn.setText("🎮  " + messages.getString("card.options.create"));
        if (loadSavedGameBtn != null) loadSavedGameBtn.setText("📂  " + messages.getString("card.options.load"));
        if (optionsHint != null) optionsHint.setText(messages.getString("card.options.hint"));

        // Config Card
        if (configTitle != null) configTitle.setText(messages.getString("card.config.title"));
        if (penguinsLabel != null) penguinsLabel.setText(messages.getString("card.config.penguins"));
        if (sealsLabel != null) sealsLabel.setText(messages.getString("card.config.seals"));
        if (configBackBtn != null) configBackBtn.setText(messages.getString("card.config.back"));
        if (configStartBtn != null) configStartBtn.setText(messages.getString("card.config.start"));
        if (configHint != null) configHint.setText(messages.getString("card.config.hint"));

        // Skin Selection Card
        if (skinTitle != null) skinTitle.setText(messages.getString("card.skin.title"));
        if (skinNameLabel != null) skinNameLabel.setText(messages.getString("card.skin.name"));
        if (skinConfirmBtn != null) skinConfirmBtn.setText(messages.getString("card.skin.confirm"));
        if (skinCancelBtn != null) skinCancelBtn.setText(messages.getString("card.skin.cancel"));

        // Load Game Card
        if (loadTitle != null) loadTitle.setText(messages.getString("card.load.title"));
        if (loadBackBtn != null) loadBackBtn.setText(messages.getString("card.config.back"));
    }

    // ══════════════ EFECTO DE NIEVE ══════════════

    private void iniciarNieve() {
        snowX = new double[MAX_SNOWFLAKES];
        snowY = new double[MAX_SNOWFLAKES];
        snowSpeed = new double[MAX_SNOWFLAKES];
        snowSize = new double[MAX_SNOWFLAKES];
        snowDrift = new double[MAX_SNOWFLAKES];

        double w = snowCanvas.getWidth() > 0 ? snowCanvas.getWidth() : 1920;
        double h = snowCanvas.getHeight() > 0 ? snowCanvas.getHeight() : 1080;

        for (int i = 0; i < MAX_SNOWFLAKES; i++) {
            snowX[i] = rand.nextDouble() * w;
            snowY[i] = rand.nextDouble() * h;
            snowSpeed[i] = 0.5 + rand.nextDouble() * 2.0;
            snowSize[i] = 1.5 + rand.nextDouble() * 3.5;
            snowDrift[i] = (rand.nextDouble() - 0.5) * 0.8;
        }

        snowTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                actualizarNieve();
            }
        };
        snowTimer.start();
    }

    private void actualizarNieve() {
        double w = snowCanvas.getWidth();
        double h = snowCanvas.getHeight();
        if (w <= 0 || h <= 0) return;

        GraphicsContext gc = snowCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        for (int i = 0; i < MAX_SNOWFLAKES; i++) {
            snowY[i] += snowSpeed[i];
            snowX[i] += snowDrift[i] + Math.sin(snowY[i] * 0.01 + i) * 0.3;

            // Reaparece arriba si sale por abajo
            if (snowY[i] > h) {
                snowY[i] = -snowSize[i];
                snowX[i] = rand.nextDouble() * w;
                snowSpeed[i] = 0.5 + rand.nextDouble() * 2.0;
                snowSize[i] = 1.5 + rand.nextDouble() * 3.5;
                snowDrift[i] = (rand.nextDouble() - 0.5) * 0.8;
            }
            if (snowX[i] < -10) snowX[i] = w + 5;
            if (snowX[i] > w + 10) snowX[i] = -5;

            // Dibujar copo con opacidad variable según tamaño
            double opacity = 0.3 + (snowSize[i] / 5.0) * 0.5;
            gc.setFill(Color.color(1, 1, 1, opacity));
            gc.fillOval(snowX[i], snowY[i], snowSize[i], snowSize[i]);
        }
    }

    // ══════════════ GESTIÓN DE CARDS ══════════════

    private void hideAllCards() {
        if (playerAuthCard != null) playerAuthCard.setVisible(false);
        if (optionsCard != null) optionsCard.setVisible(false);
        if (configCard != null) configCard.setVisible(false);
        if (loadGameCard != null) loadGameCard.setVisible(false);
        if (skinSelectionCard != null) skinSelectionCard.setVisible(false);
        if (audioCard != null) audioCard.setVisible(false);
        if (rankingCard != null) rankingCard.setVisible(false);
    }

    // ══════════════ AUTH POR JUGADOR ══════════════

    /** Muestra la card de auth configurada para el jugador actual o para cargar partida. */
    private void showPlayerAuthCard(AuthMode mode) {
        currentAuthMode = mode;
        hideAllCards();
        if (playerAuthCard != null) playerAuthCard.setVisible(true);
        if (userField != null) userField.clear();
        if (passField != null) passField.clear();
        if (feedbackLabel != null) feedbackLabel.setText("");

        if (mode == AuthMode.CREATE_PLAYER) {
            if (playerAuthInfoLabel != null)
                playerAuthInfoLabel.setText("Jugador " + currentSkinPlayerIndex + " — Inicia sesión o regístrate");
            if (userField != null) {
                userField.setEditable(true);
                userField.setPromptText("Usuario...");
            }
        } else if (mode == AuthMode.LOAD_PLAYER_AUTH) {
            String targetUser = humanPlayersToAuth.get(currentLoadPlayerIdx);
            if (playerAuthInfoLabel != null)
                playerAuthInfoLabel.setText("Identificación necesaria: " + targetUser);
            if (userField != null) {
                userField.setText(targetUser);
                userField.setEditable(false); 
            }
        } else {
            if (playerAuthInfoLabel != null)
                playerAuthInfoLabel.setText("Inicia sesión para ver tus partidas guardadas");
            if (userField != null) {
                userField.setEditable(true);
                userField.setPromptText("Usuario...");
            }
        }
    }

    @FXML
    private void handlePlayerLogin(ActionEvent event) {
        String username = userField.getText().trim();
        String password = passField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText(messages.getString("login.empty"));
            feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        // Verificar que el usuario no esté ya en la partida
        if (currentAuthMode == AuthMode.CREATE_PLAYER && loggedInUsers.contains(username)) {
            feedbackLabel.setText("Este usuario ya está en la partida. Usa otro.");
            feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        controlador.gestionbbdd.BBDD dbHelper = new controlador.gestionbbdd.BBDD();
        if (dbHelper.loginUsuario(conexionBBDD, username, password)) {
            feedbackLabel.setStyle("");
            onAuthSuccess(username);
        } else {
            feedbackLabel.setText(messages.getString("login.not_found"));
            feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    @FXML
    private void handlePlayerRegister() {
        String username = userField.getText().trim();
        String password = passField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            feedbackLabel.setText(messages.getString("register.empty"));
            feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        // Verificar que el usuario no esté ya en la partida
        if (currentAuthMode == AuthMode.CREATE_PLAYER && loggedInUsers.contains(username)) {
            feedbackLabel.setText("Este usuario ya está en la partida. Usa otro.");
            feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        controlador.gestionbbdd.BBDD dbHelper = new controlador.gestionbbdd.BBDD();
        if (dbHelper.existeUsuario(conexionBBDD, username)) {
            feedbackLabel.setText(messages.getString("register.exists"));
            feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
        } else {
            if (dbHelper.registrarUsuario(conexionBBDD, username, password)) {
                feedbackLabel.setText(messages.getString("register.success") + " ¡Ahora inicia sesión!");
                feedbackLabel.setStyle("-fx-text-fill: #4ade80;");
            } else {
                feedbackLabel.setText(messages.getString("register.error"));
                feedbackLabel.setStyle("-fx-text-fill: #ff6b6b;");
            }
        }
    }

    /** Llamado cuando el login fue exitoso, según el modo actual. */
    private void onAuthSuccess(String username) {
        if (currentAuthMode == AuthMode.LOAD_GAME) {
            usuarioLogueado = username;
            hideAllCards();
            loadGameCard.setVisible(true);
            refreshGamesList();
        } else if (currentAuthMode == AuthMode.LOAD_PLAYER_AUTH) {
            // Un jugador de la partida cargada se ha identificado
            currentLoadPlayerIdx++;
            if (currentLoadPlayerIdx < humanPlayersToAuth.size()) {
                // Siguiente jugador
                showPlayerAuthCard(AuthMode.LOAD_PLAYER_AUTH);
            } else {
                // Todos identificados, cargar de verdad
                ejecutarCargaFinal();
            }
        } else {
            // CREATE_PLAYER: guardar usuario y pasar a elegir skin
            loggedInUsers.add(username);
            if (loggedInUsers.size() == 1) usuarioLogueado = username; // primer jugador = usuario principal
            hideAllCards();
            skinSelectionCard.setVisible(true);
            actualizarEstadoSeleccionSkin();
            skinTitle.setText(messages.getString("card.skin.title") + " (" + currentSkinPlayerIndex + ")");
            skinErrorLabel.setText("");
        }
    }

    @FXML
    private void handleAuthBack() {
        if (currentAuthMode == AuthMode.LOAD_GAME) {
            showOptionsCard();
        } else if (currentAuthMode == AuthMode.LOAD_PLAYER_AUTH) {
            hideAllCards();
            loadGameCard.setVisible(true);
        } else {
            // Cancelar creación de partida: volver a configCard
            loggedInUsers.clear();
            jugadoresTemp.clear();
            selectedSkins.clear();
            hideAllCards();
            configCard.setVisible(true);
        }
    }

    @FXML
    private void handleLoadGame(ActionEvent event) {
        showPlayerAuthCard(AuthMode.LOAD_GAME);
    }

    private void refreshGamesList() {
        gamesListContainer.getChildren().clear();
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        ArrayList<LinkedHashMap<String, String>> partidas = helper.listarPartidas(conexionBBDD, usuarioLogueado);
        
        if (partidas == null || partidas.isEmpty()) {
            gamesListContainer.getChildren().add(new Label(messages.getString("load.no_games")));
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
                loadBtn.setOnAction(e -> cargarPartidaPorId(Integer.parseInt(id)));

                Button delBtn = new Button();
                javafx.scene.shape.SVGPath trashIcon = new javafx.scene.shape.SVGPath();
                trashIcon.setContent("M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z");
                trashIcon.setFill(javafx.scene.paint.Color.web("#ef4444"));
                
                delBtn.setGraphic(trashIcon);
                delBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-background-radius: 10; -fx-padding: 8; -fx-cursor: hand;");
                
                delBtn.setOnMouseEntered(e -> {
                    delBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.25); -fx-background-radius: 10; -fx-padding: 8; -fx-cursor: hand;");
                    trashIcon.setFill(javafx.scene.paint.Color.web("#f87171"));
                });
                delBtn.setOnMouseExited(e -> {
                    delBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-background-radius: 10; -fx-padding: 8; -fx-cursor: hand;");
                    trashIcon.setFill(javafx.scene.paint.Color.web("#ef4444"));
                });
                
                delBtn.setOnAction(e -> confirmarEliminacionPartida(Integer.parseInt(id), "Partida #" + id));
                
                itemRow.getChildren().addAll(loadBtn, delBtn);
                gamesListContainer.getChildren().add(itemRow);
            }
        }
    }

    private void confirmarEliminacionPartida(int idPartida, String nombrePartida) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres eliminar la partida \"" + nombrePartida + "\"?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
            if (helper.eliminarPartida(conexionBBDD, idPartida)) {
                refreshGamesList();
            }
        }
    }

    private void cargarPartidaPorId(int idPartida) {
        // En lugar de cargar directamente, identificamos jugadores humanos
        idPartidaPendienteCargar = idPartida;
        humanPlayersToAuth.clear();
        currentLoadPlayerIdx = 0;

        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        ArrayList<LinkedHashMap<String, String>> jugadores = helper.select(conexionBBDD, 
            "SELECT NOMBRE_JUGADOR FROM JUGADOR_ESTADO WHERE ID_PARTIDA = " + idPartida + " AND TIPO_JUGADOR = 'PINGUINO'");
        
        for (LinkedHashMap<String, String> j : jugadores) {
            String nombre = j.get("NOMBRE_JUGADOR");
            // El usuarioLogueado ya está identificado
            if (!nombre.equalsIgnoreCase(usuarioLogueado)) {
                humanPlayersToAuth.add(nombre);
            }
        }

        if (humanPlayersToAuth.isEmpty()) {
            ejecutarCargaFinal();
        } else {
            showPlayerAuthCard(AuthMode.LOAD_PLAYER_AUTH);
        }
    }

    private void ejecutarCargaFinal() {
        pararNieve();
        stopMusic();
        try {
            java.net.URL fxmlUrl = getClass().getResource("/resources/fxml/PantallaCarga.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            PantallaCarga controller = loader.getController();
            controller.setConexion(conexionBBDD);
            controller.setUsuario(usuarioLogueado);
            controller.setIdPartidaCargar(idPartidaPendienteCargar);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(root);

            controller.iniciarCarga();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bumpAnimation(Node node) {
        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(Duration.millis(100), node);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.3); st.setToY(1.3);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    @FXML
    private void incPlayers() {
        int players = Integer.parseInt(playerCountLabel.getText());
        int seals = Integer.parseInt(sealCountLabel.getText());
        if (players < 4 && (players + seals < 6)) { // Max 4 pinguinos, Max 6 total
            playerCountLabel.setText(String.valueOf(players + 1));
            errorLabel.setText("");
            bumpAnimation(playerCountLabel);
        } else if (players >= 4) {
            errorLabel.setText("Máximo 4 pingüinos");
        } else {
            errorLabel.setText(messages.getString("error.total_max"));
        }
    }

    @FXML
    private void decPlayers() {
        int val = Integer.parseInt(playerCountLabel.getText());
        if (val > 1) {
            playerCountLabel.setText(String.valueOf(val - 1));
            errorLabel.setText("");
            bumpAnimation(playerCountLabel);
        }
    }

    @FXML
    private void incSeals() {
        int players = Integer.parseInt(playerCountLabel.getText());
        int seals = Integer.parseInt(sealCountLabel.getText());
        if (players + seals < 6) { // Max 6 total
            sealCountLabel.setText(String.valueOf(seals + 1));
            errorLabel.setText("");
            bumpAnimation(sealCountLabel);
        } else {
            errorLabel.setText(messages.getString("error.total_max"));
        }
    }

    @FXML
    private void decSeals() {
        int val = Integer.parseInt(sealCountLabel.getText());
        if (val > 0) {
            sealCountLabel.setText(String.valueOf(val - 1));
            errorLabel.setText("");
            bumpAnimation(sealCountLabel);
        }
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        try {
            numHumans = Integer.parseInt(playerCountLabel.getText());
            numSeals = Integer.parseInt(sealCountLabel.getText());

            if (numHumans < 1) {
                errorLabel.setText(messages.getString("error.min_pingu"));
                return;
            }
            if (numSeals < 1) {
                errorLabel.setText(messages.getString("error.min_foca"));
                return;
            }

            // Iniciar proceso de auth + selección de skins
            currentSkinPlayerIndex = 1;
            selectedSkins.clear();
            jugadoresTemp.clear();
            loggedInUsers.clear();
            currentCarouselIdx = 0;
            skinPreview.setImage(new javafx.scene.image.Image("/resources/images/skins/" + SKIN_FILES[0]));

            // Mostrar auth para el primer jugador
            showPlayerAuthCard(AuthMode.CREATE_PLAYER);

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
            skinPreview.setImage(new javafx.scene.image.Image("/resources/images/skins/" + SKIN_FILES[currentCarouselIdx]));
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
        processSkinSelection(SKIN_FILES[currentCarouselIdx]);
    }

    private void actualizarEstadoSeleccionSkin() {
        if (selectedSkins.contains(SKIN_FILES[currentCarouselIdx])) {
            skinErrorLabel.setText(messages.getString("error.skin_in_use"));
            skinPreview.setOpacity(0.5);
        } else {
            skinErrorLabel.setText("");
            skinPreview.setOpacity(1.0);
        }
    }

    private void processSkinSelection(String skinFile) {
        if (selectedSkins.contains(skinFile)) {
            skinErrorLabel.setText(messages.getString("error.skin_in_use"));
            return;
        }

        // El nombre es el username con el que se logueó
        String playerName = loggedInUsers.get(currentSkinPlayerIndex - 1);
        selectedSkins.add(skinFile);
        
        String color = (currentSkinPlayerIndex == 1) ? "Azul" : (currentSkinPlayerIndex == 2) ? "Naranja" : (currentSkinPlayerIndex == 3) ? "Verde" : "Amarillo";
        modelo.jugador.Pinguino p = new modelo.jugador.Pinguino(playerName, color);
        p.setSkin(skinFile);
        p.getInventario().añadirItem(new modelo.items.Dado());
        p.setEsIA(false);
        jugadoresTemp.add(p);
        
        if (currentSkinPlayerIndex < numHumans) {
            currentSkinPlayerIndex++;
            currentCarouselIdx = 0;
            skinPreview.setImage(new javafx.scene.image.Image("/resources/images/skins/" + SKIN_FILES[0]));
            // Pedir login al siguiente jugador
            showPlayerAuthCard(AuthMode.CREATE_PLAYER);
        } else {
            // Todos los humanos han elegido, añadir focas y empezar
            ArrayList<String> focaSkins = new ArrayList<>();
            focaSkins.add("foca_skin1.png");
            focaSkins.add("foca_skin2.png");
            focaSkins.add("foca_skin3.png");
            java.util.Collections.shuffle(focaSkins);

            for (int i = 1; i <= numSeals; i++) {
                modelo.jugador.Foca foca = new modelo.jugador.Foca("Foca " + i, "Blanco");
                foca.setEsIA(true);
                String skinUnica = focaSkins.get((i - 1) % focaSkins.size());
                foca.setSkin(skinUnica);
                jugadoresTemp.add(foca);
            }

            modelo.tablero.Tablero tablero = new modelo.tablero.Tablero();
            tablero.generarTableroAleatorio();
            
            // Incrementar estadísticas de partidas jugadas para todos los participantes humanos
            controlador.gestionbbdd.BBDD dbHelper = new controlador.gestionbbdd.BBDD();
            for (String user : loggedInUsers) {
                dbHelper.sumarPartidaJugada(conexionBBDD, user);
            }
            
            modelo.partida.Partida nuevaPartida = new modelo.partida.Partida(tablero, jugadoresTemp);
            cambiarAPantallaJuego(nuevaPartida);
        }
    }

    private void cambiarAPantallaJuego(modelo.partida.Partida partida) {
        pararNieve();
        stopMusic();
        try {
            java.net.URL fxmlUrl = getClass().getResource("/resources/fxml/PantallaCarga.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            PantallaCarga controller = loader.getController();
            controller.setConexion(conexionBBDD);
            controller.setUsuario(usuarioLogueado);
            controller.setPartidaNueva(partida);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(root);

            controller.iniciarCarga();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Detiene la animación de nieve al salir de esta pantalla */
    private void pararNieve() {
        if (snowTimer != null) {
            snowTimer.stop();
            snowTimer = null;
        }
    }

    @FXML private void handleLogout() {
        // No se usa en el flujo principal, pero se mantiene por compatibilidad
        usuarioLogueado = null;
        loggedInUsers.clear();
        showOptionsCard();
    }

    @FXML public void showOptionsCard() { hideAllCards(); optionsCard.setVisible(true); }
    @FXML public void showConfigCard() { hideAllCards(); configCard.setVisible(true); }
    @FXML public void showAudioCard() { hideAllCards(); audioCard.setVisible(true); }

    @FXML
    public void showRankingCard() {
        hideAllCards();
        rankingCard.setVisible(true);
        refreshRankings();
    }

    private void refreshRankings() {
        rankingParticipacionContainer.getChildren().clear();
        rankingVictoriasContainer.getChildren().clear();
        
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        
        // Ranking de Participación (Jugadas)
        ArrayList<LinkedHashMap<String, String>> rankingP = helper.obtenerRankingParticipacion(conexionBBDD);
        if (rankingP == null || rankingP.isEmpty()) {
            Label empty = new Label("Sin datos");
            empty.setStyle("-fx-text-fill: #94a3b8;");
            rankingParticipacionContainer.getChildren().add(empty);
        } else {
            int pos = 1;
            for (LinkedHashMap<String, String> fila : rankingP) {
                Label lbl = new Label(pos + "º " + fila.get("USERNAME") + " - " + fila.get("PARTIDAS_JUGADAS"));
                lbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 14px; -fx-padding: 2;");
                rankingParticipacionContainer.getChildren().add(lbl);
                pos++;
            }
        }

        // Ranking de Victorias (Ganadas)
        ArrayList<LinkedHashMap<String, String>> rankingV = helper.obtenerRankingVictorias(conexionBBDD);
        if (rankingV == null || rankingV.isEmpty()) {
            Label empty = new Label("Sin datos");
            empty.setStyle("-fx-text-fill: #94a3b8;");
            rankingVictoriasContainer.getChildren().add(empty);
        } else {
            int pos = 1;
            for (LinkedHashMap<String, String> fila : rankingV) {
                Label lbl = new Label(pos + "º " + fila.get("USERNAME") + " - " + fila.get("PARTIDAS_GANADAS"));
                lbl.setStyle("-fx-text-fill: #fef08a; -fx-font-size: 14px; -fx-padding: 2; -fx-font-weight: bold;");
                rankingVictoriasContainer.getChildren().add(lbl);
                pos++;
            }
        }
    }

    @FXML private void handleMute() {
        if (volumeSlider != null) {
            if (volumeSlider.getValue() > 0) {
                volumeSlider.setValue(0);
                muteButton.setText("🔊 Activar Sonido");
            } else {
                volumeSlider.setValue(0.5);
                muteButton.setText("🔇 Silenciar");
            }
        }
    }

    @FXML private void handleQuitGame() { System.exit(0); }
}