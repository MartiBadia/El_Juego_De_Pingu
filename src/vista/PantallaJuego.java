package vista;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import controlador.gestor.GestorPartida;
import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.items.Inventario;
import modelo.tablero.Casilla;

public class PantallaJuego {

    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private Button dado;
    @FXML private Button rapido;
    @FXML private Button lento;
    @FXML private Button peces;
    @FXML private Button nieve;

    @FXML private Text dadoResultText;
    @FXML private Text rapido_t;
    @FXML private Text lento_t;
    @FXML private Text peces_t;
    @FXML private Text nieve_t;
    @FXML private Text eventos;

    @FXML private GridPane tablero;
    @FXML private ImageView P1, P2, P3, P4;
    @FXML private ImageView PFoca, PFoca2, PFoca3, PFoca4;
    @FXML private javafx.scene.control.Label turnLabel;

    @FXML private Button hamburgerButton;
    @FXML private VBox menuOverlay;

    private GestorPartida gestorPartida;
    private String usuarioLogueado;
    private static final int COLUMNS = 5;
    
    private ArrayList<ImageView> fichasPinguinos;
    private ArrayList<ImageView> fichasFocas;

    @FXML
    private void initialize() {
        eventos.setText("Bienvenido a El Juego de Pingu.");
        
        fichasPinguinos = new ArrayList<>();
        fichasPinguinos.add(P1); fichasPinguinos.add(P2);
        fichasPinguinos.add(P3); fichasPinguinos.add(P4);

        fichasFocas = new ArrayList<>();
        fichasFocas.add(PFoca); fichasFocas.add(PFoca2);
        fichasFocas.add(PFoca3); fichasFocas.add(PFoca4);

        for(ImageView iv : fichasPinguinos) iv.setVisible(false);
        for(ImageView iv : fichasFocas) iv.setVisible(false);

        gestorPartida = new GestorPartida();
    }

    public void setConexion(java.sql.Connection con) {
        // La conexión se usa internamente en el gestor a través de BBDD si es necesario
    }

    public void setUsuario(String user) {
        this.usuarioLogueado = user;
    }

    public void prepararPartidaPersonalizada(modelo.partida.Partida p) {
        gestorPartida.setPartida(p);
        construirTableroVisual();
        actualizarPosicionesVisuales();
        actualizarInventarioVisual();
        actualizarLabelTurno();
    }

    public void cargarPartidaEspecifica(int idPartida) {
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        java.sql.Connection con = controlador.gestionbbdd.BBDD.conectarPredeterminado();
        modelo.partida.Partida p = helper.cargarBBDD(con, idPartida);
        if (p != null) {
            prepararPartidaPersonalizada(p);
        }
    }

    private void construirTableroVisual() {
        tablero.getChildren().removeIf(node -> 
            !(node instanceof ImageView && (node.getId() != null && node.getId().startsWith("P"))) && 
            !(node instanceof Text && ((Text)node).getStyleClass().contains("cell-title"))
        );

        ArrayList<Casilla> casillas = gestorPartida.getPartida().getTablero().getCasillas();
        for (int i = 1; i < 49; i++) {
            StackPane cell = new StackPane();
            cell.getStyleClass().add("board-cell");
            
            String tipo = "Normal";
            for (Casilla c : casillas) {
                if (c.getPosicion() == i) {
                    tipo = c.getClass().getSimpleName();
                    break;
                }
            }

            String imgFile;
            switch (tipo) {
                case "MotoNieve":       imgFile = "casilla_motonieve.png"; break;
                case "Oso":             imgFile = "casilla_oso.png";       break;
                case "Agujero":         imgFile = "casilla_agujero.png";   break;
                case "SueloQuebradizo": imgFile = "casilla_agujero.png";   break;
                default:                imgFile = "casilla_normal.png";    break;
            }

            ImageView iv = crearImagenCasilla(imgFile);
            if (iv != null) cell.getChildren().add(iv);
            else {
                Text t = new Text(tipo);
                t.getStyleClass().add("cell-type");
                cell.getChildren().add(t);
            }

            int row = i / COLUMNS;
            int col = i % COLUMNS;
            GridPane.setRowIndex(cell, row);
            GridPane.setColumnIndex(cell, col);
            tablero.getChildren().add(cell);
        }
        
        // Colocar skins a las fichas
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        int pingIdx = 0;
        int focaIdx = 0;
        for (Jugador j : jugadores) {
            if (j instanceof Pinguino) {
                fichasPinguinos.get(pingIdx).setImage(new Image("/resources/skins/" + j.getSkin()));
                fichasPinguinos.get(pingIdx).setVisible(true);
                pingIdx++;
            } else {
                fichasFocas.get(focaIdx).setImage(new Image("/resources/skins/foca.png"));
                fichasFocas.get(focaIdx).setVisible(true);
                focaIdx++;
            }
        }

        for(ImageView iv : fichasPinguinos) { iv.toFront(); iv.setMouseTransparent(true); }
        for(ImageView iv : fichasFocas) { iv.toFront(); iv.setMouseTransparent(true); }
    }

    private ImageView crearImagenCasilla(String fileName) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/resources/" + fileName));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(55);
            iv.setFitHeight(55);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            return null;
        }
    }

    private void actualizarPosicionesVisuales() {
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        int pIdx = 0; int fIdx = 0;
        for (Jugador j : jugadores) {
            ImageView ficha = (j instanceof Pinguino) ? fichasPinguinos.get(pIdx++) : fichasFocas.get(fIdx++);
            GridPane.setColumnIndex(ficha, j.getPosicion() % COLUMNS);
            GridPane.setRowIndex(ficha, j.getPosicion() / COLUMNS);
            ficha.toFront();
        }
    }

    private void actualizarInventarioVisual() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        if (actual instanceof Pinguino) {
            modelo.items.Inventario inv = ((Pinguino) actual).getInventario();
            rapido_t.setText("D. Rápido: " + inv.contarPorTipo("Dado Rapido"));
            lento_t.setText("D. Lento: " + inv.contarPorTipo("Dado Lento"));
            peces_t.setText("Peces: " + inv.contarPorTipo("Pez"));
            nieve_t.setText("Bolas: " + inv.contarPorTipo("Bola de Nieve"));
        }
    }

    private void actualizarLabelTurno() {
        if (gestorPartida.getPartida().isFinalizada()) {
            turnLabel.setText("PARTIDA FINALIZADA");
        } else {
            turnLabel.setText("▶ Turno de: " + gestorPartida.getPartida().getJugadorActual().getNombre());
        }
    }

    @FXML
    public void handleDado() {
        if (gestorPartida.getPartida().isFinalizada()) return;
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        if (actual instanceof modelo.jugador.Foca) return;
        
        int res = gestorPartida.tirarDado(actual);
        dadoResultText.setText("Ha salido: " + res);
        eventos.setText("⏳ " + actual.getNombre() + " avanza " + res + " casillas...");
        
        animarPasoAPaso(actual, actual.getPosicion(), res, () -> {
            String log = gestorPartida.procesarTurnoConAvance(actual, res);
            eventos.setText(log.trim());
            concluirTurno();
        });
    }

    private void animarPasoAPaso(Jugador j, int posInicial, int pasos, Runnable onFinish) {
        if (pasos <= 0) { onFinish.run(); return; }
        TranslateTransition tt = new TranslateTransition(Duration.millis(300));
        tt.setOnFinished(e -> animarPasoAPaso(j, posInicial + 1, pasos - 1, onFinish));
        tt.play();
        j.setPosicion(posInicial + 1);
        actualizarPosicionesVisuales();
    }

    private void concluirTurno() {
        actualizarPosicionesVisuales();
        actualizarInventarioVisual();
        if (gestorPartida.getPartida().isFinalizada()) {
            mostrarFinDePartida();
        } else {
            gestorPartida.avanzarTurno();
            actualizarLabelTurno();
            if (gestorPartida.getPartida().getJugadorActual() instanceof modelo.jugador.Foca) {
                jugarTurnoFoca();
            }
        }
    }

    private void jugarTurnoFoca() {
        modelo.jugador.Foca foca = (modelo.jugador.Foca) gestorPartida.getPartida().getJugadorActual();
        eventos.setText("🦭 Turno de la Foca " + foca.getNombre() + "...");
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override public void run() {
                javafx.application.Platform.runLater(() -> {
                    String log = gestorPartida.procesarTurnoFoca(foca);
                    eventos.setText(log);
                    concluirTurno();
                });
            }
        }, 1500);
    }

    private void mostrarFinDePartida() {
        Jugador ganador = gestorPartida.getPartida().getGanador();
        String nombre = (ganador != null) ? ganador.getNombre() : "Desconocido";
        eventos.setText("El ganador es " + nombre);
        turnLabel.setText("PARTIDA FINALIZADA");
        dado.setDisable(true);
    }

    @FXML private void handleSaveGame() {
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        helper.guardarBBDD(controlador.gestionbbdd.BBDD.conectarPredeterminado(), gestorPartida.getPartida(), usuarioLogueado);
        eventos.setText("✅ Partida guardada correctamente.");
    }

    @FXML private void handleLoadGame() { toggleMenu(); handleGoToMenu(); }
    @FXML private void handleNewGame() { toggleMenu(); handleGoToMenu(); }
    @FXML private void handleQuitGame() { System.exit(0); }
    @FXML private void handleGoToMenu() {
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/resources/PantallaMenu.fxml"));
            Scene s = new Scene(l.load());
            Stage st = (Stage) tablero.getScene().getWindow();
            st.setScene(s); st.setFullScreen(true); st.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void toggleMenu() {
        menuOverlay.setVisible(!menuOverlay.isVisible());
        menuOverlay.setManaged(menuOverlay.isVisible());
    }
    
    // Stubs para botones no implementados totalmente en el snippet
    @FXML private void handleRapido() {}
    @FXML private void handleLento() {}
    @FXML private void handlePeces() {}
    @FXML private void handleNieve() {}
}