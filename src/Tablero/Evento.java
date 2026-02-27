package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class Evento extends Casilla {
    private String[] eventos;

    public Evento(int pos) {
        this.posicion = pos; // Asignación directa
    }
    
    public void realizarAccion(Partida p, Jugador j) {}
}