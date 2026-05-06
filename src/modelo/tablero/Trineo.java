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
        int posSiguienteTrineo = -1;
        
        // Buscamos la siguiente casilla que sea un Trineo
        for (Casilla c : p.getTablero().getCasillas()) {
            if (c instanceof Trineo && c.getPosicion() > this.getPosicion()) {
                posSiguienteTrineo = c.getPosicion();
                break; // El primer trineo que encontremos es el más cercano
            }
        }

        if (posSiguienteTrineo != -1) {
            j.setPosicion(posSiguienteTrineo);
            return "¡Has caído en un Trineo! Te deslizas hasta el siguiente trineo en la casilla " + (posSiguienteTrineo + 1) + ".";
        } else {
            return "¡Has caído en un Trineo! Pero no hay más trineos adelante...";
        }
    }

    @Override
    public String toString() {
        return "Casilla Trineo en posicion " + posicion;
    }
}
