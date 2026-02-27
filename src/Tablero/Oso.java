package Tablero;
import Partida.Partida;
import Jugador.Jugador;

public class Oso extends Casilla {
    public Oso(int pos) {
        this.posicion = pos; // Asignación directa sin super()
    }
    
    public void realizarAccion(Partida p, Jugador j) {
        j.moverPosicion(-2);
    }
}