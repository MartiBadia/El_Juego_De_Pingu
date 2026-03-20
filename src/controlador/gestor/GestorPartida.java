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

    public void setPartida(Partida partida) {
        this.partida = partida;
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

    
      //Procesa el turno de un jugador concreto:
     
    public String procesarTurnoConAvance(Jugador j, int avance) {
        StringBuilder log = new StringBuilder();
        
        // Lógica de congelación para Pingüinos
        if (j instanceof modelo.jugador.Pinguino) {
            modelo.jugador.Pinguino p = (modelo.jugador.Pinguino) j;
            if (p.getTurnosCongelado() > 0) {
                log.append(p.getNombre() + " está congelado.\n");
                gestorJugador.jugadorFinalizaTurno(p);
                return log.toString();
            }
        }
        
        // Lógica de bloqueo para Focas (CPU)
        if (j instanceof modelo.jugador.Foca) {
            modelo.jugador.Foca f = (modelo.jugador.Foca) j;
            if (f.getTurnosBloqueada() > 0) {
                log.append("La foca " + f.getNombre() + " está bloqueada.\n");
                gestorJugador.jugadorFinalizaTurno(f);
                return log.toString();
            }
        }

        // --- IA de la Foca: Lógica de paso ---
        if (j instanceof modelo.jugador.Foca) {
            modelo.jugador.Foca foca = (modelo.jugador.Foca) j;
            int posInicial = foca.getPosicion();
            int posFinal = posInicial + avance;

            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Pinguino) {
                    if (otro.getPosicion() > posInicial && otro.getPosicion() < posFinal) {
                        log.append("Foca pasó sobre " + otro.getNombre() + "!\n");
                        foca.aplastarJugador((modelo.jugador.Pinguino) otro);
                    }
                }
            }
        }

        int maxPosTablero = partida.getTablero().getTamaño() - 1;
        j.moverPosicion(avance, maxPosTablero);
        log.append(j.getNombre() + " avanzó " + avance + " → casilla " + j.getPosicion() + ".\n");

        // --- IA de la Foca: Lógica al caer coincidiendo ---
        if (j instanceof modelo.jugador.Foca) {
            modelo.jugador.Foca foca = (modelo.jugador.Foca) j;
            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Pinguino && otro.getPosicion() == foca.getPosicion()) {
                    log.append("Foca coincide con " + otro.getNombre() + " y golpea!\n");
                    foca.golpearJugador((modelo.jugador.Pinguino) otro, partida.getTablero());
                }
            }
        }
        
        // Guerra de jugadores
        if (j instanceof modelo.jugador.Pinguino) {
            modelo.jugador.Pinguino p1 = (modelo.jugador.Pinguino) j;
            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Pinguino && otro != p1 && otro.getPosicion() == p1.getPosicion()) {
                    log.append("¡Batalla " + p1.getNombre() + " vs " + otro.getNombre() + "!\n");
                    gestorJugador.pinguinoLuchaPinguino(p1, (modelo.jugador.Pinguino) otro);
                }
            }
            
            // Si cae en foca
            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Foca && otro.getPosicion() == p1.getPosicion()) {
                    log.append("¡Encuentro con Foca!\n");
                    gestorJugador.focaInteractuaPinguino(p1, (modelo.jugador.Foca) otro, partida.getTablero());
                }
            }
        }

        int posCae = j.getPosicion();
        modelo.tablero.Casilla c = partida.getTablero().getCasillaEnPosicion(posCae);
        if (c != null) {
            String logCasilla = c.realizarAccionConLog(partida, j);
            if (logCasilla != null && !logCasilla.isEmpty()) {
                log.append(logCasilla).append("\n");
            }
        }

        // Si la casilla movió al jugador, informamos
        if (j.getPosicion() != posCae) {
            log.append("➡ Ahora estás en la casilla ").append(j.getPosicion()).append(".\n");
        }

        gestorTablero.comprobarFinTurno(partida);
        if (partida.isFinalizada()) {
            log.append("¡LA PARTIDA HA TERMINADO!\n");
        }

        gestorJugador.jugadorFinalizaTurno(j);
        
        return log.toString();
    }
    
    public void procesarTurnoJugador(Jugador j) {
        int avance = tirarDado(j);
        procesarTurnoConAvance(j, avance);
    }

    public String procesarTurnoFoca(modelo.jugador.Foca f) {
        int avance = tirarDado(f);
        return procesarTurnoConAvance(f, avance);
    }


    /**
     * Ejecuta la lógica de la casilla actual del jugador y comprueba el fin de turno.
     * Creado específicamente para sincronizar el movimiento visual con la lógica del juego.
     */
    public void ejecutarCasillaVisualmente(Jugador j) {
        System.out.println("Ejecutando lógica de la casilla en posición: " + j.getPosicion());
        gestorTablero.ejecutarCasilla(partida, j);
        gestorTablero.comprobarFinTurno(partida);
        gestorJugador.jugadorFinalizaTurno(j);
    }

    
    public void siguienteTurno() {
        int actual = (partida.getJugadorActualIndice() + 1) % partida.getJugadores().size();
        partida.setJugadorActual(actual);
        if (actual == 0) {
            partida.setTurnos(partida.getTurnos() + 1);
        }
    }

    public void avanzarTurno() {
        siguienteTurno();
    }

    public void actualizarEstadoTablero() {
        gestorTablero.comprobarFinTurno(partida);
    }



    public void guardarPartida(String username) {
        if (con == null) {
            System.out.println("No hay conexión activa. Conecta antes con setConexion().");
            return;
        }
        bbdd.guardarBBDD(con, this.partida, username);
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

    
     //Cierra la conexión con la BBDD al terminar la sesión.
     
    public void cerrarConexion() {
        BBDD.cerrar(con);
        this.con = null;
    }
}