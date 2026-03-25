package vista;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javafx.scene.layout.TilePane;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.paint.Color;

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
    
    @FXML private VBox vboxEventos;
    @FXML private ScrollPane scrollEventos;

    // Game board and player pieces
    @FXML private GridPane tablero;
    @FXML private Circle P1;
    @FXML private Circle P2;
    @FXML private Circle P3;
    @FXML private Circle P4;
    @FXML private Circle PFoca;
    @FXML private Circle PFoca2;
    @FXML private javafx.scene.control.Label turnLabel;

    @FXML private TilePane playersPanel;
    private Map<Jugador, VBox> playerUIMap = new HashMap<>();

    // Elementos del menú hamburguesa
    @FXML private Button hamburgerButton;
    @FXML private VBox menuOverlay;

    private GestorPartida gestorPartida;
    private static final int COLUMNS = 5;
    private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";
    
    // Listas para acceso dinámico a los círculos
    private ArrayList<Circle> fichasPinguinos;
    private ArrayList<Circle> fichasFocas;

    @FXML
    private void initialize() {
        registrarEvento("¡Bienvenido a El Juego de Pingu! 🐧", null);
        
        // Inicializar listas de fichas
        fichasPinguinos = new ArrayList<>();
        fichasPinguinos.add(P1);
        fichasPinguinos.add(P2);
        fichasPinguinos.add(P3);
        fichasPinguinos.add(P4);

        fichasFocas = new ArrayList<>();
        fichasFocas.add(PFoca);
        fichasFocas.add(PFoca2);

        // Ocultar todas las fichas al inicio
        for(Circle c : fichasPinguinos) c.setVisible(false);
        for(Circle c : fichasFocas) c.setVisible(false);

        gestorPartida = new GestorPartida();
        
        // No llamamos a iniciarPartida() aquí para que el menú pueda pasar la configuración
    }

    /**
     * Permite al menú pasar una partida ya creada.
     */
    public void prepararPartidaPersonalizada(modelo.partida.Partida p) {
        this.gestorPartida.nuevaPartida(p.getJugadores(), p.getTablero());
        configurarFichasYRefrescar();
    }

    /**
     * Permite al menú cargar una partida de la BD.
     */
    public void cargarPartidaEspecifica(int id) {
        this.gestorPartida.cargaPartida(id);
        configurarFichasYRefrescar();
    }

    private void configurarFichasYRefrescar() {
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        
        int pingCount = 0;
        int sealCount = 0;

        for (Jugador j : jugadores) {
            if (j instanceof modelo.jugador.Pinguino) {
                if (pingCount < fichasPinguinos.size()) {
                    fichasPinguinos.get(pingCount).setVisible(true);
                    pingCount++;
                }
            } else if (j instanceof modelo.jugador.Foca) {
                if (sealCount < fichasFocas.size()) {
                    fichasFocas.get(sealCount).setVisible(true);
                    sealCount++;
                }
            }
        }

        refrescarPartida();
        
        Jugador actual = gestorPartida.getPartida().getJugadorActual();

        // Generar UI de Jugadores en playersPanel
        if (playersPanel != null) {
            playersPanel.getChildren().clear();
            playerUIMap.clear();
            for (Jugador j : jugadores) {
                VBox pBox = new VBox(5);
                pBox.setAlignment(javafx.geometry.Pos.CENTER);
                pBox.setPrefSize(90, 70);
                pBox.setStyle("-fx-border-color: #1a3557; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 5;");
                
                Circle skin = new Circle(14);
                Circle tokenOriginal = getTokenDeJugador(j);
                if (tokenOriginal != null) {
                    skin.setId(tokenOriginal.getId());
                    skin.getStyleClass().addAll(tokenOriginal.getStyleClass());
                } else if (j instanceof modelo.jugador.Foca) {
                    skin.getStyleClass().add("foca");
                } else {
                    skin.getStyleClass().add("player");
                }
                
                Label nameLbl = new Label(j.getNombre());
                nameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
                
                pBox.getChildren().addAll(skin, nameLbl);
                playersPanel.getChildren().add(pBox);
                playerUIMap.put(j, pBox);
            }
        }
        actualizarTurnoVisual(actual);

        registrarEvento("¡Partida lista! Turno de: " + actual.getNombre(), actual);
        if (turnLabel != null) turnLabel.setText("▶ Turno de: " + actual.getNombre());
        
        if (actual instanceof modelo.jugador.Foca) {
            dado.setDisable(true);
            jugarTurnoFoca();
        }
    }

    private void actualizarTurnoVisual(Jugador actual) {
        if (playerUIMap == null || playerUIMap.isEmpty()) return;
        for (Map.Entry<Jugador, VBox> entry : playerUIMap.entrySet()) {
            if (entry.getKey().equals(actual)) {
                entry.getValue().setStyle("-fx-border-color: #1dd1a1; -fx-border-width: 3; -fx-border-radius: 5; -fx-padding: 5; -fx-background-color: rgba(29, 209, 161, 0.2);");
            } else {
                entry.getValue().setStyle("-fx-border-color: #1a3557; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 5; -fx-background-color: transparent;");
            }
        }
    }

    /**
     * Requisito: Iniciar la partida configurando jugadores y tablero (Versión por defecto).
     */
    @FXML
    public void iniciarPartida() {
        // Mantenemos este método por compatibilidad si se pulsa "New" desde dentro del juego
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
        
        prepararPartidaPersonalizada(gestorPartida.getPartida());
    }

    /**
     * Requisito: Refrescar el estado visual del tablero y la interfaz.
     */
    @FXML
    public void refrescarPartida() {
        if (gestorPartida.getPartida() != null) {
            mostrarTiposDeCasillasEnTablero(gestorPartida.getPartida().getTablero());
            actualizarInventarioVisual();
            actualizarPosicionesVisuales();
        }
    }

    private ImageView crearImagenCasilla(String nombreArchivo) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/resources/" + nombreArchivo));
            ImageView iv = new ImageView(img);
            iv.setPreserveRatio(true);
            iv.setFitWidth(45);
            iv.setFitHeight(45);
            return iv;
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + nombreArchivo + " - " + e.getMessage());
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
                case "Trineo":
                case "trineo":
                    imgFile = "casilla_trineo.png"; 
                    break;
                default:
                    imgFile = "casilla_normal.png"; 
                    break;
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
        registrarEvento("⏳ " + actual.getNombre() + " avanza " + resultado + " casillas...", actual);

        animarPasoAPaso(actual, actual.getPosicion(), resultado, () -> {
            String log = gestorPartida.procesarTurnoConAvance(actual, resultado);
            registrarEvento(log.trim(), actual);
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
        registrarEvento("▶ Turno de: " + nombreActual, actual);
        if (turnLabel != null) turnLabel.setText("▶ Turno de: " + nombreActual);
        actualizarTurnoVisual(actual);
        
        if (actual instanceof modelo.jugador.Foca) {
            dado.setDisable(true);
            jugarTurnoFoca();
        } else {
            dado.setDisable(false);
        }
    }

    private void jugarTurnoFoca() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        registrarEvento("🦭 La Foca está pensando...", actual);
        
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            int avance = gestorPartida.tirarDado(actual);
            dadoResultText.setText("Foca: " + avance);
            registrarEvento("🦭 La Foca avanza " + avance + " casillas...", actual);

            animarPasoAPaso(actual, actual.getPosicion(), avance, () -> {
                String log = gestorPartida.procesarTurnoConAvance(actual, avance);
                registrarEvento("🦭 FOCA:\n" + log.trim(), actual);
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
        registrarEvento("🏆 ¡PARTIDA TERMINADA!\n¡" + nombre + " ha ganado la partida!", ganador);
        registrarEvento("Pulsa 'New' para jugar de nuevo.", null);
    }

    @FXML
    public void guardarPartida() {
        gestorPartida.guardarPartida();
        registrarEvento("✅ Partida guardada en BD.", null);
    }

    @FXML
    public void cargarPartida() {
        // Por simplificación cargamos la ID 1 para el ejemplo
        gestorPartida.cargaPartida(1);
        refrescarPartida();
        registrarEvento("📂 Partida cargada.", null);
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

    @FXML
    public void toggleMenu() {
        boolean visible = !menuOverlay.isVisible();
        menuOverlay.setVisible(visible);
        menuOverlay.setManaged(visible);
        if (visible) menuOverlay.toFront();
    }

    @FXML
    public void handleGoToMenu(ActionEvent event) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/resources/PantallaMenu.fxml");
            if (fxmlUrl == null) fxmlUrl = getClass().getClassLoader().getResource("resources/PantallaMenu.fxml");
            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("El Juego de Pingu - Menú");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                    registrarEvento("⏳ " + p.getNombre() + " usa dado especial, avanza " + resultado + " casillas...", p);
                    
                    animarPasoAPaso(j, j.getPosicion(), resultado, () -> {
                        String log = gestorPartida.procesarTurnoConAvance(j, resultado);
                        registrarEvento(log.trim(), p);
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
                    registrarEvento("❄️ Has lanzado una Bola de Nieve.", p);
                } else if (nombre.equals("Pez")) {
                    registrarEvento("🐟 El pez se usa automáticamente al encontrarse con la Foca!", p);
                }
                actualizarInventarioVisual();
            } else {
                registrarEvento("❌ No tienes " + nombre, p);
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
            String msgPaso = (c != null) 
                ? "Paso " + (pasoActual + 1) + "/" + total + " — casilla " + posSiguiente + " (" + c.getClass().getSimpleName() + ")"
                : "Paso " + (pasoActual + 1) + "/" + total + " — casilla " + posSiguiente;
            
            registrarEvento(msgPaso, j);

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
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        
        if (j instanceof modelo.jugador.Foca) {
            int focaIndex = 0;
            for (Jugador jug : jugadores) {
                if (jug == j) break;
                if (jug instanceof modelo.jugador.Foca) focaIndex++;
            }
            if (focaIndex < fichasFocas.size()) return fichasFocas.get(focaIndex);
            return PFoca;
        }
        
        int pingIndex = 0;
        for (Jugador jug : jugadores) {
            if (jug == j) break;
            if (jug instanceof Pinguino) pingIndex++;
        }
        
        if (pingIndex < fichasPinguinos.size()) {
            return fichasPinguinos.get(pingIndex);
        }
        
        return P1;
    }

    private void actualizarPosicionesVisuales() {
        if (gestorPartida.getPartida() == null) return;
        
        for (Jugador j : gestorPartida.getPartida().getJugadores()) {
            Circle token = getTokenDeJugador(j);
            if (token != null) {
                GridPane.setRowIndex(token, j.getPosicion() / COLUMNS);
                GridPane.setColumnIndex(token, j.getPosicion() % COLUMNS);
                token.setTranslateX(0);
                token.setTranslateY(0);
            }
        }
    }

    public void setGestorPartida(GestorPartida gestorPartida) {
        this.gestorPartida = gestorPartida;
    }

    private void registrarEvento(String mensaje, Jugador j) {
        if (vboxEventos == null) return;

        Label entry = new Label(mensaje);
        entry.setWrapText(true);
        entry.setMaxWidth(310);

        // Estilo dinámico según el jugador
        if (j != null) {
            String colorStyle = "-fx-text-fill: #e8f4fd;"; // Default
            if (j instanceof modelo.jugador.Foca) {
                colorStyle = "-fx-text-fill: #f87171; -fx-font-weight: bold;"; // Rojo para foca
            } else {
                // Pinguino: intentamos usar su color o un color brillante
                colorStyle = "-fx-text-fill: #4fc3f7; -fx-font-weight: bold;"; // Celeste para jugadores
            }
            entry.setStyle(colorStyle + " -fx-font-size: 13px;");
            entry.setText("➤ [" + j.getNombre() + "]: " + mensaje.replace("⏳ ", "").replace(j.getNombre() + " ", ""));
        } else {
            // Mensaje de sistema
            entry.setStyle("-fx-text-fill: #a8c8e8; -fx-font-size: 12px; -fx-font-style: italic;");
        }

        vboxEventos.getChildren().add(entry);

        // Auto-scroll al final
        Platform.runLater(() -> {
            if (scrollEventos != null) scrollEventos.setVvalue(1.0);
        });
    }
}