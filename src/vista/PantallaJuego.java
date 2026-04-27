package vista;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
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
    private static final double CELL_SIZE  = 50.0;
    private static final double TOKEN_SIZE = 45.0;

    /**
     * Coordenadas del CENTRO de cada una de las 50 casillas
     * en el espacio de la imagen original (1390×780).
     *
     * en el espacio de la imagen original.
     */
    private static final double[][] PATH_IMG = {
    	//   1          2           3           4           5           6           7           8           9          10
        {200, 192}, {286, 189}, {371, 194}, {455, 210}, {540, 217}, {625, 208}, {710, 190}, {796, 190}, {880, 245}, {880, 324},
        //   11         12          13          14          15          16          17          18          19         20
        {798, 380}, {714, 388}, {629, 390}, {545, 376}, {461, 359}, {376, 342}, {291, 325}, {200, 340}, {130, 393}, {129, 468},
        //   21         22          23          24          25          26          27          28          29         30
        {195, 528}, {275, 542}, {360, 542}, {446, 534}, {531, 525}, {617, 521}, {702, 510}, {805, 520}, {870, 560}, {883, 625},
        //   31         32          33          34          35          36          37          38          39         40
        {809, 683}, {727, 694}, {641, 689}, {556, 677}, {470, 679}, {385, 684}, {299, 681}, {216, 687}, {145, 728}, {162, 797},
        //   41         42          43          44          45          46          47          48          49         50
        {236, 831}, {322, 841}, {407, 845}, {493, 838}, {579, 840}, {664, 854}, {749, 861}, {835, 863}, {920, 860}, {1000, 840}
    };

    private ArrayList<ImageView> fichasPinguinos;
    private ArrayList<ImageView> fichasFocas;	
    private Map<Jugador, VBox> iceCubesMap;
    private Map<Jugador, Map<String, Label>> rosterLabelsMap;

    // ══════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ══════════════════════════════════════════════════

    @FXML
    private void initialize() {
        appendLog(null, "🐧 ¡Bienvenido a El Juego de Pingu!");

        fichasPinguinos = new ArrayList<>();
        fichasPinguinos.add(P1); fichasPinguinos.add(P2);
        fichasPinguinos.add(P3); fichasPinguinos.add(P4);

        fichasFocas = new ArrayList<>();
        fichasFocas.add(PFoca); fichasFocas.add(PFoca2);
        fichasFocas.add(PFoca3); fichasFocas.add(PFoca4);

        for (ImageView iv : fichasPinguinos) iv.setVisible(false);
        for (ImageView iv : fichasFocas)    iv.setVisible(false);

        gestorPartida = new GestorPartida();

        // RE-POSICIONAMIENTO RESPONSIVO:
        // Si el tamaño del tablero cambia (ventana <-> pantalla completa), actualizamos todo.
        tablero.widthProperty().addListener((obs, oldVal, newVal) -> actualizarTodoVisually());
        tablero.heightProperty().addListener((obs, oldVal, newVal) -> actualizarTodoVisually());
    }

    private void actualizarTodoVisually() {
        if (gestorPartida == null || gestorPartida.getPartida() == null) return;
        
        // Reposicionar casillas
        for (Node node : tablero.getChildren()) {
            if (node.getStyleClass().contains("board-cell")) {
                // El ID o el orden determina el índice de la casilla
                // En nuestro caso, las casillas se crearon en orden, pero es mejor usar el orden inverso 
                // ya que se añadieron con add(0, cell)
            }
        }
        // Para mayor robustez, simplemente reconstruimos la capa visual si cambia el tamaño
        // o mejor, iteramos y re-posicionamos.
        
        // La forma más segura y limpia es reconstruir el mapeo
        construirTableroVisual();
        actualizarPosicionesVisuales();
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

        // Lógica "contain": la imagen se escala al tamaño máximo posible sin recortarse
        double scale = Math.min(boardW / IMG_W, boardH / IMG_H);

        // Alineación "center center" (sincronizado con el CSS)
        // Calculamos el espacio sobrante a los lados (X) y arriba/abajo (Y) para centrar
        double imgOffX = (boardW - IMG_W * scale) / 2.0;
        double imgOffY = (boardH - IMG_H * scale) / 2.0;

        double bpX = imgX * scale + imgOffX;
        double bpY = imgY * scale + imgOffY;

        return new double[]{ bpX, bpY };
    }

    /** Posiciona cualquier nodo en el centro de la casilla i. */
    private void posicionarEnCasilla(Node node, int i, double nodeSize) {
        double[] pos = imageToPaneCoords(PATH_IMG[i][0], PATH_IMG[i][1]);
        node.setLayoutX(pos[0] - nodeSize / 2.0);
        node.setLayoutY(pos[1] - nodeSize / 2.0);
    }

    /** Posiciona una ficha en su casilla con una mejor separación entre jugadores. */
    private void posicionarFicha(ImageView ficha, int cellPos, int tokenOffset) {
        int safePos = Math.min(cellPos, PATH_IMG.length - 1);
        double[] pos = imageToPaneCoords(PATH_IMG[safePos][0], PATH_IMG[safePos][1]);

        // Lógica de separación: distribuimos las fichas en una pequeña cuadrícula/cruz
        double offsetX = 0;
        double offsetY = 0;
        double spread = 8.0; // Distancia de separación mínima

        switch (tokenOffset % 4) {
            case 0: offsetX = -spread; offsetY = -spread/1.5; break;
            case 1: offsetX = spread;  offsetY = -spread/1.5; break;
            case 2: offsetX = -spread; offsetY = spread/1.5;  break;
            case 3: offsetX = spread;  offsetY = spread/1.5;  break;
        }

        ficha.setFitWidth(TOKEN_SIZE);
        ficha.setFitHeight(TOKEN_SIZE);
        ficha.setLayoutX(pos[0] - TOKEN_SIZE / 2.0 + offsetX);
        ficha.setLayoutY(pos[1] - TOKEN_SIZE / 2.0 + offsetY);
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
        if (j instanceof modelo.jugador.Foca) return "#ff1744"; // Foca = Rojo brillante
        String color = j.getColor() != null ? j.getColor().toLowerCase() : "";
        if (j.getNombre().toLowerCase().contains("1") || color.contains("azul"))    return "#00e5ff";
        if (j.getNombre().toLowerCase().contains("2") || color.contains("rojo"))    return "#ff1744";
        switch (color) {
            case "verde":    return "#00ffa3";
            case "amarillo": return "#ffd740";
            default:         return "#ffffff";
        }
    }

    private void aplicarEfectoBrillo(ImageView iv, Jugador j) {
        String hex = getPlayerColorHex(j);
        javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow();
        // Reducimos la opacidad para que no sea tan luminoso
        ds.setColor(Color.web(hex).deriveColor(0, 1, 1, 0.6));
        ds.setRadius(10);
        ds.setSpread(0.3);
        iv.setEffect(ds);
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
        rosterLabelsMap = new HashMap<>();

        for (Jugador j : gestorPartida.getPartida().getJugadores()) {
            // Contenedor principal: VBox vertical
            VBox card = new VBox(12);
            card.getStyleClass().add("ice-cube");
            card.setMinWidth(200);
            card.setAlignment(javafx.geometry.Pos.CENTER);

            // FILA SUPERIOR: HBox para Skin (Izquierda) e Inventario (Derecha)
            javafx.scene.layout.HBox topRow = new javafx.scene.layout.HBox(15);
            topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // 1. Skin del Jugador
            ImageView img = new ImageView(new Image("/resources/images/skins/" + j.getSkin()));
            img.setFitWidth(55); img.setFitHeight(55); img.setPreserveRatio(true);

            // 2. Rejilla de mini-inventario (A LA DERECHA de la skin)
            GridPane miniInv = new GridPane();
            miniInv.setHgap(10); miniInv.setVgap(4);
            miniInv.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Map<String, Label> jLabels = new HashMap<>();
            miniInv.add(crearMiniEtiqueta("🐟", "Pez", jLabels), 0, 0);
            miniInv.add(crearMiniEtiqueta("❄️", "Bola de Nieve", jLabels), 1, 0);
            miniInv.add(crearMiniEtiqueta("🏎️", "Moto de Nieve", jLabels), 0, 1);
            miniInv.add(crearMiniEtiqueta("⏩", "Dado Rapido", jLabels), 1, 1);
            miniInv.add(crearMiniEtiqueta("⏪", "Dado Lento", jLabels), 0, 2);

            topRow.getChildren().addAll(img, miniInv);

            // SECCIÓN INFERIOR: Nombre centrado
            Label nameLabel = new Label(j.getNombre().toUpperCase());
            nameLabel.getStyleClass().add("ice-cube-label");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setAlignment(javafx.geometry.Pos.CENTER);

            card.getChildren().addAll(topRow, nameLabel);

            iceCubesMap.put(j, card);
            rosterLabelsMap.put(j, jLabels);
            rosterContainer.getChildren().add(card);
        }
        actualizarRosterInventarios();
    }

    private Label crearMiniEtiqueta(String icono, String tipo, Map<String, Label> map) {
        Label l = new Label(icono + " 0");
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #a8c8e8; -fx-font-weight: bold;");
        map.put(tipo, l);
        return l;
    }

    private void actualizarRosterInventarios() {
        if (rosterLabelsMap == null) return;
        for (Jugador j : gestorPartida.getPartida().getJugadores()) {
            if (j instanceof Pinguino) {
                Map<String, Label> labels = rosterLabelsMap.get(j);
                if (labels != null) {
                    modelo.items.Inventario inv = ((Pinguino) j).getInventario();
                    labels.get("Pez").setText("🐟 " + inv.contarPorTipo("Pez"));
                    labels.get("Bola de Nieve").setText("❄️ " + inv.contarPorTipo("Bola de Nieve"));
                    labels.get("Moto de Nieve").setText("🏎️ " + inv.contarPorTipo("Moto de Nieve"));
                    labels.get("Dado Rapido").setText("⏩ " + inv.contarPorTipo("Dado Rapido"));
                    labels.get("Dado Lento").setText("⏪ " + inv.contarPorTipo("Dado Lento"));
                }
            }
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

        for (int i = 0; i < maxPos; i++) {
            StackPane cell = new StackPane();
            cell.getStyleClass().add("board-cell");
            cell.setPrefSize(CELL_SIZE, CELL_SIZE);
            cell.setMaxSize(CELL_SIZE, CELL_SIZE);
            cell.setMinSize(CELL_SIZE, CELL_SIZE);

            // Determinar tipo de casilla
            String tipo = "Normal";
            boolean encontrada = false;
            int idxCasilla = 0;
            while (idxCasilla < casillas.size() && !encontrada) {
                Casilla c = casillas.get(idxCasilla);
                if (c.getPosicion() == i) { 
                    tipo = c.getClass().getSimpleName(); 
                    encontrada = true; 
                }
                idxCasilla++;
            }

            // Imagen de la casilla
            String imgFile;
            switch (tipo) {
                case "Oso":             imgFile = "casilla_oso.png";       break;
                case "Agujero":         imgFile = "casilla_agujero.png";    break;
                case "SueloQuebradizo": imgFile = "casilla_agrietada.png";  break;
                case "Trineo":          imgFile = "casilla_trineo.png";     break;
                case "Evento":          imgFile = "casilla_interrogante.png"; break;
                case "CasillaSalida":   imgFile = "casilla_salida.png";     break;
                case "CasillaMeta":     imgFile = "casilla_meta.png";       break;
                default:                imgFile = "casilla_normal.png";     break;
            }

            ImageView iv = crearImagenCasilla(imgFile);
            if (iv != null) cell.getChildren().add(iv);

            // No añadimos texto extra ya que las nuevas imágenes ya son descriptivas

            // Posición absoluta sobre la pasarela
            posicionarEnCasilla(cell, i, CELL_SIZE);

            // Añadir al tablero - nos aseguramos de que estén por debajo de los pingüinos
            tablero.getChildren().add(0, cell);
        }

        // Asignar skins a las fichas y traerlas al frente
        ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
        int pingIdx = 0, focaIdx = 0;
        for (Jugador j : jugadores) {
            if (j instanceof Pinguino) {
                ImageView iv = fichasPinguinos.get(pingIdx);
                iv.setImage(new Image("/resources/images/skins/" + j.getSkin()));
                iv.setVisible(true);
                aplicarEfectoBrillo(iv, j);
                pingIdx++;
            } else {
                ImageView iv = fichasFocas.get(focaIdx);
                iv.setImage(new Image("/resources/images/skins/" + j.getSkin()));
                iv.setVisible(true);
                aplicarEfectoBrillo(iv, j);
                focaIdx++;
            }
        }
        for (ImageView iv : fichasPinguinos) { iv.toFront(); iv.setMouseTransparent(true); }
        for (ImageView iv : fichasFocas)     { iv.toFront(); iv.setMouseTransparent(true); }
    }

    private ImageView crearImagenCasilla(String fileName) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/resources/images/casillas/" + fileName));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(CELL_SIZE);
            iv.setFitHeight(CELL_SIZE);
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
            aplicarEfectoBrillo(ficha, j);
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
        actualizarRosterInventarios();
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

        // Destino en coordenadas del pane
        double[] target = imageToPaneCoords(PATH_IMG[posNueva][0], PATH_IMG[posNueva][1]);
        double newLayoutX = target[0] - TOKEN_SIZE / 2.0;
        double newLayoutY = target[1] - TOKEN_SIZE / 2.0;

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
            ArrayList<Jugador> listaJugadores = gestorPartida.getPartida().getJugadores();
            boolean focaEncontrada = false;
            int idx = 0;
            while (idx < listaJugadores.size() && !focaEncontrada) {
                Jugador item = listaJugadores.get(idx);
                if (item instanceof modelo.jugador.Foca && item.getPosicion() == targetPos) {
                    fTarget = (modelo.jugador.Foca) item; 
                    focaEncontrada = true;
                }
                idx++;
            }
        } else if (j instanceof modelo.jugador.Foca) {
            fTarget = (modelo.jugador.Foca) j;
            ArrayList<Jugador> listaJugadores = gestorPartida.getPartida().getJugadores();
            boolean pinguinoEncontrado = false;
            int idx = 0;
            while (idx < listaJugadores.size() && !pinguinoEncontrado) {
                Jugador item = listaJugadores.get(idx);
                if (item instanceof Pinguino && item.getPosicion() == targetPos) {
                    pTarget = (Pinguino) item; 
                    pinguinoEncontrado = true;
                }
                idx++;
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

        turnAnimationText.setText("¡Turno de " + j.getNombre() + "!");
        turnAnimationSkin.setImage(new Image("/resources/images/skins/" + j.getSkin()));
        
        // Setup inicial transparente y escalado
        turnAnimationOverlay.setOpacity(0);
        turnAnimationOverlay.setVisible(true);
        turnAnimationOverlay.toFront();
        
        turnAnimationSkin.setScaleX(1.8);
        turnAnimationSkin.setScaleY(1.8);
        turnAnimationSkin.setTranslateX(0); // Asegurar que esté centrado
        
        turnAnimationText.setTranslateY(30);
        turnAnimationText.setOpacity(0);

        // 1. Fundido suave del fondo
        javafx.animation.FadeTransition fadeBg = new javafx.animation.FadeTransition(Duration.millis(400), turnAnimationOverlay);
        fadeBg.setToValue(1.0);

        // 2. Zoom out suave (Cinematic Focus)
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(700), turnAnimationSkin);
        zoomOut.setToX(1.0); zoomOut.setToY(1.0);
        zoomOut.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        // 3. Entrada del texto (Slide + Fade)
        javafx.animation.FadeTransition fadeText = new javafx.animation.FadeTransition(Duration.millis(500), turnAnimationText);
        fadeText.setDelay(Duration.millis(200));
        fadeText.setToValue(1.0);
        
        javafx.animation.TranslateTransition slideText = new javafx.animation.TranslateTransition(Duration.millis(500), turnAnimationText);
        slideText.setDelay(Duration.millis(200));
        slideText.setToY(0);
        slideText.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        // 4. Salida elegante
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(1200));
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.millis(400), turnAnimationOverlay);
        
        pause.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> {
            turnAnimationOverlay.setVisible(false);
            onFinish.run();
        });

        // Iniciar orquestación
        fadeBg.play();
        zoomOut.play();
        fadeText.play();
        slideText.play();
        pause.play();
    }

    private void jugarTurnoFoca() {
        modelo.jugador.Foca foca = (modelo.jugador.Foca) gestorPartida.getPartida().getJugadorActual();
        appendLog(foca, "Turno de la IA...");
        
        // Usamos PauseTransition en lugar de Timer para no crear hilos externos (más limpio para primero)
        javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(Duration.seconds(1));
        pausa.setOnFinished(e -> {
            int avance = gestorPartida.tirarDado(foca);
            dadoResultText.setText("Foca tira: " + avance);
            appendLog(foca, "tira el dado: " + avance);
            iniciarMovimientoAnimado(foca, avance);
        });
        pausa.play();
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
            Stage st = (Stage) tablero.getScene().getWindow();
            Scene s = st.getScene();
            s.setRoot(l.load());
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