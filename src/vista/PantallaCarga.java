package vista;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Pantalla de carga animada que se muestra antes de entrar al tablero de juego.
 * Dura ~5 segundos con una barra de progreso y mensajes rotativos temáticos.
 */
public class PantallaCarga {

    @FXML private Label loadingTitle;
    @FXML private Label messageLabel;
    @FXML private Label percentLabel;
    @FXML private StackPane progressTrack;
    @FXML private Region progressFill;
    @FXML private StackPane rootPane;
    @FXML private javafx.scene.canvas.Canvas snowCanvas;

    // Datos que se pasan desde PantallaMenu
    private modelo.partida.Partida partidaPendiente;
    private int idPartidaCargar = -1;
    private Connection conexionBBDD;
    private String usuarioLogueado;

    // ══════════ Sistema de nieve ══════════
    private AnimationTimer snowTimer;
    private static final int MAX_SNOWFLAKES = 60;
    private double[] snowX, snowY, snowSpeed, snowSize, snowDrift;
    private Random rand = new Random();

    private static final double DURACION_TOTAL_MS = 5000;
    private static final double INTERVALO_MS = 50; // actualizar cada 50ms = suave

    private final String[] MENSAJES = {
        "Los pingüinos se están preparando para darlo todo en el tablero... 🐧",
        "Las focas ultiman su estrategia secreta... 🦭",
        "El hielo se está formando bajo tus pies... ❄️",
        "¡Encuentra los peces antes que nadie! 🐟",
        "Los dados de hielo se están enfriando... 🎲",
        "La aurora boreal ilumina el camino... 🌌",
        "¡Los trineos están siendo pulidos! 🛷",
        "Preparando paisajes nevados con mucho cariño... ⛰️",
        "Los osos polares se despiertan de su siesta... 🐻‍❄️",
        "¡Casi listo! Los pingüinos ya calientan aletas... 🔥"
    };

    private int mensajeIndex = 0;

    @FXML
    private void initialize() {
        progressFill.setPrefWidth(0);
        progressFill.setMaxWidth(0);

        if (snowCanvas != null && rootPane != null) {
            snowCanvas.widthProperty().bind(rootPane.widthProperty());
            snowCanvas.heightProperty().bind(rootPane.heightProperty());
        }

        iniciarNieve();
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
            snowSpeed[i] = 0.3 + rand.nextDouble() * 1.5;
            snowSize[i] = 10 + rand.nextDouble() * 25; // Copos mucho más grandes
            snowDrift[i] = (rand.nextDouble() - 0.5) * 0.5;
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

            if (snowY[i] > h) {
                snowY[i] = -snowSize[i];
                snowX[i] = rand.nextDouble() * w;
                snowSpeed[i] = 0.5 + rand.nextDouble() * 2.0;
                snowSize[i] = 1.5 + rand.nextDouble() * 3.5;
                snowDrift[i] = (rand.nextDouble() - 0.5) * 0.8;
            }
            if (snowX[i] < -10) snowX[i] = w + 5;
            if (snowX[i] > w + 10) snowX[i] = -5;

            double opacity = 0.15 + (snowSize[i] / 30.0) * 0.4;
            gc.setFill(Color.color(1, 1, 1, opacity));
            // Dibujar copos con un poco de desenfoque visual (círculos rellenos suaves)
            gc.fillOval(snowX[i], snowY[i], snowSize[i], snowSize[i]);
        }
    }

    // ══════════════ Setters para datos de partida ══════════════

    public void setConexion(Connection con) {
        this.conexionBBDD = con;
    }

    public void setUsuario(String user) {
        this.usuarioLogueado = user;
    }

    /** Para partidas nuevas: recibe la partida ya configurada */
    public void setPartidaNueva(modelo.partida.Partida partida) {
        this.partidaPendiente = partida;
    }

    /** Para partidas guardadas: recibe el ID a cargar */
    public void setIdPartidaCargar(int id) {
        this.idPartidaCargar = id;
    }

    // ══════════════ Iniciar animación de carga ══════════════

    public void iniciarCarga() {
        final double pasos = DURACION_TOTAL_MS / INTERVALO_MS;
        final double incrementoPorPaso = 1.0 / pasos;
        final double[] progreso = {0.0};

        // Cambiar mensaje cada ~1.2 segundos
        Timeline mensajesTimeline = new Timeline(
            new KeyFrame(Duration.millis(1200), e -> {
                mensajeIndex = (mensajeIndex + 1) % MENSAJES.length;
                messageLabel.setText(MENSAJES[mensajeIndex]);
            })
        );
        mensajesTimeline.setCycleCount((int)(DURACION_TOTAL_MS / 1200));
        mensajesTimeline.play();

        // Animar barra de progreso
        Timeline barraTimeline = new Timeline(
            new KeyFrame(Duration.millis(INTERVALO_MS), e -> {
                progreso[0] = Math.min(progreso[0] + incrementoPorPaso, 1.0);
                double trackWidth = progressTrack.getWidth() - 4; // margen interior
                double fillWidth = trackWidth * progreso[0];
                progressFill.setPrefWidth(fillWidth);
                progressFill.setMaxWidth(fillWidth);
                percentLabel.setText((int)(progreso[0] * 100) + "%");
            })
        );
        barraTimeline.setCycleCount((int) pasos);
        barraTimeline.setOnFinished(e -> {
            percentLabel.setText("100%");
            messageLabel.setText("¡Todo listo! Entrando al tablero... 🎮");
            mensajesTimeline.stop();
            if (snowTimer != null) snowTimer.stop();

            // Pequeña pausa antes de cargar el juego
            Timeline pausa = new Timeline(new KeyFrame(Duration.millis(600), ev -> {
                cargarPantallaJuego();
            }));
            pausa.play();
        });
        barraTimeline.play();
    }

    // ══════════════ Transición al juego ══════════════

    private void cargarPantallaJuego() {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/resources/fxml/PantallaJuego.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            PantallaJuego controller = loader.getController();
            controller.setConexion(conexionBBDD);
            controller.setUsuario(usuarioLogueado);

            if (partidaPendiente != null) {
                controller.prepararPartidaPersonalizada(partidaPendiente);
            } else if (idPartidaCargar > 0) {
                controller.cargarPartidaEspecifica(idPartidaCargar);
            }

            Stage stage = (Stage) loadingTitle.getScene().getWindow();
            Scene scene = stage.getScene();
            
            // Usar setRoot en lugar de setScene para evitar parpadeos y que desaparezca el modo pantalla completa
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
