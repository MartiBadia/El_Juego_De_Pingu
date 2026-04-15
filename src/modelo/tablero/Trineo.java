package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

/**
 * Casilla especial Trineo.
 * Al caer en ella, el jugador avanza un número determinado de casillas (ej: 12).
 */
public class Trineo extends Casilla {

    private static final int AVANCE = 12;

    public Trineo(int posicion) {
        super(posicion);
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        realizarAccionConLog(p, j);
    }

    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        j.moverPosicion(AVANCE, p.getTablero().getTamaño() - 1);
        return "🛷 ¡Has caído en un Trineo! Te deslizas " + AVANCE + " casillas hacia adelante.";
    }

    @Override
    public String toString() {
        return "Casilla Trineo en posicion " + posicion;
    }
}
