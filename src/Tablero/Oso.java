package Tablero;
import Partida.Partida;
import Jugador.Jugador;

public class Oso extends Casilla {

    // Constructor
    public Oso(int pos) {
        this.posicion = pos;
    }

   
    public void realizarAccion(Partida p, Jugador j) {
        //retorna al inicio del juego si es atacado
        j.setPosicion(0);
    }

   
    public String toString() {
        return "Casilla Oso en posicion " + posicion;
    }
}