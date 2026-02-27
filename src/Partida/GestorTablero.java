package Partida;

import Tablero.Casilla;
import Jugador.Jugador;

public class GestorTablero {

    public GestorTablero() {
        // falta getionar
    }

    public void ejecutarCasilla(Partida partida, Jugador j, Casilla c) {
        if (c != null) {
            c.realizarAccion(partida, j);
        }
    }

    public void comprobarFinTurno(Partida partida) {
        // Lógica para verificar si la partida ha terminado
    }
}