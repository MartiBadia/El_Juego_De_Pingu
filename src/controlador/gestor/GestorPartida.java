package controlador.gestor;

import modelo.jugador.Jugador;
import modelo.partida.Partida;
import modelo.tablero.Tablero;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import controlador.gestionbbdd.BBDD;

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

    public int iniciarDado(modelo.items.Dado dado, Integer datoOpcional) {
        if (datoOpcional != null) return datoOpcional;
        if (dado != null) return dado.tirarRandom();
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
        // Lógica de congelación para Pingüinos
        if (j instanceof modelo.jugador.Pinguino) {
            modelo.jugador.Pinguino p = (modelo.jugador.Pinguino) j;
            if (p.getTurnosCongelado() > 0) {
                System.out.println(p.getNombre() + " está congelado y pierde el turno.");
                gestorJugador.jugadorFinalizaTurno(p);
                return;
            }
        }
        
        // Lógica de bloqueo para Focas (CPU)
        if (j instanceof modelo.jugador.Foca) {
            modelo.jugador.Foca f = (modelo.jugador.Foca) j;
            if (f.getTurnosBloqueada() > 0) {
                System.out.println("La foca " + f.getNombre() + " está bloqueada/sobornada.");
                gestorJugador.jugadorFinalizaTurno(f);
                return;
            }
        }

        int avance = tirarDado(j);
        
        // --- IA de la Foca: Lógica de paso ---
        if (j instanceof modelo.jugador.Foca) {
            modelo.jugador.Foca foca = (modelo.jugador.Foca) j;
            int posInicial = foca.getPosicion();
            int posFinal = posInicial + avance;

            // Si la foca PASA por la casilla de un jugador, le hace perder la mitad del inventario
            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Pinguino) {
                    if (otro.getPosicion() > posInicial && otro.getPosicion() < posFinal) {
                        System.out.println("¡La foca pasa por encima de " + otro.getNombre() + "!");
                        foca.aplastarJugador((modelo.jugador.Pinguino) otro);
                    }
                }
            }
        }

        j.moverPosicion(avance);
        System.out.println(j.getNombre() + " avanza " + avance + " -> casilla " + j.getPosicion());

        // --- IA de la Foca: Lógica al caer coincidiendo ---
        if (j instanceof modelo.jugador.Foca) {
            modelo.jugador.Foca foca = (modelo.jugador.Foca) j;
            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Pinguino && otro.getPosicion() == foca.getPosicion()) {
                    System.out.println("¡La foca coincide con " + otro.getNombre() + " y le da un coletazo!");
                    foca.golpearJugador((modelo.jugador.Pinguino) otro, partida.getTablero());
                }
            }
        }
        
        // Guerra de jugadores (si caen dos pingüinos juntos)
        if (j instanceof modelo.jugador.Pinguino) {
            modelo.jugador.Pinguino p1 = (modelo.jugador.Pinguino) j;
            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Pinguino && otro != p1 && otro.getPosicion() == p1.getPosicion()) {
                    gestorJugador.pinguinoLuchaPinguino(p1, (modelo.jugador.Pinguino) otro);
                }
            }
        }

        // Ejecutar efecto de la casilla
        gestorTablero.ejecutarCasilla(partida, j);

        // Comprobar si alguien llegó al final
        gestorTablero.comprobarFinTurno(partida);

        // Decrementar contadores de estado al finalizar el turno
        gestorJugador.jugadorFinalizaTurno(j);
    }

    
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



    public void guardarPartida() {
        if (con == null) {
            System.out.println("No hay conexión activa. Conecta antes con setConexion().");
            return;
        }
        bbdd.guardarBBDD(con, this.partida);
    }


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