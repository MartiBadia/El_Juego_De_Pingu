package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

// Una casilla normal y corriente, aquí no pasa nada especial
public class CasillaNormal extends Casilla {

    // Constructor que simplemente sitúa la casilla en el tablero
    public CasillaNormal(int posicion) {
        super(posicion);
    }

    // Aquí no se hace nada, es una casilla de descanso
    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {
        // No realiza ninguna acción especial
    }

    // Informa de que el jugador ha caído en terreno seguro
    @Override
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        return "Has caído en una casilla normal.";
    }

    @Override
    public String toString() {
        return "Casilla Normal en posición " + posicion;
    }
}
