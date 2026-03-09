package partida;

import jugador.Jugador;
import tablero.Tablero;

import java.util.ArrayList;

public class Partida {
    private Tablero tablero;
    private ArrayList<Jugador> jugadores;
    private int turnos;
    private int jugadorActual;
    private boolean finalizada;
    private Jugador ganador;

    // Constructor básico sin super()
    public Partida(Tablero tablero, ArrayList<Jugador> jugadores) {
        this.tablero = tablero;
        this.jugadores = jugadores;
        this.turnos = 0;
        this.jugadorActual = 0;
        this.finalizada = false;
    }

    // --- GETTERS ---

    // Este es el método que necesitaba el GestorPartida para el bucle for
    public ArrayList<Jugador> getJugadores() {
        return this.jugadores;
    }

    public Jugador getJugadorActual() {
        // Devuelve el objeto Jugador según el índice actual
        return jugadores.get(jugadorActual);
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    public int getTurnos() {
        return this.turnos;
    }

    public boolean isFinalizada() {
        return this.finalizada;
    }

    public Jugador getGanador() {
        return this.ganador;
    }

    // --- SETTERS ---

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public void setJugadores(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public void setTurnos(int turnos) {
        this.turnos = turnos;
    }

    public void setJugadorActual(int jugadorActual) {
        this.jugadorActual = jugadorActual;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    public void setGanador(Jugador ganador) {
        this.ganador = ganador;
    }
}