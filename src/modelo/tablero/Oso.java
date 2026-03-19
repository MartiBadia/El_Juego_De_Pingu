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
        j.setPosicion(0);
    }

    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        realizarAccion(p, j);
        return "🐻 ¡Un Oso te ataca! Vuelves al inicio del tablero.";
    }

    @Override
    public String toString() {
        return "Casilla Oso en posicion " + posicion;
    }
}