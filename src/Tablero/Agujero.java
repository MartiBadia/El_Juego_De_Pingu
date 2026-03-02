package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class Agujero extends Casilla {
    public Agujero(int pos) {
        this.posicion = pos; // Asignación directa
    }
    
    public void realizarAccion(Partida p, Jugador j) {
        // Lógica: el jugador queda atrapado un turno
    }
}