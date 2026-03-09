package partida;

import gestionbbdd.BBDD;
import jugador.Jugador;
import tablero.Tablero;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GestorPartida {
    private Partida partida;
    private GestorTablero gestorTablero;
    private GestorJugador gestorJugador;
    private BBDD bbdd;
    private Connection con; // Conexión activa a la BBDD
    private Random random;

    // Constructor: inicializa gestores internos y conecta a la BBDD
    public GestorPartida(Scanner scan) {
        this.random        = new Random();
        this.bbdd          = new BBDD();
        this.gestorTablero = new GestorTablero();
        this.gestorJugador = new GestorJugador();
        // Conectar a la base de datos al arrancar el gestor
        this.con = BBDD.conectarBaseDatos(scan);
    }

    // Constructor sin conexión (para tests o entornos sin BBDD)
    public GestorPartida() {
        this.random        = new Random();
        this.bbdd          = new BBDD();
        this.gestorTablero = new GestorTablero();
        this.gestorJugador = new GestorJugador();
        this.con           = null;
    }

    // --- Getters / Setters ---

    public Partida getPartida() {
        return this.partida;
    }

    public Connection getConexion() {
        return this.con;
    }

    public void setConexion(Connection con) {
        this.con = con;
    }

    // --- Gestión de partida ---

    public void nuevaPartida(ArrayList<Jugador> jugadores, Tablero tablero) {
        this.partida = new Partida(tablero, jugadores);
    }

    public int tirarDado(Jugador j) {
        return random.nextInt(6) + 1;
    }

    /**
     * Ejecuta el turno completo de todos los jugadores:
     * mueve, aplica la casilla y comprueba si alguien ha ganado.
     */
    public void ejecutarTurnoCompleto() {
        for (Jugador j : partida.getJugadores()) {
            if (!partida.isFinalizada()) {
                procesarTurnoJugador(j);
            }
        }
        partida.setTurnos(partida.getTurnos() + 1);
    }

    /**
     * Procesa el turno de un jugador concreto:
     * 1. Tira el dado y mueve al jugador
     * 2. Ejecuta la casilla donde ha caído
     * 3. Comprueba si la partida ha terminado
     */
    public void procesarTurnoJugador(Jugador j) {
        int avance = tirarDado(j);
        j.moverPosicion(avance);
        System.out.println(j.getNombre() + " avanza " + avance + " -> casilla " + j.getPosicion());

        // Ejecutar efecto de la casilla
        gestorTablero.ejecutarCasilla(partida, j);

        // Comprobar si alguien llegó al final
        gestorTablero.comprobarFinTurno(partida);
    }

    /**
     * Avanza al siguiente jugador (modo multijugador – Nivel INTERMIG).
     */
    public void siguienteTurno() {
        int actual = partida.getJugadorActual().equals(
                partida.getJugadores().get(partida.getJugadores().size() - 1))
                ? 0
                : partida.getJugadores().indexOf(partida.getJugadorActual()) + 1;
        partida.setJugadorActual(actual);
    }

    public void actualizarEstadoTablero() {
        gestorTablero.comprobarFinTurno(partida);
    }

    // --- BBDD: guardar y cargar ---

    /**
     * Guarda la partida actual en la base de datos.
     * Usa la conexión almacenada en este gestor.
     */
    public void guardarPartida() {
        if (con == null) {
            System.out.println("No hay conexión activa. Conecta antes con setConexion().");
            return;
        }
        bbdd.guardarBBDD(con, this.partida);
    }

    /**
     * Carga una partida desde la base de datos por ID.
     * Sustituye la partida actual.
     */
    public void cargaPartida(int id) {
        if (con == null) {
            System.out.println("No hay conexión activa. Conecta antes con setConexion().");
            return;
        }
        Partida cargada = bbdd.cargarBBDD(con, id);
        if (cargada != null) {
            this.partida = cargada;
        }
    }

    /**
     * Cierra la conexión con la BBDD al terminar la sesión.
     */
    public void cerrarConexion() {
        BBDD.cerrar(con);
        this.con = null;
    }
}