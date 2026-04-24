package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

/**
 * Clase que representa la casilla de salida del tablero.
 */
public class CasillaSalida extends Casilla {

    public CasillaSalida(int posicion) {
        super(posicion);
    }

    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {
        // No realiza ninguna acción especial al caer en la salida
    }

    @Override
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        return "🏠 Estás en la casilla de salida.";
    }

    @Override
    public String toString() {
        return "Casilla de Salida en posición " + posicion;
    }
}
