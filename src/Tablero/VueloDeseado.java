package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class VueloDeseado extends Casilla {
    public VueloDeseado(int pos) {
        this.posicion = pos; // Asignación directa
    }
    
    public void realizarAccion(Partida p, Jugador j) {}
}
