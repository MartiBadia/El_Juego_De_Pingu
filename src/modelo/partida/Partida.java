package modelo.partida;

import modelo.jugador.Jugador;
import modelo.tablero.Tablero;
import java.util.ArrayList;

public class Partida {
    private int idPartida; // ID de la base de datos (0 si es nueva)
    private Tablero tablero;
    private ArrayList<Jugador> jugadores;
    private int turnos;
    private int jugadorActual;
    private boolean finalizada;
    private Jugador ganador;

    public Partida(Tablero tablero, ArrayList<Jugador> jugadores) {
        this.idPartida = 0; 
        this.tablero = tablero;
        this.jugadores = jugadores;
        this.turnos = 0;
        this.jugadorActual = 0;
        this.finalizada = false;
    }

    // --- GETTERS ---
    public int getIdPartida() { return this.idPartida; }
    public ArrayList<Jugador> getJugadores() { return this.jugadores; }
    public Jugador getJugadorActual() { return jugadores.get(jugadorActual); }
    public int getJugadorActualIndice() { return this.jugadorActual; }
    public Tablero getTablero() { return this.tablero; }
    public int getTurnos() { return this.turnos; }
    public boolean isFinalizada() { return this.finalizada; }
    public Jugador getGanador() { return this.ganador; }

    // --- SETTERS ---
    public void setIdPartida(int id) { this.idPartida = id; }
    public void setTablero(Tablero tablero) { this.tablero = tablero; }
    public void setJugadores(ArrayList<Jugador> jugadores) { this.jugadores = jugadores; }
    public void setTurnos(int turnos) { this.turnos = turnos; }
    public void setJugadorActual(int jugadorActual) { this.jugadorActual = jugadorActual; }
    public void setFinalizada(boolean finalizada) { this.finalizada = finalizada; }
    public void setGanador(Jugador ganador) { this.ganador = ganador; }
}