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
    // Crea el gestor y establece la conexión con la base de datos pidiendo los datos al usuario
    public GestorPartida(Scanner scan) {
        this.random        = new Random();
        this.bbdd          = new BBDD();
        this.gestorTablero = new GestorTablero();
        this.gestorJugador = new GestorJugador();
        // Conectar a la base de datos al arrancar el gestor
        this.con = BBDD.conectarBaseDatos(scan);
    }

    // Constructor sin conexión (para tests o entornos sin BBDD)
    // Constructor por si no queremos o no podemos usar la base de datos (para pruebas rápidas)
    public GestorPartida() {
        this.random        = new Random();
        this.bbdd          = new BBDD();
        this.gestorTablero = new GestorTablero();
        this.gestorJugador = new GestorJugador();
        this.con           = null;
    }

    // --- Getters / Setters ---

    // Devuelve el objeto con todos los datos de la partida actual
    public Partida getPartida() {
        return this.partida;
    }

    // Permite inyectar una partida ya creada (por ejemplo, al cargar una)
    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    // Nos da la conexión activa con la DB
    public Connection getConexion() {
        return this.con;
    }

    // Accede al gestor encargado de los movimientos y estados de los jugadores
    public GestorJugador getGestorJugador() {
        return this.gestorJugador;
    }

    // Cambia el gestor de jugadores por otro
    public void setGestorJugador(GestorJugador gestorJugador) {
        this.gestorJugador = gestorJugador;
    }

    // --- Gestión de partida ---

    // Inicializa una partida nueva con los jugadores y el tablero indicados
    public void nuevaPartida(ArrayList<Jugador> jugadores, Tablero tablero) {
        this.partida = new Partida(tablero, jugadores);
    }

    // Tira un dado de 6 caras para un jugador
    public int tirarDado(Jugador j) {
        return random.nextInt(6) + 1;
    }

    // Lanza un dado (puede ser especial o normal) y devuelve el resultado
    public int iniciarDado(modelo.items.Dado dado, Integer datoOpcional) {
        if (datoOpcional != null) return datoOpcional;
        if (dado != null) return dado.tirarRandom();
        return random.nextInt(6) + 1;
    }

    /**
     * Ejecuta el turno completo de todos los jugadores:
     * mueve, aplica la casilla y comprueba si alguien ha ganado.
     */
    // Hace que todos los jugadores muevan en su turno correspondiente
    public void ejecutarTurnoCompleto() {
        for (Jugador j : partida.getJugadores()) {
            if (!partida.isFinalizada()) {
                procesarTurnoJugador(j);
            }
        }
        partida.setTurnos(partida.getTurnos() + 1);
    }

    
      //Procesa el turno de un jugador concreto:
     
    // Gestiona todo lo que pasa cuando un jugador avanza: bloqueos, choques, robos y la acción de la casilla final
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
                        String msgRobo = foca.aplastarJugador((modelo.jugador.Pinguino) otro);
                        if (!msgRobo.isEmpty()) log.append(msgRobo).append("\n");
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
            if (foca.isSoborno()) return log.toString();
            
            for (Jugador otro : partida.getJugadores()) {
                if (otro instanceof modelo.jugador.Pinguino && otro.getPosicion() == foca.getPosicion()) {
                    log.append(gestorJugador.focaInteractuaPinguino((modelo.jugador.Pinguino) otro, foca, partida.getTablero())).append("\n");
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
                    
                    // Comprobar si tras el desplazamiento alguno cayó con una foca
                    for (Jugador item : partida.getJugadores()) {
                        if (item instanceof modelo.jugador.Foca) {
                            if (item.getPosicion() == p1.getPosicion()) {
                                log.append(gestorJugador.focaInteractuaPinguino(p1, (modelo.jugador.Foca) item, partida.getTablero())).append("\n");
                            }
                            if (item.getPosicion() == otro.getPosicion()) {
                                log.append(gestorJugador.focaInteractuaPinguino((modelo.jugador.Pinguino) otro, (modelo.jugador.Foca) item, partida.getTablero())).append("\n");
                            }
                        }
                    }
                }
            }
            
            // Encuentro con foca directo si no hubo batalla o si la batalla terminó ahí
            for (Jugador f : partida.getJugadores()) {
                if (f instanceof modelo.jugador.Foca && f.getPosicion() == p1.getPosicion()) {
                    log.append(gestorJugador.focaInteractuaPinguino(p1, (modelo.jugador.Foca) f, partida.getTablero())).append("\n");
                }
            }
        }

        int posCae = j.getPosicion();
        modelo.tablero.Casilla c = partida.getTablero().getCasillaEnPosicion(posCae);
        String logCasilla = c.realizarAccionConLog(partida, j);
        if (logCasilla != null && !logCasilla.isEmpty()) {
            log.append(logCasilla).append("\n");
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
    
    // Procesa el turno estándar de un jugador lanzando el dado automáticamente
    public void procesarTurnoJugador(Jugador j) {
        int avance = tirarDado(j);
        procesarTurnoConAvance(j, avance);
    }

    // Turno específico para la foca
    public String procesarTurnoFoca(modelo.jugador.Foca f) {
        int avance = tirarDado(f);
        return procesarTurnoConAvance(f, avance);
    }


    /**
     * Ejecuta la lógica de la casilla actual del jugador y comprueba el fin de turno.
     * Creado específicamente para sincronizar el movimiento visual con la lógica del juego.
     */
    // Ejecuta la lógica de la casilla pero pensada para el flujo visual de la interfaz
    public void ejecutarCasillaVisualmente(Jugador j) {
        System.out.println("Ejecutando lógica de la casilla en posición: " + j.getPosicion());
        gestorTablero.ejecutarCasilla(partida, j);
        gestorTablero.comprobarFinTurno(partida);
        gestorJugador.jugadorFinalizaTurno(j);
    }

    
    // Pasa el turno al siguiente jugador de la lista
    public void siguienteTurno() {
        int actual = (partida.getJugadorActualIndice() + 1) % partida.getJugadores().size();
        partida.setJugadorActual(actual);
        if (actual == 0) {
            partida.setTurnos(partida.getTurnos() + 1);
        }
    }

    // Sinónimo de siguienteTurno
    public void avanzarTurno() {
        siguienteTurno();
    }

    // Revisa si alguien ha ganado y actualiza el estado de la partida
    public void actualizarEstadoTablero() {
        gestorTablero.comprobarFinTurno(partida);
    }



    // Manda los datos de la partida actual a la base de datos para no perder el progreso
    public void guardarPartida(String username) {
        if (con == null) {
            System.out.println("No hay conexión activa. Conecta antes con setConexion().");
            return;
        }
        bbdd.guardarBBDD(con, this.partida, username);
    }


    // Carga una partida guardada previamente en la DB usando su identificador
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
     
    // Desconecta el gestor de la base de datos limpiamente
    public void cerrarConexion() {
        BBDD.cerrar(con);
        this.con = null;
    }
}