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
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import controlador.gestor.GestorPartida;
import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.items.Inventario;
import modelo.items.Dado;
import modelo.items.Item;
import modelo.tablero.Tablero;
import modelo.tablero.Casilla;

public class PantallaJuego {

    // Menu items
    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    // Buttons
    @FXML private Button dado;
    @FXML private Button rapido;
    @FXML private Button lento;
    @FXML private Button peces;
    @FXML private Button nieve;

    // Texts
    @FXML private Text dadoResultText;
    @FXML private Text rapido_t;
    @FXML private Text lento_t;
    @FXML private Text peces_t;
    @FXML private Text nieve_t;
    @FXML private Text eventos;

    // Game board and player pieces
    @FXML private GridPane tablero;
    @FXML private Circle P1;
    @FXML private Circle P2;
    @FXML private Circle P3;
    @FXML private Circle P4;
    @FXML private Circle PFoca;
    @FXML private javafx.scene.control.Label turnLabel;

    private GestorPartida gestorPartida;
    // La variable p1Position ha sido eliminada para evitar desincronización con el modelo lógico.
    private static final int COLUMNS = 5;
    private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";

    @FXML
    private void initialize() {
        eventos.setText("¡El juego ha comenzado!");

        // Asignar icono de imagen a los botones del inventario
        ponerIconoBoton(nieve,  "bola_nieve.png",      20);

        gestorPartida = new GestorPartida();
        iniciarPartida();
    }

    /** Carga una imagen de resources y la asigna como graphic del botón. */
    private void ponerIconoBoton(Button boton, String archivo, double tamaño) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/resources/" + archivo));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(tamaño);
            iv.setFitHeight(tamaño);
            iv.setPreserveRatio(true);
            boton.setGraphic(iv);
        } catch (Exception e) {
            // Si falla, el botón queda sin icono (no es crítico)
        }
    }

    /**
     * Requisito: Iniciar la partida configurando jugadores y tablero.
     */
    @FXML
    public void iniciarPartida() {
        ArrayList<Jugador> jugadores = new ArrayList<>();
        
        Pinguino p1 = new Pinguino("Jugador1", "Azul");
        p1.getInventario().añadirItem(new Dado()); 
        jugadores.add(p1);

        Pinguino p2 = new Pinguino("Jugador2", "Rojo");
        p2.getInventario().añadirItem(new Dado());
        jugadores.add(p2);
        
        modelo.jugador.Foca foca = new modelo.jugador.Foca("Morsa CPU", "Gris");
        jugadores.add(foca);

        Tablero modeloTablero = new Tablero();
        modeloTablero.generarTableroAleatorio();

        gestorPartida.nuevaPartida(jugadores, modeloTablero);
        
        P1.setVisible(true);
        P2.setVisible(true);
        P3.setVisible(false);
        P4.setVisible(false);
        PFoca.setVisible(true);
        
        refrescarPartida();
        
        if (gestorPartida.getPartida().getJugadorActual() instanceof modelo.jugador.Foca) {
            jugarTurnoFoca();
        } else {
            eventos.setText("Turno de: " + gestorPartida.getPartida().getJugadorActual().getNombre());
        }
    }

    /**
     * Requisito: Refrescar el estado visual del tablero y la interfaz.
     */
    @FXML
    public void refrescarPartida() {
        if (gestorPartida.getPartida() != null) {
            mostrarTiposDeCasillasEnTablero(gestorPartida.getPartida().getTablero());
            actualizarInventarioVisual();
            // Aquí se actualizarían las posiciones de las fichas si fuera una carga
        }
    }

    private ImageView crearImagenCasilla(String nombreArchivo) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/resources/" + nombreArchivo));
            ImageView iv = new ImageView(img);
            iv.setPreserveRatio(true);
            iv.setFitWidth(70);
            iv.setFitHeight(70);
            return iv;
        } catch (Exception e) {
            return null;
        }
    }

    private void mostrarTiposDeCasillasEnTablero(Tablero t) {
        tablero.getChildren().removeIf(node -> TAG_CASILLA_TEXT.equals(node.getUserData()));

        for (int i = 1; i < t.getTamaño() - 1; i++) {
            Casilla casilla = t.getCasillaEnPosicion(i);
            String tipo = (casilla != null) ? casilla.getClass().getSimpleName() : "";

            StackPane cell = new StackPane();
            cell.setUserData(TAG_CASILLA_TEXT);
            cell.getStyleClass().add("board-cell");
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            // Seleccionar imagen según el tipo de casilla
            String imgFile;
            switch (tipo) {
                case "Evento":          imgFile = "casilla_evento.png";    break;
                case "MotoNieve":       imgFile = "casilla_motonieve.png"; break;
                case "Oso":             imgFile = "casilla_oso.png";       break;
                case "Agujero":         imgFile = "casilla_agujero.png";   break;
                case "SueloQuebradizo": imgFile = "casilla_agujero.png";   break;
                default:                imgFile = "casilla_normal.png";    break;
            }

            ImageView iv = crearImagenCasilla(imgFile);
            if (iv != null) {
                cell.getChildren().add(iv);
            } else {
                // Fallback: mostrar texto si la imagen no carga
                Text texto = new Text(tipo);
                texto.getStyleClass().add("cell-type");
                cell.getChildren().add(texto);
            }

            int row = i / COLUMNS;
            int col = i % COLUMNS;

            GridPane.setRowIndex(cell, row);
            GridPane.setColumnIndex(cell, col);
            tablero.getChildren().add(cell);
        }
        
        // Traer a los jugadores al frente para que no queden tapados por las nuevas celdas
        P1.toFront();
        P1.setMouseTransparent(true);
        P2.toFront();
        P2.setMouseTransparent(true);
        P3.toFront();
        P3.setMouseTransparent(true);
        P4.toFront();
        P4.setMouseTransparent(true);
        PFoca.toFront();
        PFoca.setMouseTransparent(true);
    }

    private void actualizarInventarioVisual() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        if (actual instanceof Pinguino) {
            Pinguino p = (Pinguino) actual;
            Inventario inv = p.getInventario();
            rapido_t.setText("D. Rápido: " + inv.contarPorTipo("Dado Rapido"));
            lento_t.setText("D. Lento: " + inv.contarPorTipo("Dado Lento"));
            peces_t.setText("Peces: " + inv.contarPorTipo("Pez"));
            nieve_t.setText("Bolas: " + inv.contarPorTipo("Bola de Nieve"));
        } else {
            rapido_t.setText("D. Rápido: -");
            lento_t.setText("D. Lento: -");
            peces_t.setText("Peces: -");
            nieve_t.setText("Bolas: -");
        }
    }

    // --- ACCIONES DE BOTONES ---

    @FXML
    public void botonTirarDado() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        if (actual instanceof modelo.jugador.Foca) return;
        
        dado.setDisable(true);
        
        int resultado = gestorPartida.tirarDado(actual);
        dadoResultText.setText("Ha salido: " + resultado);
        eventos.setText("⏳ " + actual.getNombre() + " avanza " + resultado + " casillas...");

        animarPasoAPaso(actual, actual.getPosicion(), resultado, () -> {
            String log = gestorPartida.procesarTurnoConAvance(actual, resultado);
            eventos.setText(log.trim());
            actualizarInventarioVisual();
            actualizarPosicionesVisuales();

            if (gestorPartida.getPartida().isFinalizada()) {
                mostrarFinDePartida();
                return;
            }

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(2.5));
            pause.setOnFinished(ev -> botonFinalizarTurno());
            pause.play();
        });
    }

    @FXML
    public void botonUsarObjeto() {
        System.out.println("Selector de objetos...");
    }

    @FXML
    public void botonFinalizarTurno() {
        if (gestorPartida.getPartida().isFinalizada()) {
            mostrarFinDePartida();
            return;
        }
        gestorPartida.siguienteTurno();
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        actualizarInventarioVisual();
        String nombreActual = actual.getNombre();
        eventos.setText("▶ Turno de: " + nombreActual);
        if (turnLabel != null) turnLabel.setText("▶ Turno de: " + nombreActual);
        
        if (actual instanceof modelo.jugador.Foca) {
            dado.setDisable(true);
            jugarTurnoFoca();
        } else {
            dado.setDisable(false);
        }
    }

    private void jugarTurnoFoca() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        eventos.setText("🦭 La Foca está pensando...");
        
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            int avance = gestorPartida.tirarDado(actual);
            dadoResultText.setText("Foca: " + avance);
            eventos.setText("🦭 La Foca avanza " + avance + " casillas...");

            animarPasoAPaso(actual, actual.getPosicion(), avance, () -> {
                String log = gestorPartida.procesarTurnoConAvance(actual, avance);
                eventos.setText("🦭 FOCA:\n" + log.trim());
                actualizarPosicionesVisuales();

                if (gestorPartida.getPartida().isFinalizada()) {
                    mostrarFinDePartida();
                    return;
                }

                javafx.animation.PauseTransition pause2 = new javafx.animation.PauseTransition(Duration.seconds(2.5));
                pause2.setOnFinished(ev -> botonFinalizarTurno());
                pause2.play();
            });
        });
        pause.play();
    }

    private void mostrarFinDePartida() {
        dado.setDisable(true);
        rapido.setDisable(true);
        lento.setDisable(true);
        peces.setDisable(true);
        nieve.setDisable(true);
        Jugador ganador = gestorPartida.getPartida().getGanador();
        String nombre = (ganador != null) ? ganador.getNombre() : "Desconocido";
        dadoResultText.setText("FIN");
        eventos.setText("🏆 ¡PARTIDA TERMINADA!\n¡" + nombre + " ha ganado la partida!\n\nPulsa 'New' para jugar de nuevo.");
    }

    @FXML
    public void guardarPartida() {
        gestorPartida.guardarPartida();
        eventos.setText("Partida guardada en BD.");
    }

    @FXML
    public void cargarPartida() {
        // Por simplificación cargamos la ID 1 para el ejemplo
        gestorPartida.cargaPartida(1);
        refrescarPartida();
        eventos.setText("Partida cargada.");
    }

    // --- Aliases para mantener compatibilidad con FXML actual hasta que se actualice ---
    @FXML private void handleDado(ActionEvent event) { botonTirarDado(); }
    @FXML private void handleRapido() { usarItemEspecifico("Dado Rapido"); }
    @FXML private void handleLento() { usarItemEspecifico("Dado Lento"); }
    @FXML private void handlePeces() { usarItemEspecifico("Pez"); }
    @FXML private void handleNieve() { usarItemEspecifico("Bola de Nieve"); }
    @FXML private void handleNewGame() { iniciarPartida(); }
    @FXML private void handleSaveGame() { guardarPartida(); }
    @FXML private void handleLoadGame() { cargarPartida(); }
    @FXML private void handleQuitGame() { System.exit(0); }

    private void usarItemEspecifico(String nombre) {
        Jugador j = gestorPartida.getPartida().getJugadorActual();
        if (j instanceof Pinguino) {
            Pinguino p = (Pinguino) j;
            Item item = p.getInventario().obtenerItemPorNombre(nombre);
            
            if (item != null) {
                if (nombre.equals("Dado Lento") || nombre.equals("Dado Rapido")) {
                    p.getInventario().quitarItem(item);
                    dado.setDisable(true);
                    
                    int resultado = ((Dado)item).tirarRandom();
                    dadoResultText.setText("Dado Especial: " + resultado);
                    eventos.setText("⏳ " + p.getNombre() + " usa dado especial, avanza " + resultado + " casillas...");
                    
                    animarPasoAPaso(j, j.getPosicion(), resultado, () -> {
                        String log = gestorPartida.procesarTurnoConAvance(j, resultado);
                        eventos.setText(log.trim());
                        actualizarInventarioVisual();
                        actualizarPosicionesVisuales();

                        if (gestorPartida.getPartida().isFinalizada()) {
                            mostrarFinDePartida();
                            return;
                        }

                        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(2.5));
                        pause.setOnFinished(ev -> botonFinalizarTurno());
                        pause.play();
                    });
                } else if (nombre.equals("Bola de Nieve")) {
                    p.getInventario().quitarItem(item);
                    eventos.setText("Has lanzado una Bola de Nieve. (Efecto no implementado visual guiado)");
                } else if (nombre.equals("Pez")) {
                    eventos.setText("El pez se usa automáticamente al encontrarse con la Foca!");
                }
                actualizarInventarioVisual();
            } else {
                eventos.setText("No tienes " + nombre);
            }
        }
    }

    /**
     * Anima la ficha casilla a casilla.
     * Mueve el token visualmente paso a paso (150ms/casilla) y cuando
     * llega al destino llama a onFinished.
     */
    private void animarPasoAPaso(Jugador j, int posOrigen, int pasos, Runnable onFinished) {
        if (pasos <= 0) {
            if (onFinished != null) onFinished.run();
            return;
        }

        int maxPos = gestorPartida.getPartida().getTablero().getTamaño() - 1;
        int destino = Math.min(posOrigen + pasos, maxPos);
        int totalPasos = destino - posOrigen;

        avanzarUnPaso(j, posOrigen, totalPasos, 0, onFinished);
    }

    private void avanzarUnPaso(Jugador j, int inicio, int totalPasos, int pasoActual, Runnable onFinished) {
        if (pasoActual >= totalPasos) {
            if (onFinished != null) onFinished.run();
            return;
        }

        int posActual = inicio + pasoActual;
        int maxPos = gestorPartida.getPartida().getTablero().getTamaño() - 1;

        // Si ya estamos en el límite, no hay donde avanzar: terminar
        if (posActual >= maxPos) {
            if (onFinished != null) onFinished.run();
            return;
        }

        int posSiguienteRaw = posActual + 1;
        // Clamp: si llegamos al final, animamos este último paso y luego terminamos
        boolean esUltimoPorLimite = (posSiguienteRaw >= maxPos);
        final int posSiguiente = Math.min(posSiguienteRaw, maxPos); // final → usable en lambda

        int rowActual = posActual / COLUMNS;
        int colActual = posActual % COLUMNS;
        int rowSig    = posSiguiente / COLUMNS;
        int colSig    = posSiguiente % COLUMNS;

        double cellWidth  = tablero.getWidth()  / COLUMNS;
        double cellHeight = tablero.getHeight() / 10;

        double dx = (colSig - colActual) * cellWidth;
        double dy = (rowSig - rowActual) * cellHeight;

        Circle token = getTokenDeJugador(j);

        TranslateTransition slide = new TranslateTransition(Duration.millis(150), token);
        slide.setByX(dx);
        slide.setByY(dy);
        slide.setOnFinished(e -> {
            // Snap al GridPane
            token.setTranslateX(0);
            token.setTranslateY(0);
            GridPane.setRowIndex(token, rowSig);
            GridPane.setColumnIndex(token, colSig);

            // Log de la casilla actual
            modelo.tablero.Casilla c = gestorPartida.getPartida().getTablero().getCasillaEnPosicion(posSiguiente);
            int total = Math.min(totalPasos, maxPos - inicio); // pasos reales posibles
            if (c != null) {
                eventos.setText("Paso " + (pasoActual + 1) + "/" + total
                        + " — casilla " + posSiguiente + " (" + c.getClass().getSimpleName() + ")");
            } else {
                eventos.setText("Paso " + (pasoActual + 1) + "/" + total
                        + " — casilla " + posSiguiente);
            }

            // Si alcanzamos el límite del tablero, terminamos
            if (esUltimoPorLimite || pasoActual + 1 >= totalPasos) {
                if (onFinished != null) onFinished.run();
                return;
            }

            // Pausa de 80ms antes del siguiente paso
            javafx.animation.PauseTransition gap = new javafx.animation.PauseTransition(Duration.millis(80));
            gap.setOnFinished(ev -> avanzarUnPaso(j, inicio, totalPasos, pasoActual + 1, onFinished));
            gap.play();
        });
        slide.play();
    }

    private Circle getTokenDeJugador(Jugador j) {
        if (j.getNombre().equals("Jugador1")) return P1;
        if (j.getNombre().equals("Jugador2")) return P2;
        if (j instanceof modelo.jugador.Foca) return PFoca;
        return P3;
    }

    private void actualizarPosicionesVisuales() {
        for (Jugador j : gestorPartida.getPartida().getJugadores()) {
            Circle token = getTokenDeJugador(j);
            GridPane.setRowIndex(token, j.getPosicion() / COLUMNS);
            GridPane.setColumnIndex(token, j.getPosicion() % COLUMNS);
            token.setTranslateX(0);
            token.setTranslateY(0);
        }
    }

    public void setGestorPartida(GestorPartida gestorPartida) {
        this.gestorPartida = gestorPartida;
    }
}