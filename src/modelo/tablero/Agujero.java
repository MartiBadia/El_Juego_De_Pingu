package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

/**
 * Representa una trampa en el tablero.
 * Si un jugador cae aquí, es teletransportado a una casilla anterior o al inicio.
 */
public class Agujero extends Casilla {
    private int posicionAgujeroAnterior; // Posición del agujero de hielo al que envía al jugador
    private boolean esPrimero;  // Si es el primer agujero, el jugador va al inicio del tablero

    // Constructor
    // Constructor básico, por defecto asume que es el primero y te manda al inicio
    public Agujero(int pos) {
        this.posicion = pos;
        this.posicionAgujeroAnterior = 0;
        this.esPrimero = true;
    }

    // Constructor con destino
    // Constructor que permite especificar a qué posición retrocede el jugador
    public Agujero(int pos, int posicionAgujeroAnterior) {
        this.posicion = pos;
        this.posicionAgujeroAnterior = posicionAgujeroAnterior;
        this.esPrimero = (posicionAgujeroAnterior == 0);
    }

    // --- GETTERS ---
    // Nos dice a qué casilla manda este agujero
    public int getPosicionAgujeroAnterior() {
        return posicionAgujeroAnterior;
    }

    // Indica si es el primer agujero del mapa
    public boolean isEsPrimero() {
        return esPrimero;
    }

    // --- SETTERS ---
    // Cambia el destino del agujero (se usa al generar el tablero)
    public void setPosicionAgujeroAnterior(int posicionAgujeroAnterior) {
        this.posicionAgujeroAnterior = posicionAgujeroAnterior;
        this.esPrimero = (posicionAgujeroAnterior == 0);
    }

    // Permite marcar manualmente si es el primero o no
    public void setEsPrimero(boolean esPrimero) {
        this.esPrimero = esPrimero;
    }

    // Mueve al jugador a la posición de castigo
    @Override
    public void realizarAccion(Partida p, Jugador j) {
        j.setPosicion(posicionAgujeroAnterior);
    }

    // Ejecuta el retroceso y devuelve el aviso para el usuario
    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        realizarAccion(p, j);
        return "¡Caíste en un Agujero de Hielo! Te manda a la casilla " + posicionAgujeroAnterior + ".";
    }

    @Override
    public String toString() {
        return "Casilla agujero en el hielo en posicion " + posicion + " -> envia a " + posicionAgujeroAnterior;
    }
}