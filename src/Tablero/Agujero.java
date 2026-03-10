package tablero;

import jugador.Jugador;
import partida.Partida;

public class Agujero extends Casilla {
    private int turnosAtrapado; // Número de turnos que el jugador queda atrapado
    private boolean esPrimero;  // Si es el primer agujero, el jugador va al inicio del tablero

    // Constructor
    public Agujero(int pos) {
        this.posicion = pos;
        this.turnosAtrapado = 1; // Por defecto 1 turno atrapado
        this.esPrimero = false;
    }

    // Constructor con parámetros completos
    public Agujero(int pos, boolean esPrimero) {
        this.posicion = pos;
        this.turnosAtrapado = 1;
        this.esPrimero = esPrimero;
    }

    // --- GETTERS ---
    public int getTurnosAtrapado() {
        return turnosAtrapado;
    }

    public boolean isEsPrimero() {
        return esPrimero;
    }

    // --- SETTERS ---
    public void setTurnosAtrapado(int turnosAtrapado) {
        this.turnosAtrapado = turnosAtrapado;
    }

    public void setEsPrimero(boolean esPrimero) {
        this.esPrimero = esPrimero;
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        // Si es el primer agujero -> vuelve al inicio del tablero (posición 0)
        // Si no -> el jugador queda atrapado turnosAtrapado turnos
        // ???????? No info: no está claro si la lógica de congelado se gestiona aquí o en GestorPartida
    }
}