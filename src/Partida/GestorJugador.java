package Partida;

import Jugador.Jugador;
import Jugador.Pinguino;
import Jugador.Foca;
import Tablero.Tablero;

public class GestorJugador {

    public GestorJugador() {
        // Constructor vacio basico
    }

    public void jugadorUsaItem(String nombreItem) {
        // Lógica de uso de items
    }

    public void jugadorRecibeDaño(Jugador j, int pasos, Tablero t) {
        j.moverPosicion(-pasos);
    }

    public void jugadorFinalizaTurno(Jugador j) {
        // Lógica de fin de turno
    }

    public void pinguinoEventoPez(Pinguino p) {
        // Lógica cuando un pingüino encuentra un pez
    }

    public void pinguinoLuchaPinguino(Pinguino p1, Pinguino p2) {
        // Lógica de combate
    }

    public void focaInteractuaPinguino(Pinguino p, Foca f) {
        // Lógica de interacción con la foca
    }
}