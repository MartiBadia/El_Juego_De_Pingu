package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

// El punto de partida de todos los jugadores
public class CasillaSalida extends Casilla {

    // Constructor para la salida
    public CasillaSalida(int posicion) {
        super(posicion);
    }

    // No pasa nada especial por estar aquí
    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {
        // No realiza ninguna acción especial al caer en la salida
    }

    // Mensaje para el log del juego
    @Override
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        return "Estás en la casilla de salida.";
    }

    @Override
    public String toString() {
        return "Casilla de Salida en posición " + posicion;
    }
}
