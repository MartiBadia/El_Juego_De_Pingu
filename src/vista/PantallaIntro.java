package vista;

import java.io.File;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaIntro {

    @FXML private StackPane rootPane;
    @FXML private StackPane videoContainer;
    @FXML private VBox promptContainer;
    @FXML private MediaView mediaView;
    @FXML private ImageView fallbackImage;

    private MediaPlayer mediaPlayer;
    private boolean videoFinished = false;

    @FXML
    public void initialize() {
        // Hacer la imagen responsiva
        fallbackImage.fitWidthProperty().bind(rootPane.widthProperty());
        fallbackImage.fitHeightProperty().bind(rootPane.heightProperty());
        
        // Listener de escena: cargamos el video solo cuando el rootPane esté en escena
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Ajustar el MediaView al tamaño de la pantalla
                mediaView.fitWidthProperty().bind(rootPane.widthProperty());
                mediaView.fitHeightProperty().bind(rootPane.heightProperty());
                
                cargarVideo();

                // Listener de teclado
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.SPACE) {
                        if (videoFinished || mediaPlayer == null) {
                            goToMenu();
                        } else {
                            skipVideo();
                        }
                    }
                });
            }
        });
    }

    private void cargarVideo() {
        try {
            String videoPath = null;
            java.net.URL resource = getClass().getResource("/resources/video/pingu_intro.mp4");
            
            if (resource != null) {
                videoPath = resource.toExternalForm();
            } else {
                File f = new File("src/resources/video/pingu_intro.mp4");
                if (!f.exists()) f = new File("resources/video/pingu_intro.mp4");
                if (f.exists()) videoPath = f.toURI().toString();
            }

            if (videoPath != null) {
                // Normalización de URI
                if (videoPath.startsWith("file:/") && !videoPath.startsWith("file:///")) {
                    videoPath = videoPath.replaceFirst("file:/", "file:///");
                }
                videoPath = videoPath.replace(" ", "%20");
                
                System.out.println("[INFO] Intentando cargar Media: " + videoPath);
                
                Media media = new Media(videoPath);
                media.setOnError(() -> System.err.println("[ERROR] Error en el objeto Media: " + media.getError().getMessage()));
                
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);

                // Configurar eventos
                mediaPlayer.setOnError(() -> {
                    System.err.println("[ERROR] Error en MediaPlayer: " + mediaPlayer.getError().getMessage());
                    showStartPrompt();
                });

                mediaPlayer.setOnEndOfMedia(this::showStartPrompt);
                
                // IMPORTANTE: Esperar a que el player esté listo antes de llamar a play
                mediaPlayer.setOnReady(() -> {
                    System.out.println("[INFO] MediaPlayer listo. Iniciando reproducción.");
                    mediaPlayer.play();
                });

            } else {
                System.err.println("[ERROR] No se encontró el video.");
                showStartPrompt();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al inicializar carga: " + e.getMessage());
            e.printStackTrace();
            showStartPrompt();
        }
    }

    private void playerEvents(MediaPlayer player) {
        // Redundante con los listeners configurados arriba, pero mantenemos por compatibilidad si es necesario
    }

    private void skipVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        showStartPrompt();
    }

    private void showStartPrompt() {
        if (videoFinished) return;
        videoFinished = true;

        // Transición fluida: desvanecer video y aparecer fondo
        FadeTransition hideVideo = new FadeTransition(Duration.millis(1000), videoContainer);
        hideVideo.setFromValue(1.0);
        hideVideo.setToValue(0.0);
        
        FadeTransition showBack = new FadeTransition(Duration.millis(1000), fallbackImage);
        showBack.setFromValue(0.0);
        showBack.setToValue(1.0);

        hideVideo.setOnFinished(e -> {
            videoContainer.setVisible(false);
            if (promptContainer != null) {
                promptContainer.setVisible(true);
                // Animación de parpadeo del mensaje
                FadeTransition ft = new FadeTransition(Duration.millis(1000), promptContainer);
                ft.setFromValue(1.0);
                ft.setToValue(0.4);
                ft.setCycleCount(Timeline.INDEFINITE);
                ft.setAutoReverse(true);
                ft.play();
            }
        });

        hideVideo.play();
        showBack.play();
    }

    private void goToMenu() {
        if (mediaPlayer != null) mediaPlayer.stop();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/PantallaMenu.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = stage.getScene();
            
            // Cambiamos el contenido de la escena actual en lugar de crear una nueva
            // para evitar el parpadeo del modo pantalla completa
            scene.setRoot(root);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
