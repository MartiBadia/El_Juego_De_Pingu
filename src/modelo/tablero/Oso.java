package modelo.tablero;
import modelo.jugador.Jugador;
import modelo.partida.Partida;

public class Oso extends Casilla {

    // Constructor
    public Oso(int pos) {
        this.posicion = pos;
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        // Según el enunciado: "si un jugador es atacado, retorna al inicio del juego"
        j.setPosicion(0);
    }

    @Override
    public String toString() {
        return "Casilla Oso en posicion " + posicion;
    }
}