package modelo.partida;

import modelo.jugador.Jugador;
import modelo.tablero.Tablero;
import java.util.ArrayList;

/**
 * Clase contenedora que agrupa todos los elementos de un juego en curso:
 * el tablero, los jugadores, el turno actual y el estado de la partida.
 */
public class Partida {
    private int idPartida; // ID de la base de datos (0 si es nueva)
    private Tablero tablero;
    private ArrayList<Jugador> jugadores;
    private int turnos;
    private int jugadorActual;
    private boolean finalizada;
    private Jugador ganador;

    // Prepara una partida nueva con su tablero y la lista de jugadores
    public Partida(Tablero tablero, ArrayList<Jugador> jugadores) {
        this.idPartida = 0; 
        this.tablero = tablero;
        this.jugadores = jugadores;
        this.turnos = 0;
        this.jugadorActual = 0;
        this.finalizada = false;
    }

    // --- GETTERS ---
    // Nos da el ID de la partida (si se ha guardado en la DB)
    public int getIdPartida() { return this.idPartida; }
    // Devuelve la lista completa de jugadores que están participando
    public ArrayList<Jugador> getJugadores() { return this.jugadores; }
    // Devuelve el objeto Jugador que tiene el turno ahora mismo
    public Jugador getJugadorActual() { return jugadores.get(jugadorActual); }
    // Nos da el índice (0, 1, 2...) del jugador que le toca jugar
    public int getJugadorActualIndice() { return this.jugadorActual; }
    // Accede al tablero donde se está jugando
    public Tablero getTablero() { return this.tablero; }
    // Indica cuántos turnos se han jugado ya
    public int getTurnos() { return this.turnos; }
    // Dice si la partida ha terminado o sigue en marcha
    public boolean isFinalizada() { return this.finalizada; }
    // Nos dice quién ha sido el ganador (si ya hay uno)
    public Jugador getGanador() { return this.ganador; }

    // --- SETTERS ---
    // Asigna un ID a la partida (normalmente al guardarla)
    public void setIdPartida(int id) { this.idPartida = id; }
    // Cambia el tablero de la partida
    public void setTablero(Tablero tablero) { this.tablero = tablero; }
    // Actualiza la lista de jugadores
    public void setJugadores(ArrayList<Jugador> jugadores) { this.jugadores = jugadores; }
    // Cambia el contador de turnos transcurridos
    public void setTurnos(int turnos) { this.turnos = turnos; }
    // Establece a qué jugador le toca mover
    public void setJugadorActual(int jugadorActual) { this.jugadorActual = jugadorActual; }
    // Marca la partida como acabada o no
    public void setFinalizada(boolean finalizada) { this.finalizada = finalizada; }
    // Designa al jugador que ha ganado la partida
    public void setGanador(Jugador ganador) { this.ganador = ganador; }
}