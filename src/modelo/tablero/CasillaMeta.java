package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

// La casilla final, quien llega aquí gana la partida
public class CasillaMeta extends Casilla {

    // Constructor para la meta
    public CasillaMeta(int posicion) {
        super(posicion);
    }

    // Aquí se podría disparar la lógica de fin de juego
    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {
        // La lógica de victoria suele gestionarse en el GestorPartida o Partida,
        // pero marcamos que el jugador ha llegado.
    }

    // Mensaje de celebración para el log
    @Override
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        return "🏁 ¡HAS LLEGADO A LA META!";
    }

    @Override
    public String toString() {
        return "Casilla de Meta en posición " + posicion;
    }
}
