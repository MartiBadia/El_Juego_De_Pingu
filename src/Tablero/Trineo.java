package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class Trineo extends Casilla {
    public Trineo(int pos) {
        this.posicion = pos; // Asignación directa
    }
    
    public void realizarAccion(Partida p, Jugador j) {
        j.moverPosicion(3);
    }
}