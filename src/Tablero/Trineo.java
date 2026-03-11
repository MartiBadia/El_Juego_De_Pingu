package tablero;

import jugador.Jugador;
import partida.Partida;

public class Trineo extends Casilla {
    private int posicionSiguienteTrineo; // Posición del siguiente trineo al que te envía

    // Constructor
    public Trineo(int pos) {
        this.posicion = pos;
        this.posicionSiguienteTrineo = -1; // -1 indica que es el último trineo (no pasa nada)
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
        // Según el enunciado: avanza al siguiente trineo del tablero
        // Si ya estás en el último trineo, no pasa nada
        if (posicionSiguienteTrineo != -1) {
            j.setPosicion(posicionSiguienteTrineo);
        }
    }

    @Override
    public String toString() {
        return "Casilla Trineo en posicion " + posicion
                + (posicionSiguienteTrineo != -1 ? " -> siguiente trineo: " + posicionSiguienteTrineo : " (ultimo trineo)");
    }
}