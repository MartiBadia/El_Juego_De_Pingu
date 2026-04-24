package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

/**
 * Clase que representa la casilla de meta del tablero.
 */
public class CasillaMeta extends Casilla {

    public CasillaMeta(int posicion) {
        super(posicion);
    }

    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {
        // La lógica de victoria suele gestionarse en el GestorPartida o Partida,
        // pero marcamos que el jugador ha llegado.
    }

    @Override
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        return "🏁 ¡HAS LLEGADO A LA META!";
    }

    @Override
    public String toString() {
        return "Casilla de Meta en posición " + posicion;
    }
}
