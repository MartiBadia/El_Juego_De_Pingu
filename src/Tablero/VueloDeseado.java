package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class VueloDeseado extends Casilla {
    
   "
    private int posicionAgujeroAnterior; // Agujero d ehielo en que posicion cuando cae el jugador

    // Constructor
    public VueloDeseado(int pos) {
        this.posicion = pos;
        this.posicionAgujeroAnterior = 0; // Por defecto retrocede al inicio
    }

    // Constructor con destino dekl jugador
    public VueloDeseado(int pos, int posicionAgujeroAnterior) {
        this.posicion = pos;
        this.posicionAgujeroAnterior = posicionAgujeroAnterior;
    }

    // Getter
    public int getPosicionAgujeroAnterior() {
        return posicionAgujeroAnterior;
    }

    //setter
    public void setPosicionAgujeroAnterior(int posicionAgujeroAnterior) {
        this.posicionAgujeroAnterior = posicionAgujeroAnterior;
    }


    public void realizarAccion(Partida p, Jugador j) {
        // Envia al ususario al anterior agujero
        // Si era el primer agujero pues vuelve al inicio
        j.setPosicion(posicionAgujeroAnterior);
    }


    public String toString() {
        return "Casilla VueloDeseado (forat) en posicion " + posicion + " -> envia a " + posicionAgujeroAnterior;
    }
}
