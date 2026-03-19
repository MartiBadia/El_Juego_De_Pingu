package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

public class Trineo extends Casilla {
    private int posicionSiguienteTrineo; // Posición del siguiente trineo al que te envía

    // Constructor
    public Trineo(int pos) {
        this.posicion = pos;
        this.posicionSiguienteTrineo = -1;
    }

    // Constructor con posición del siguiente trineo
    public Trineo(int pos, int posicionSiguienteTrineo) {
        this.posicion = pos;
        this.posicionSiguienteTrineo = posicionSiguienteTrineo;
    }

    // --- GETTER ---
    public int getPosicionSiguienteTrineo() {
        return posicionSiguienteTrineo;
    }

    // --- SETTER ---
    public void setPosicionSiguienteTrineo(int posicionSiguienteTrineo) {
        this.posicionSiguienteTrineo = posicionSiguienteTrineo;
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        if (posicionSiguienteTrineo != -1) {
            j.setPosicion(posicionSiguienteTrineo);
        }
    }

    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        if (posicionSiguienteTrineo != -1) {
            realizarAccion(p, j);
            return "🛷 ¡Trineo! Te deslizas hasta la casilla " + posicionSiguienteTrineo + ".";
        }
        return "🛷 Trineo (último del tablero). No hay siguiente trineo.";
    }

    @Override
    public String toString() {
        return "Casilla Trineo en posicion " + posicion
                + (posicionSiguienteTrineo != -1 ? " -> siguiente trineo: " + posicionSiguienteTrineo : " (ultimo trineo)");
    }
}