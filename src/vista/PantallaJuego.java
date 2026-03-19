package vista;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
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
    @FXML private Button finTurno;

    private GestorPartida gestorPartida;
    // La variable p1Position ha sido eliminada para evitar desincronización con el modelo lógico.
    private static final int COLUMNS = 5;
    private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";

    @FXML
    private void initialize() {
        eventos.setText("¡El juego ha comenzado!");
        
        gestorPartida = new GestorPartida();
        iniciarPartida();
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

    private void mostrarTiposDeCasillasEnTablero(Tablero t) {
        tablero.getChildren().removeIf(node -> TAG_CASILLA_TEXT.equals(node.getUserData()));

        for (Casilla casilla : t.getCasillas()) {
            int i = casilla.getPosicion();
            String tipo = casilla.getClass().getSimpleName();

            Text texto = new Text(tipo.substring(0, Math.min(tipo.length(), 4))); // Abreviatura
            texto.setUserData(TAG_CASILLA_TEXT);
            texto.getStyleClass().add("cell-type");

            int row = i / COLUMNS;
            int col = i % COLUMNS;

            GridPane.setRowIndex(texto, row);
            GridPane.setColumnIndex(texto, col);
            tablero.getChildren().add(texto);
        }
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
        if (actual instanceof modelo.jugador.Foca) return; // La foca juega sola
        
        dado.setDisable(true);
        finTurno.setDisable(true);
        
        int resultado = gestorPartida.tirarDado(actual);
        dadoResultText.setText("Ha salido: " + resultado);

        moverJugadorUI(actual, resultado, () -> {
            String log = gestorPartida.procesarTurnoConAvance(actual, resultado);
            eventos.setText(log);
            actualizarPosicionesVisuales();
            finTurno.setDisable(true);
            
            // Finalizar turno automáticamente tras unos segundos
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(2));
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
        gestorPartida.siguienteTurno();
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        actualizarInventarioVisual();
        eventos.setText("Turno de: " + actual.getNombre());
        
        if (actual instanceof modelo.jugador.Foca) {
            dado.setDisable(true);
            finTurno.setDisable(true);
            jugarTurnoFoca();
        } else {
            dado.setDisable(false);
            finTurno.setDisable(false);
        }
    }

    private void jugarTurnoFoca() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        eventos.setText("Foca está pensando...");
        
        // Timer simple
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            int avance = gestorPartida.tirarDado(actual);
            moverJugadorUI(actual, avance, () -> {
                String log = gestorPartida.procesarTurnoConAvance(actual, avance);
                eventos.setText("FOCA:\n" + log);
                actualizarPosicionesVisuales();
                
                // Pasa turno automático para la foca
                javafx.animation.PauseTransition pause2 = new javafx.animation.PauseTransition(Duration.seconds(2));
                pause2.setOnFinished(ev -> botonFinalizarTurno());
                pause2.play();
            });
        });
        pause.play();
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
                    finTurno.setDisable(true);
                    
                    int resultado = ((Dado)item).tirarRandom();
                    dadoResultText.setText("Dado Especial: " + resultado);
                    
                    moverJugadorUI(j, resultado, () -> {
                        String log = gestorPartida.procesarTurnoConAvance(j, resultado);
                        eventos.setText(log);
                        actualizarPosicionesVisuales();
                        finTurno.setDisable(true);
                        
                        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(2));
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

    private void moverJugadorUI(Jugador j, int steps, Runnable onFinished) {
        int oldPosition = j.getPosicion();
        int targetPosition = oldPosition + steps;
        
        int maxPos = gestorPartida.getPartida().getTablero().getTamaño() - 1;
        if (targetPosition > maxPos) targetPosition = maxPos;
        if (targetPosition < 0) targetPosition = 0;

        int oldRow = oldPosition / COLUMNS;
        int oldCol = oldPosition % COLUMNS;
        int newRow = targetPosition / COLUMNS;
        int newCol = targetPosition % COLUMNS;

        double cellWidth = tablero.getWidth() / COLUMNS;
        double cellHeight = tablero.getHeight() / 10;

        double dx = (newCol - oldCol) * cellWidth;
        double dy = (newRow - oldRow) * cellHeight;

        Circle playerToken = getTokenDeJugador(j);
        TranslateTransition slide = new TranslateTransition(Duration.millis(350), playerToken);
        slide.setByX(dx);
        slide.setByY(dy);

        slide.setOnFinished(e -> {
            playerToken.setTranslateX(0);
            playerToken.setTranslateY(0);
            GridPane.setRowIndex(playerToken, newRow);
            GridPane.setColumnIndex(playerToken, newCol);
            
            if (onFinished != null) onFinished.run();
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