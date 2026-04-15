package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

/**
 * Clase que representa una casilla estándar del tablero.
 * No tiene efectos especiales al caer en ella.
 */
public class CasillaNormal extends Casilla {

    /**
     * Constructor para la casilla normal con una posición específica.
     * @param posicion Índice de la casilla en el tablero.
     */
    public CasillaNormal(int posicion) {
        super(posicion);
    }

    /**
     * Ejecuta la acción de la casilla normal. 
     * Por definición, no realiza ninguna alteración al estado del juego.
     */
    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {
        // No realiza ninguna acción especial
    }

    /**
     * Devuelve una descripción de la acción para el log.
     */
    @Override
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        return "📍 Has caído en una casilla normal.";
    }

    @Override
    public String toString() {
        return "Casilla Normal en posición " + posicion;
    }
}
