package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

public class Agujero extends Casilla {
    private int posicionAgujeroAnterior; // Posición del agujero de hielo al que envía al jugador
    private boolean esPrimero;  // Si es el primer agujero, el jugador va al inicio del tablero

    // Constructor
    public Agujero(int pos) {
        this.posicion = pos;
        this.posicionAgujeroAnterior = 0;
        this.esPrimero = true;
    }

    // Constructor con destino
    public Agujero(int pos, int posicionAgujeroAnterior) {
        this.posicion = pos;
        this.posicionAgujeroAnterior = posicionAgujeroAnterior;
        this.esPrimero = (posicionAgujeroAnterior == 0);
    }

    // --- GETTERS ---
    public int getPosicionAgujeroAnterior() {
        return posicionAgujeroAnterior;
    }

    public boolean isEsPrimero() {
        return esPrimero;
    }

    // --- SETTERS ---
    public void setPosicionAgujeroAnterior(int posicionAgujeroAnterior) {
        this.posicionAgujeroAnterior = posicionAgujeroAnterior;
        this.esPrimero = (posicionAgujeroAnterior == 0);
    }

    public void setEsPrimero(boolean esPrimero) {
        this.esPrimero = esPrimero;
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        j.setPosicion(posicionAgujeroAnterior);
    }

    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        realizarAccion(p, j);
        return "🕳️ ¡Caíste en un Agujero de Hielo! Te manda a la casilla " + posicionAgujeroAnterior + ".";
    }

    @Override
    public String toString() {
        return "Casilla agujero en el hielo en posicion " + posicion + " -> envia a " + posicionAgujeroAnterior;
    }
}