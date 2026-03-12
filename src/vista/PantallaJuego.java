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

    private GestorPartida gestorPartida;
    private int p1Position = 0; 
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
        
        // Configuración inicial de ejemplo de un pingüino
        Pinguino p1 = new Pinguino("Jugador1", "Azul");
        p1.getInventario().añadirItem(new Dado()); // Dado normal inicial
        jugadores.add(p1);

        // Generamos el tablero desde el modelo
        Tablero modeloTablero = new Tablero();
        modeloTablero.generarTableroAleatorio();

        gestorPartida.nuevaPartida(jugadores, modeloTablero);
        
        refrescarPartida();
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
        }
    }

    // --- ACCIONES DE BOTONES (SEGÚN DIAGRAMA) ---

    @FXML
    public void botonTirarDado() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        
        dado.setDisable(true);
        
        int resultado = gestorPartida.tirarDado(actual);
        dadoResultText.setText("Ha salido: " + resultado);

        // La lógica de movimiento animado se mantiene
        moveP1(resultado);
    }

    @FXML
    public void botonUsarObjeto() {
        // En una implementación real, esto abriría un selector o usaría el seleccionado
        System.out.println("Usando objeto...");
    }

    @FXML
    public void botonFinalizarTurno() {
        gestorPartida.siguienteTurno();
        actualizarInventarioVisual();
        eventos.setText("Turno de: " + gestorPartida.getPartida().getJugadorActual().getNombre());
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
            // Lógica delegada al gestor
            System.out.println("Usando: " + nombre);
            actualizarInventarioVisual();
        }
    }

    private void moveP1(int steps) {
        int oldPosition = p1Position;
        p1Position += steps;
        
        int maxPos = gestorPartida.getPartida().getTablero().getTamaño() - 1;
        if (p1Position > maxPos) p1Position = maxPos;
        if (p1Position < 0) p1Position = 0;

        int oldRow = oldPosition / COLUMNS;
        int oldCol = oldPosition % COLUMNS;
        int newRow = p1Position / COLUMNS;
        int newCol = p1Position % COLUMNS;

        double cellWidth = tablero.getWidth() / COLUMNS;
        double cellHeight = tablero.getHeight() / 10;

        double dx = (newCol - oldCol) * cellWidth;
        double dy = (newRow - oldRow) * cellHeight;

        TranslateTransition slide = new TranslateTransition(Duration.millis(350), P1);
        slide.setByX(dx);
        slide.setByY(dy);

        slide.setOnFinished(e -> {
            P1.setTranslateX(0);
            P1.setTranslateY(0);
            GridPane.setRowIndex(P1, newRow);
            GridPane.setColumnIndex(P1, newCol);
            dado.setDisable(false);
            
            // Ejecutar casilla
            gestorPartida.procesarTurnoJugador(gestorPartida.getPartida().getJugadorActual());
        });

        slide.play();
    }

    public void setGestorPartida(GestorPartida gestorPartida) {
        this.gestorPartida = gestorPartida;
    }
}