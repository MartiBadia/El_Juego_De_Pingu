package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class Trineo extends Casilla {
    private int posicionSiguienteTrineo; // posicion del siguiente trineo al que te manda

    // Constructor
    public Trineo(int pos) {
        this.posicion = pos;
        this.posicionSiguienteTrineo = -1; // -1 indica que es el ultimo trineo
    }

    // Constructor con posicion del siguiente trineo
    public Trineo(int pos, int posicionSiguienteTrineo) {
        this.posicion = pos;
        this.posicionSiguienteTrineo = posicionSiguienteTrineo;
    }

    // getter
    public int getPosicionSiguienteTrineo() {
        return posicionSiguienteTrineo;
    }

    //setters
    public void setPosicionSiguienteTrineo(int posicionSiguienteTrineo) {
        this.posicionSiguienteTrineo = posicionSiguienteTrineo;
    }

   
    public void realizarAccion(Partida p, Jugador j) {
        // Según el enunciado: avanza al siguiente trineo del tablero
        // Si ya estás en el último trineo, no pasa nada
        if (posicionSiguienteTrineo != -1) {
            j.setPosicion(posicionSiguienteTrineo);
        }
    }

    public String toString() {
        return "Casilla Trineo en posicion " + posicion
                + (posicionSiguienteTrineo != -1 ? " -> siguiente trineo: " + posicionSiguienteTrineo : " (ultimo trineo)");
    }
}