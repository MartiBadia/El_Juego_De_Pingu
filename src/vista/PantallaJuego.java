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
import javafx.scene.control.ScrollPane;
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
    @FXML private javafx.scene.control.Label inventoryTitle;
    @FXML private ImageView currentTurnSkin;

    @FXML private Button hamburgerButton;
    @FXML private VBox menuOverlay;
    @FXML private ScrollPane eventosScroll;

    private GestorPartida gestorPartida;
    private String usuarioLogueado;
    private static final int COLUMNS = 5;
    
    private ArrayList<ImageView> fichasPinguinos;
    private ArrayList<ImageView> fichasFocas;

    @FXML
    private void initialize() {
        appendLog("🐧 ¡Bienvenido a El Juego de Pingu!");
        
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

    /** Añade un mensaje al log acumulativo y hace scroll al final automáticamente. */
    private void appendLog(String msg) {
        String current = eventos.getText();
        if (current == null || current.isEmpty()) {
            eventos.setText(msg);
        } else {
            eventos.setText(current + "\n" + msg);
        }
        // Scroll automático al final para ver el último mensaje
        if (eventosScroll != null) {
            javafx.application.Platform.runLater(() -> eventosScroll.setVvalue(1.0));
        }
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
        int maxPos = gestorPartida.getPartida().getTablero().getTamaño();

        for (int i = 0; i < maxPos; i++) {
            StackPane cell = new StackPane();
            cell.getStyleClass().add("board-cell");
            
            String tipo = "Normal";
            if (i == 0) tipo = "Inicio";
            else if (i == 49) tipo = "Final";
            else {
                for (Casilla c : casillas) {
                    if (c.getPosicion() == i) {
                        tipo = c.getClass().getSimpleName();
                        break;
                    }
                }
            }

            String imgFile = "casilla_normal.png";
            String labelText = "";
            
            switch (tipo) {
                case "Inicio":
                    labelText = "SALIDA";
                    break;
                case "Final":
                    labelText = "META";
                    break;
                case "MotoNieve":
                    imgFile = "casilla_motonieve.png";
                    break;
                case "Oso":
                    imgFile = "casilla_oso.png";
                    break;
                case "Agujero":
                    imgFile = "casilla_agujero.png";
                    break;
                case "SueloQuebradizo":
                    imgFile = "casilla_agujero.png"; // Usamos la misma de agujero para suelo
                    break;
                case "Trineo":
                    imgFile = "casilla_trineo.png";
                    break;
                case "Evento":
                    imgFile = "casilla_evento.png";
                    break;
                default:
                    imgFile = "casilla_normal.png";
                    break;
            }

            ImageView iv = crearImagenCasilla(imgFile);
            if (iv != null) cell.getChildren().add(iv);

            if (!labelText.isEmpty()) {
                Text t = new Text(labelText);
                if (tipo.equals("Inicio")) t.getStyleClass().add("start-title");
                else if (tipo.equals("Final")) t.getStyleClass().add("finish-title");
                else t.getStyleClass().add("cell-title");
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
                fichasFocas.get(focaIdx).setImage(new Image("/resources/skins/" + j.getSkin()));
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
            iv.setFitWidth(45);
            iv.setFitHeight(45);
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
            // Al refrescar todas, ponemos traslación a 0 por si había alguna animación
            ficha.setTranslateX(0);
            ficha.setTranslateY(0);
            ficha.toFront();
        }
    }

    private ImageView getFichaDeJugador(Jugador j) {
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        int pIdx = 0; int fIdx = 0;
        for (Jugador item : jugadores) {
            if (item == j) {
                return (j instanceof Pinguino) ? fichasPinguinos.get(pIdx) : fichasFocas.get(fIdx);
            }
            if (item instanceof Pinguino) pIdx++; else fIdx++;
        }
        return null;
    }

    private void actualizarInventarioVisual() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        inventoryTitle.setText("🎒  Mochila de " + actual.getNombre());
        if (actual instanceof Pinguino) {
            modelo.items.Inventario inv = ((Pinguino) actual).getInventario();
            rapido_t.setText("D. Rápido: " + inv.contarPorTipo("Dado Rapido"));
            lento_t.setText("D. Lento: " + inv.contarPorTipo("Dado Lento"));
            peces_t.setText("Peces: " + inv.contarPorTipo("Pez"));
            nieve_t.setText("Bolas: " + inv.contarPorTipo("Bola de Nieve"));
        } else {
            // Es una Foca: no mostrar inventario
            inventoryTitle.setText("🦭  Turno de la IA");
            rapido_t.setText("");
            lento_t.setText("");
            peces_t.setText("");
            nieve_t.setText("");
        }
    }

    private void actualizarLabelTurno() {
        if (gestorPartida.getPartida().isFinalizada()) {
            turnLabel.setText("PARTIDA FINALIZADA");
            currentTurnSkin.setVisible(false);
        } else {
            Jugador jActual = gestorPartida.getPartida().getJugadorActual();
            turnLabel.setText("▶ Turno de: " + jActual.getNombre());
            currentTurnSkin.setImage(new Image("/resources/skins/" + jActual.getSkin()));
            currentTurnSkin.setVisible(true);
        }
    }

    @FXML
    public void handleDado() {
        if (gestorPartida.getPartida().isFinalizada()) return;
        Jugador j = gestorPartida.getPartida().getJugadorActual();
        if (j instanceof modelo.jugador.Foca) return;

        int res = gestorPartida.tirarDado(j);
        dadoResultText.setText("Ha salido: " + res);
        
        iniciarMovimientoAnimado(j, res);
    }

    private void iniciarMovimientoAnimado(Jugador j, int avance) {
        int posFisicaInicio = j.getPosicion();
        appendLog("⏳ " + j.getNombre() + " avanza " + avance + " casillas...");
        
        animarPasoAPaso(j, posFisicaInicio, avance, () -> {
            j.setPosicion(posFisicaInicio);
            
            // Calculamos la posición destino esperada
            int targetPos = Math.min(posFisicaInicio + avance, gestorPartida.getPartida().getTablero().getTamaño() - 1);
            
            // Gestión del soborno por UI: preguntamos al jugador involucrado
            comprobarSobornoEnCasilla(j, targetPos);
            
            String log = gestorPartida.procesarTurnoConAvance(j, avance);
            appendLog(log.trim());
            concluirTurno();
        });
    }

    private void comprobarSobornoEnCasilla(Jugador j, int targetPos) {
        Pinguino pTarget = null;
        modelo.jugador.Foca fTarget = null;
        
        // 1. Identificar quién es quién en esta colisión
        if (j instanceof Pinguino) {
            pTarget = (Pinguino) j;
            // Buscamos si hay una foca en la casilla destino
            for (Jugador item : gestorPartida.getPartida().getJugadores()) {
                if (item instanceof modelo.jugador.Foca && item.getPosicion() == targetPos) {
                    fTarget = (modelo.jugador.Foca) item;
                    break;
                }
            }
        } else if (j instanceof modelo.jugador.Foca) {
            fTarget = (modelo.jugador.Foca) j;
            // Buscamos si hay un pingüino en la casilla destino
            for (Jugador item : gestorPartida.getPartida().getJugadores()) {
                if (item instanceof Pinguino && item.getPosicion() == targetPos) {
                    pTarget = (Pinguino) item;
                    break;
                }
            }
        }

        // 2. Si hay colisión de especies distinta y foca no sobornada, preguntar
        if (pTarget != null && fTarget != null && !fTarget.isSoborno()) {
            int nPeces = pTarget.getInventario().contarPorTipo("Pez");
            if (nPeces > 0) {
                // Interrupción por UI
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("¡Interacción con la Foca!");
                alert.setHeaderText(null);
                alert.setContentText(pTarget.getNombre() + " ¡tienes un pescado! ¿Quieres sobornar a la foca?");
                
                javafx.scene.control.ButtonType btnSi = new javafx.scene.control.ButtonType("Sí (Sobornar)");
                javafx.scene.control.ButtonType btnNo = new javafx.scene.control.ButtonType("No");
                alert.getButtonTypes().setAll(btnSi, btnNo);

                java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnSi) {
                    // Acción de soborno
                    gestorPartida.getGestorJugador().jugadorUsaItem(pTarget, "Pez");
                    fTarget.setSoborno(true);
                    fTarget.setTurnosBloqueada(2);
                    appendLog(pTarget.getNombre() + " soborna a la foca con su pescado. 🐟");
                }
            }
        }
    }

    private void animarPasoAPaso(Jugador j, int posActual, int pasosRestantes, Runnable onFinish) {
        if (pasosRestantes <= 0) {
            onFinish.run();
            return;
        }

        int maxPos = gestorPartida.getPartida().getTablero().getTamaño() - 1;
        if (j.getPosicion() < maxPos) {
            int posNueva = j.getPosicion() + 1;
            ImageView ficha = getFichaDeJugador(j);
            if (ficha == null) { onFinish.run(); return; }

            // 1. Guardar la posición visual actual en la escena antes del salto
            double startX = ficha.getLayoutX() + ficha.getTranslateX();
            double startY = ficha.getLayoutY() + ficha.getTranslateY();

            // 2. Mover lógicamente y cambiar índices en el GridPane (esto "teletransporta")
            j.setPosicion(posNueva);
            GridPane.setColumnIndex(ficha, posNueva % COLUMNS);
            GridPane.setRowIndex(ficha, posNueva / COLUMNS);
            
            // 3. Forzar layout para que el ImageView calcule su nueva posición objetivo
            ficha.getParent().layout();
            
            // 4. Calcular el salto relativo para reponer la ficha visualmente donde estaba
            double newLayoutX = ficha.getLayoutX();
            double newLayoutY = ficha.getLayoutY();
            
            ficha.setTranslateX(startX - newLayoutX);
            ficha.setTranslateY(startY - newLayoutY);

            // 5. Animar suavemente hacia (0,0) (que es su posición en el GridPane)
            TranslateTransition tt = new TranslateTransition(Duration.millis(350), ficha);
            tt.setToX(0);
            tt.setToY(0);
            tt.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
            tt.setOnFinished(e -> {
                animarPasoAPaso(j, posNueva, pasosRestantes - 1, onFinish);
            });
            tt.play();
            ficha.toFront();
        } else {
            onFinish.run();
        }
    }

    private void concluirTurno() {
        actualizarPosicionesVisuales();
        if (gestorPartida.getPartida().isFinalizada()) {
            mostrarFinDePartida();
        } else {
            gestorPartida.avanzarTurno();
            actualizarLabelTurno();
            actualizarInventarioVisual();
            if (gestorPartida.getPartida().getJugadorActual() instanceof modelo.jugador.Foca) {
                jugarTurnoFoca();
            }
        }
    }

    private void jugarTurnoFoca() {
        modelo.jugador.Foca foca = (modelo.jugador.Foca) gestorPartida.getPartida().getJugadorActual();
        appendLog("🦭 Turno de la Foca " + foca.getNombre() + "...");
        
        // Pequeño delay inicial antes de tirar el dado
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override public void run() {
                javafx.application.Platform.runLater(() -> {
                    int avance = gestorPartida.tirarDado(foca);
                    dadoResultText.setText("Foca tira: " + avance);
                    appendLog("🦭 " + foca.getNombre() + " tira el dado: " + avance);
                    iniciarMovimientoAnimado(foca, avance);
                });
            }
        }, 1000);
    }

    private void mostrarFinDePartida() {
        Jugador ganador = gestorPartida.getPartida().getGanador();
        String nombre = (ganador != null) ? ganador.getNombre() : "Desconocido";
        appendLog("🏆 ¡El ganador es " + nombre + "! 🎉");
        turnLabel.setText("PARTIDA FINALIZADA");
        dado.setDisable(true);
    }

    @FXML private void handleSaveGame() {
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        helper.guardarBBDD(controlador.gestionbbdd.BBDD.conectarPredeterminado(), gestorPartida.getPartida(), usuarioLogueado);
        appendLog("✅ Partida guardada correctamente.");
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