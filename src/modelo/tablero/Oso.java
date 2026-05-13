package modelo.tablero;
import modelo.jugador.Jugador;
import modelo.partida.Partida;

public class Oso extends Casilla {

    // Constructor
    // Crea una casilla con un oso en la posición indicada
    public Oso(int pos) {
        this.posicion = pos;
    }

    // Si caes aquí, el oso te manda de vuelta a la casilla 0
    @Override
    public void realizarAccion(Partida p, Jugador j) {
        j.setPosicion(0);
    }

    // Ejecuta el ataque del oso y devuelve el mensaje de aviso
    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        realizarAccion(p, j);
        return "¡Un Oso te ataca! Vuelves al inicio del tablero.";
    }

    @Override
    public String toString() {
        return "Casilla Oso en posicion " + posicion;
    }
}