package vista;

import java.util.ArrayList;
import javafx.application.Platform;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.geometry.Bounds;

import java.util.HashMap;
import java.util.Map;
import javafx.animation.ScaleTransition;

import controlador.gestor.GestorPartida;
import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
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
    @FXML private Text moto_t;
    @FXML private TextFlow eventosContenedor;

    @FXML private Pane tablero;   // ← ahora es Pane, no GridPane
    @FXML private ImageView P1, P2, P3, P4;
    @FXML private ImageView PFoca, PFoca2, PFoca3, PFoca4;
    @FXML private javafx.scene.control.Label inventoryTitle;

    @FXML private javafx.scene.layout.HBox rosterContainer;
    @FXML private StackPane turnAnimationOverlay;
    @FXML private ImageView turnAnimationSkin;
    @FXML private Label turnAnimationText;

    @FXML private Button hamburgerButton;
    @FXML private VBox menuOverlay;
    @FXML private ScrollPane eventosScroll;

    private GestorPartida gestorPartida;
    private String usuarioLogueado;

    // ══════════════════════════════════════════════════
    //  CONFIGURACIÓN DE LA PASARELA HELADA
    // ══════════════════════════════════════════════════

    /** Dimensiones naturales de la imagen de fondo (tablero_pasarela.png). */
    /** Dimensiones virtuales para el mapeo de coordenadas (proporción del diseño). */
    private static final double IMG_W = 1000.0;
    private static final double IMG_H = 1000.0;

    /** Tamaño visual de cada casilla y de cada ficha de jugador (px). */
    private static final double CELL_SIZE  = 34.0;
    private static final double TOKEN_SIZE = 30.0;

    /**
     * Coordenadas del CENTRO de cada una de las 50 casillas
     * en el espacio de la imagen original (1390×780).
     *
     * en el espacio de la imagen original.
     */
    private static final double[][] PATH_IMG = {
        // 1-10
        {245, 180}, {305, 180}, {442, 215}, {520, 215}, {598, 215}, {677, 215}, {755, 215}, {824, 245}, {889, 289}, {840, 349},
        // 11-20
        {779, 385}, {700, 385}, {622, 385}, {544, 385}, {465, 385}, {387, 385}, {309, 385}, {234, 396}, {170, 442}, {150, 491},
        // 21-30
        {204, 548}, {271, 575}, {349, 575}, {428, 575}, {506, 575}, {584, 575}, {663, 575}, {741, 575}, {820, 575}, {890, 590},
        // 31-40
        {931, 656}, {885, 699}, {816, 736}, {740, 745}, {661, 745}, {583, 745}, {505, 745}, {426, 745}, {348, 745}, {270, 745},
        // 41-50
        {194, 751}, {141, 809}, {205, 851}, {275, 880}, {353, 880}, {432, 880}, {510, 880}, {588, 880}, {667, 880}, {745, 880}
    };

    private ArrayList<ImageView> fichasPinguinos;
    private ArrayList<ImageView> fichasFocas;
    private ArrayList<StackPane> celdasVisuales = new ArrayList<>();
    private double lastW = -1, lastH = -1;
	
    private Map<Jugador, VBox> iceCubesMap;

    // ══════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ══════════════════════════════════════════════════

    @FXML
    private void initialize() {
        appendLog(null, "🐧 ¡Bienvenido a El Juego de Pingu!");

        prepararFichas();

        gestorPartida = new GestorPartida();

        // RE-POSICIONAMIENTO RESPONSIVO:
        // Si el tamaño del tablero cambia (ventana <-> pantalla completa), actualizamos todo.
        tablero.widthProperty().addListener((obs, oldVal, newVal) -> actualizarTodoVisually());
        tablero.heightProperty().addListener((obs, oldVal, newVal) -> actualizarTodoVisually());
    }

    private void prepararFichas() {
        fichasPinguinos = new ArrayList<>();
        fichasPinguinos.add(P1); fichasPinguinos.add(P2);
        fichasPinguinos.add(P3); fichasPinguinos.add(P4);

        fichasFocas = new ArrayList<>();
        fichasFocas.add(PFoca); fichasFocas.add(PFoca2);
        fichasFocas.add(PFoca3); fichasFocas.add(PFoca4);

        for (ImageView iv : fichasPinguinos) iv.setVisible(false);
        for (ImageView iv : fichasFocas)    iv.setVisible(false);
    }

    /** Redibuja o ajusta el tablero según el nuevo tamaño del panel. */
    private void actualizarTodoVisually() {
        double w = tablero.getWidth();
        double h = tablero.getHeight();
        
        // Evitamos recalcular si el tamaño no ha cambiado realmente o es inválido
        if (w <= 0 || h <= 0 || (Math.abs(w - lastW) < 0.1 && Math.abs(h - lastH) < 0.1)) return;
        lastW = w; lastH = h;

        // Usamos runLater para asegurar que el motor de layout de JavaFX haya terminado de propagar tamaños
        Platform.runLater(() -> {
            // Si ya hay celdas, solo las reposicionamos y escalamos
            if (!celdasVisuales.isEmpty()) {
                actualizarEscalaYPosicionDeElementos();
            } else {
                // Primera vez: construir
                tablero.getChildren().clear();
                celdasVisuales.clear();
                prepararFichas();
                construirTableroVisual();
            }
            actualizarPosicionesVisuales();
        });
    }

    /** Escala y reposiciona los elementos existentes sin volver a crearlos. */
    private void actualizarEscalaYPosicionDeElementos() {
        double boardW = tablero.getWidth();
        double boardH = tablero.getHeight();
        double scale = Math.min(boardW / IMG_W, boardH / IMG_H);
        double scaledSize = CELL_SIZE * scale;

        // 1. Actualizar Celdas
        for (int i = 0; i < celdasVisuales.size(); i++) {
            StackPane cell = celdasVisuales.get(i);
            posicionarEnCasilla(cell, i, CELL_SIZE);

            // Actualizar imagen interna de la casilla si existe
            for (Node child : cell.getChildren()) {
                if (child instanceof ImageView) {
                    ImageView iv = (ImageView) child;
                    iv.setFitWidth(scaledSize);
                    iv.setFitHeight(scaledSize);
                } else if (child instanceof Text) {
                    child.setStyle("-fx-font-size: " + (14 * scale) + "px;");
                }
            }
        }
    }

    // ══════════════════════════════════════════════════
    //  CONVERSIÓN DE COORDENADAS IMAGEN → PANE
    // ══════════════════════════════════════════════════

    /**
     * Convierte un punto en coordenadas de la imagen original
     * a coordenadas locales del Pane del tablero, teniendo en
     * cuenta el escalado "cover" del fondo de pantalla.
     */
    private double[] imageToPaneCoords(double imgX, double imgY) {
        double boardW = tablero.getWidth();
        double boardH = tablero.getHeight();

        if (boardW <= 0 || boardH <= 0) {
            // Fallback razonable para inicialización
            return new double[]{ imgX, imgY };
        }

        // Lógica "contain" base
        double scale = Math.min(boardW / IMG_W, boardH / IMG_H);

        // Alineación "center center" (sincronizado con el CSS)
        // Calculamos el espacio sobrante a los lados (X) y arriba/abajo (Y) para centrar
        double imgOffX = (boardW - IMG_W * scale) / 2.0;
        double imgOffY = (boardH - IMG_H * scale) / 2.0;

        double bpX = imgX * scale + imgOffX;
        double bpY = imgY * scale + imgOffY;

        return new double[]{ bpX, bpY };
    }

    /** Posiciona cualquier nodo en el centro de la casilla i, escalando el nodo según el tamaño del tablero. */
    private void posicionarEnCasilla(Node node, int i, double baseSize) {
        double boardW = tablero.getWidth();
        double boardH = tablero.getHeight();
        double scale = Math.min(boardW / IMG_W, boardH / IMG_H);
        
        double scaledSize = baseSize * scale;
        if (node instanceof Region) {
            Region r = (Region) node;
            r.setPrefSize(scaledSize, scaledSize);
            r.setMaxSize(scaledSize, scaledSize);
            r.setMinSize(scaledSize, scaledSize);
        }

        double[] pos = imageToPaneCoords(PATH_IMG[i][0], PATH_IMG[i][1]);
        node.setLayoutX(pos[0] - scaledSize / 2.0);
        node.setLayoutY(pos[1] - scaledSize / 2.0);
    }

    /** Posiciona una ficha en su casilla con un pequeño offset por índice, escalando según el tablero. */
    private void posicionarFicha(ImageView ficha, int cellPos, int tokenOffset) {
        int safePos = Math.min(cellPos, PATH_IMG.length - 1);
        double boardW = tablero.getWidth();
        double boardH = tablero.getHeight();
        double scale = Math.min(boardW / IMG_W, boardH / IMG_H);
        
        double scaledTokenSize = TOKEN_SIZE * scale;
        ficha.setFitWidth(scaledTokenSize);
        ficha.setFitHeight(scaledTokenSize);

        double[] pos = imageToPaneCoords(PATH_IMG[safePos][0], PATH_IMG[safePos][1]);
        // Offset también escalado
        double offsetScale = 8.0 * scale;
        double offsetVY = 5.0 * scale;

        ficha.setLayoutX(pos[0] - scaledTokenSize / 2.0 + tokenOffset * offsetScale);
        ficha.setLayoutY(pos[1] - scaledTokenSize / 2.0 - tokenOffset * offsetVY);
    }

    // ══════════════════════════════════════════════════
    //  LOG DE EVENTOS
    // ══════════════════════════════════════════════════

    private void appendLog(Jugador j, String msg) {
        if (eventosContenedor == null) return;
        Text textNode = new Text();
        textNode.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        if (j != null) {
            textNode.setFill(Color.web(getPlayerColorHex(j)));
            textNode.setText("• " + j.getNombre() + ": " + msg + "\n");
        } else {
            textNode.setFill(Color.WHITE);
            textNode.setText(msg + "\n");
        }
        eventosContenedor.getChildren().add(textNode);
        if (eventosScroll != null) {
            javafx.application.Platform.runLater(() -> eventosScroll.setVvalue(1.0));
        }
    }

    private String getPlayerColorHex(Jugador j) {
        if (j instanceof modelo.jugador.Foca) return "#00ffa3";
        String color = j.getColor() != null ? j.getColor().toLowerCase() : "";
        if (j.getNombre().toLowerCase().contains("1") || color.contains("azul"))    return "#00e5ff";
        if (j.getNombre().toLowerCase().contains("2") || color.contains("rojo"))    return "#ff1744";
        switch (color) {
            case "verde":    return "#00ffa3";
            case "amarillo": return "#ffd740";
            default:         return "#ffffff";
        }
    }

    // ══════════════════════════════════════════════════
    //  SETTERS PÚBLICOS
    // ══════════════════════════════════════════════════

    public void setConexion(java.sql.Connection con) { /* gestionado por BBDD */ }
    public void setUsuario(String user) { this.usuarioLogueado = user; }

    // ══════════════════════════════════════════════════
    //  PREPARAR PARTIDA
    // ══════════════════════════════════════════════════

    public void prepararPartidaPersonalizada(modelo.partida.Partida p) {
        gestorPartida.setPartida(p);
        construirRosterVisual();
        actualizarInventarioVisual();
        actualizarLabelTurno();
        
        // Ejecutamos en el siguiente pulso para asegurar que el layout inicial esté listo
        javafx.application.Platform.runLater(() -> {
            actualizarTodoVisually();
        });
    }

    public void cargarPartidaEspecifica(int idPartida) {
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        java.sql.Connection con = controlador.gestionbbdd.BBDD.conectarPredeterminado();
        modelo.partida.Partida p = helper.cargarBBDD(con, idPartida);
        if (p != null) prepararPartidaPersonalizada(p);
    }

    // ══════════════════════════════════════════════════
    //  ROSTER VISUAL (panel inferior de jugadores)
    // ══════════════════════════════════════════════════

    private void construirRosterVisual() {
        if (rosterContainer == null) return;
        rosterContainer.getChildren().clear();
        iceCubesMap = new HashMap<>();

        for (Jugador j : gestorPartida.getPartida().getJugadores()) {
            VBox cube = new VBox(5);
            cube.getStyleClass().add("ice-cube");

            ImageView img = new ImageView(new Image("/resources/images/skins/" + j.getSkin()));
            img.setFitWidth(50); img.setFitHeight(50); img.setPreserveRatio(true);

            Label nameLabel = new Label(j.getNombre());
            nameLabel.getStyleClass().add("ice-cube-label");

            cube.getChildren().addAll(img, nameLabel);
            iceCubesMap.put(j, cube);
            rosterContainer.getChildren().add(cube);
        }
    }

    // ══════════════════════════════════════════════════
    //  CONSTRUCCIÓN DEL TABLERO VISUAL
    // ══════════════════════════════════════════════════

    private void construirTableroVisual() {
        // Eliminar casillas antiguas pero conservar las fichas de jugadores
        tablero.getChildren().removeIf(node -> !(node instanceof ImageView));

        ArrayList<Casilla> casillas = gestorPartida.getPartida().getTablero().getCasillas();
        int maxPos = gestorPartida.getPartida().getTablero().getTamaño();

        double boardW = tablero.getWidth();
        double boardH = tablero.getHeight();
        double scale = Math.min(boardW / IMG_W, boardH / IMG_H);
        double scaledSize = CELL_SIZE * scale;

        for (int i = 0; i < maxPos; i++) {
            StackPane cell = new StackPane();
            cell.getStyleClass().add("board-cell");
            cell.setPrefSize(scaledSize, scaledSize);
            cell.setMaxSize(scaledSize, scaledSize);
            cell.setMinSize(scaledSize, scaledSize);

            // Determinar tipo de casilla
            String tipo = "Normal";
            if      (i == 0)          tipo = "Inicio";
            else if (i == maxPos - 1) tipo = "Final";
            else {
                for (Casilla c : casillas) {
                    if (c.getPosicion() == i) { tipo = c.getClass().getSimpleName(); break; }
                }
            }

            // Imagen de la casilla
            String imgFile;
            switch (tipo) {
                case "MotoNieve":       imgFile = "casilla_motonieve.png"; break;
                case "Oso":             imgFile = "casilla_oso.png";       break;
                case "Agujero":
                case "SueloQuebradizo": imgFile = "casilla_agujero.png";   break;
                case "Trineo":          imgFile = "casilla_trineo.png";    break;
                case "Evento":          imgFile = "casilla_evento.png";    break;
                default:                imgFile = "casilla_normal.png";    break;
            }

            ImageView iv = crearImagenCasilla(imgFile, scaledSize);
            if (iv != null) cell.getChildren().add(iv);

            if (tipo.equals("Inicio")) {
                Text t = new Text("START");
                t.getStyleClass().add("start-title");
                t.setStyle("-fx-font-size: " + (14 * scale) + "px;");
                cell.getChildren().add(t);
            } else if (tipo.equals("Final")) {
                Text t = new Text("META");
                t.getStyleClass().add("finish-title");
                t.setStyle("-fx-font-size: " + (14 * scale) + "px;");
                cell.getChildren().add(t);
            }

            // Posición absoluta sobre la pasarela
            posicionarEnCasilla(cell, i, CELL_SIZE);

            // Guardar referencia para redimensionado
            celdasVisuales.add(cell);

            // Añadir al tablero - nos aseguramos de que estén por debajo de los pingüinos
            tablero.getChildren().add(0, cell);
        }

        // Asignar skins a las fichas y traerlas al frente
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        int pingIdx = 0, focaIdx = 0;
        for (Jugador j : jugadores) {
            if (j instanceof Pinguino) {
                fichasPinguinos.get(pingIdx).setImage(new Image("/resources/images/skins/" + j.getSkin()));
                fichasPinguinos.get(pingIdx).setVisible(true);
                pingIdx++;
            } else {
                fichasFocas.get(focaIdx).setImage(new Image("/resources/images/skins/" + j.getSkin()));
                fichasFocas.get(focaIdx).setVisible(true);
                focaIdx++;
            }
        }
        for (ImageView iv : fichasPinguinos) { iv.toFront(); iv.setMouseTransparent(true); }
        for (ImageView iv : fichasFocas)     { iv.toFront(); iv.setMouseTransparent(true); }
    }

    private ImageView crearImagenCasilla(String fileName, double size) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/resources/images/casillas/" + fileName));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) { return null; }
    }

    // ══════════════════════════════════════════════════
    //  ACTUALIZAR POSICIONES DE FICHAS
    // ══════════════════════════════════════════════════

    private void actualizarPosicionesVisuales() {
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        int pIdx = 0, fIdx = 0, tokenOffset = 0;
        for (Jugador j : jugadores) {
            ImageView ficha = (j instanceof Pinguino)
                    ? fichasPinguinos.get(pIdx++)
                    : fichasFocas.get(fIdx++);
            posicionarFicha(ficha, j.getPosicion(), tokenOffset++);
            ficha.setTranslateX(0);
            ficha.setTranslateY(0);
            ficha.toFront();
        }
    }

    private ImageView getFichaDeJugador(Jugador j) {
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        int pIdx = 0, fIdx = 0;
        for (Jugador item : jugadores) {
            if (item == j) return (j instanceof Pinguino)
                    ? fichasPinguinos.get(pIdx)
                    : fichasFocas.get(fIdx);
            if (item instanceof Pinguino) pIdx++; else fIdx++;
        }
        return null;
    }

    // ══════════════════════════════════════════════════
    //  INVENTARIO Y TURNO
    // ══════════════════════════════════════════════════

    private void actualizarInventarioVisual() {
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        if (actual instanceof Pinguino) {
            modelo.items.Inventario inv = ((Pinguino) actual).getInventario();
            inventoryTitle.setText("🎒  Mochila de " + actual.getNombre());
            rapido_t.setText("D. Rápido: " + inv.contarPorTipo("Dado Rapido"));
            lento_t.setText("D. Lento: "  + inv.contarPorTipo("Dado Lento"));
            peces_t.setText("Peces: "    + inv.contarPorTipo("Pez"));
            nieve_t.setText("Bolas: "    + inv.contarPorTipo("Bola de Nieve"));
            moto_t.setText("Moto: "     + inv.contarPorTipo("Moto de Nieve"));
        } else {
            inventoryTitle.setText("🦭  Turno de la IA");
            rapido_t.setText(""); lento_t.setText(""); peces_t.setText("");
            nieve_t.setText(""); moto_t.setText("");
        }
    }

    private void actualizarLabelTurno() {
        if (!gestorPartida.getPartida().isFinalizada()) {
            Jugador jActual = gestorPartida.getPartida().getJugadorActual();
            if (iceCubesMap != null) {
                for (Map.Entry<Jugador, VBox> entry : iceCubesMap.entrySet()) {
                    entry.getValue().getStyleClass().remove("ice-cube-active");
                    if (entry.getKey() == jActual)
                        entry.getValue().getStyleClass().add("ice-cube-active");
                }
            }
        }
    }

    // ══════════════════════════════════════════════════
    //  LÓGICA DE DADO Y MOVIMIENTO
    // ══════════════════════════════════════════════════

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
        appendLog(j, "avanza " + avance + " casillas...");

        animarPasoAPaso(j, posFisicaInicio, avance, () -> {
            j.setPosicion(posFisicaInicio);

            int targetPos = Math.min(posFisicaInicio + avance,
                    gestorPartida.getPartida().getTablero().getTamaño() - 1);
            comprobarSobornoEnCasilla(j, targetPos);

            String log = gestorPartida.procesarTurnoConAvance(j, avance);
            if (log != null && !log.isEmpty()) appendLog(j, log.trim());
            concluirTurno();
        });
    }

    // ══════════════════════════════════════════════════
    //  ANIMACIÓN PASO A PASO SOBRE LA PASARELA
    // ══════════════════════════════════════════════════

    private void animarPasoAPaso(Jugador j, int posActual, int pasosRestantes, Runnable onFinish) {
        if (pasosRestantes <= 0) { onFinish.run(); return; }

        int maxPos = gestorPartida.getPartida().getTablero().getTamaño() - 1;
        if (j.getPosicion() >= maxPos) { onFinish.run(); return; }

        int posNueva = j.getPosicion() + 1;
        ImageView ficha = getFichaDeJugador(j);
        if (ficha == null) { onFinish.run(); return; }

        // Posición visual actual (layout + translate)
        double startX = ficha.getLayoutX() + ficha.getTranslateX();
        double startY = ficha.getLayoutY() + ficha.getTranslateY();

        double boardW = tablero.getWidth();
        double boardH = tablero.getHeight();
        double scale = Math.min(boardW / IMG_W, boardH / IMG_H);
        double scaledTokenSize = TOKEN_SIZE * scale;

        // Destino en coordenadas del pane
        double[] target = imageToPaneCoords(PATH_IMG[posNueva][0], PATH_IMG[posNueva][1]);
        double newLayoutX = target[0] - scaledTokenSize / 2.0;
        double newLayoutY = target[1] - scaledTokenSize / 2.0;

        // Mover lógicamente y reposicionar la ficha de forma invisible en el destino
        j.setPosicion(posNueva);
        ficha.setLayoutX(newLayoutX);
        ficha.setLayoutY(newLayoutY);
        // Desplazarse visualmente de vuelta al origen para animar
        ficha.setTranslateX(startX - newLayoutX);
        ficha.setTranslateY(startY - newLayoutY);

        // Animar hasta la posición destino
        TranslateTransition tt = new TranslateTransition(Duration.millis(280), ficha);
        tt.setToX(0); tt.setToY(0);
        tt.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
        tt.setOnFinished(e -> animarPasoAPaso(j, posNueva, pasosRestantes - 1, onFinish));
        tt.play();
        ficha.toFront();
    }

    // ══════════════════════════════════════════════════
    //  SOBORNO / INTERACCIÓN FOCA
    // ══════════════════════════════════════════════════

    private void comprobarSobornoEnCasilla(Jugador j, int targetPos) {
        Pinguino pTarget = null;
        modelo.jugador.Foca fTarget = null;

        if (j instanceof Pinguino) {
            pTarget = (Pinguino) j;
            for (Jugador item : gestorPartida.getPartida().getJugadores()) {
                if (item instanceof modelo.jugador.Foca && item.getPosicion() == targetPos) {
                    fTarget = (modelo.jugador.Foca) item; break;
                }
            }
        } else if (j instanceof modelo.jugador.Foca) {
            fTarget = (modelo.jugador.Foca) j;
            for (Jugador item : gestorPartida.getPartida().getJugadores()) {
                if (item instanceof Pinguino && item.getPosicion() == targetPos) {
                    pTarget = (Pinguino) item; break;
                }
            }
        }

        if (pTarget != null && fTarget != null && !fTarget.isSoborno()) {
            int nPeces = pTarget.getInventario().contarPorTipo("Pez");
            if (nPeces > 0) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("¡Interacción con la Foca!");
                alert.setHeaderText(null);
                alert.setContentText(pTarget.getNombre() + " ¡tienes un pescado! ¿Quieres sobornar a la foca?");

                javafx.scene.control.ButtonType btnSi = new javafx.scene.control.ButtonType("Sí (Sobornar)");
                javafx.scene.control.ButtonType btnNo = new javafx.scene.control.ButtonType("No");
                alert.getButtonTypes().setAll(btnSi, btnNo);

                java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnSi) {
                    gestorPartida.getGestorJugador().jugadorUsaItem(pTarget, "Pez");
                    fTarget.setSoborno(true);
                    fTarget.setTurnosBloqueada(2);
                    appendLog(pTarget, "soborna a la foca con su pescado. 🐟");
                }
            }
        }
    }

    // ══════════════════════════════════════════════════
    //  CONCLUSIÓN DE TURNO Y ANIMACIÓN
    // ══════════════════════════════════════════════════

    private void concluirTurno() {
        actualizarPosicionesVisuales();
        if (gestorPartida.getPartida().isFinalizada()) {
            mostrarFinDePartida();
        } else {
            gestorPartida.avanzarTurno();
            Jugador jNuevo = gestorPartida.getPartida().getJugadorActual();
            mostrarAnimacionTurno(jNuevo, () -> {
                actualizarLabelTurno();
                actualizarInventarioVisual();
                if (jNuevo instanceof modelo.jugador.Foca) jugarTurnoFoca();
            });
        }
    }

    private void mostrarAnimacionTurno(Jugador j, Runnable onFinish) {
        if (turnAnimationOverlay == null) { onFinish.run(); return; }

        turnAnimationText.setText("¡Es el turno de " + j.getNombre() + "!");
        turnAnimationSkin.setImage(new Image("/resources/images/skins/" + j.getSkin()));
        turnAnimationOverlay.setVisible(true);
        turnAnimationOverlay.toFront();

        ScaleTransition stIn = new ScaleTransition(Duration.millis(400), turnAnimationSkin);
        stIn.setFromX(0); stIn.setFromY(0);
        stIn.setToX(1.3); stIn.setToY(1.3);
        stIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        javafx.animation.TranslateTransition ttJump =
                new javafx.animation.TranslateTransition(Duration.millis(250), turnAnimationSkin);
        ttJump.setByY(-60); ttJump.setAutoReverse(true); ttJump.setCycleCount(4);

        ScaleTransition stBounce = new ScaleTransition(Duration.millis(250), turnAnimationSkin);
        stBounce.setToX(1.1); stBounce.setToY(1.1);
        stBounce.setAutoReverse(true); stBounce.setCycleCount(4);

        stIn.setOnFinished(e -> { ttJump.play(); stBounce.play(); });
        ttJump.setOnFinished(e -> {
            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(Duration.millis(600));
            pause.setOnFinished(ev -> { turnAnimationOverlay.setVisible(false); onFinish.run(); });
            pause.play();
        });
        stIn.play();
    }

    private void jugarTurnoFoca() {
        modelo.jugador.Foca foca = (modelo.jugador.Foca) gestorPartida.getPartida().getJugadorActual();
        appendLog(foca, "Turno de la IA...");
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override public void run() {
                javafx.application.Platform.runLater(() -> {
                    int avance = gestorPartida.tirarDado(foca);
                    dadoResultText.setText("Foca tira: " + avance);
                    appendLog(foca, "tira el dado: " + avance);
                    iniciarMovimientoAnimado(foca, avance);
                });
            }
        }, 1000);
    }

    private void mostrarFinDePartida() {
        Jugador ganador = gestorPartida.getPartida().getGanador();
        appendLog(ganador, "🏆 ¡HA GANADO LA PARTIDA! 🎉");
        dado.setDisable(true);
    }

    // ══════════════════════════════════════════════════
    //  ACCIONES DE MENÚ
    // ══════════════════════════════════════════════════

    @FXML private void handleSaveGame() {
        controlador.gestionbbdd.BBDD helper = new controlador.gestionbbdd.BBDD();
        helper.guardarBBDD(controlador.gestionbbdd.BBDD.conectarPredeterminado(),
                gestorPartida.getPartida(), usuarioLogueado);
        appendLog(null, "✅ Partida guardada correctamente.");
    }

    @FXML private void handleLoadGame() { toggleMenu(); handleGoToMenu(); }
    @FXML private void handleNewGame()  { toggleMenu(); handleGoToMenu(); }
    @FXML private void handleQuitGame() { System.exit(0); }

    @FXML private void handleGoToMenu() {
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/resources/fxml/PantallaMenu.fxml"));
            Scene s = new Scene(l.load());
            Stage st = (Stage) tablero.getScene().getWindow();
            st.setScene(s); st.setFullScreen(true); st.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void toggleMenu() {
        menuOverlay.setVisible(!menuOverlay.isVisible());
        menuOverlay.setManaged(menuOverlay.isVisible());
    }

    // ══════════════════════════════════════════════════
    //  ÍTEMS DEL INVENTARIO
    // ══════════════════════════════════════════════════

    @FXML private void handleRapido() { usarItemEnTurno("Dado Rapido"); }
    @FXML private void handleLento()  { usarItemEnTurno("Dado Lento");  }
    @FXML private void handlePeces()  { usarItemEnTurno("Pez");         }
    @FXML private void handleNieve()  { usarItemEnTurno("Bola de Nieve"); }
    @FXML private void handleMoto()   { usarItemEnTurno("Moto de Nieve"); }

    private void usarItemEnTurno(String nombreItem) {
        if (gestorPartida.getPartida().isFinalizada()) return;
        Jugador actual = gestorPartida.getPartida().getJugadorActual();
        if (!(actual instanceof Pinguino)) return;

        Pinguino p = (Pinguino) actual;
        int posPrevia = p.getPosicion();
        gestorPartida.getGestorJugador().jugadorUsaItem(p, nombreItem);

        if (p.getPosicion() != posPrevia) {
            javafx.application.Platform.runLater(this::actualizarPosicionesVisuales);
            appendLog(p, "ha usado " + nombreItem + "!");
        }
        actualizarInventarioVisual();
    }
}