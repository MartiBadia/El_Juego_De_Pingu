package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

/**
 * Casilla especial "Moto de Nieve".
 * Permite avanzar hasta el siguiente trineo del tablero.
 */
public class MotoNieve extends Casilla {

    public MotoNieve(int pos) {
        super(pos);
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        realizarAccionConLog(p, j);
    }

    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        int posActual = j.getPosicion();
        int destino = -1;

        for (Casilla c : p.getTablero().getCasillas()) {
            if (c instanceof Trineo && c.getPosicion() > posActual) {
                destino = c.getPosicion();
                break;
            }
        }

        if (destino != -1) {
            j.setPosicion(destino);
            return "🏍️ ¡Moto de Nieve! Te lanzas hasta el Trineo en casilla " + destino + ".";
        }
        return "🏍️ Moto de Nieve, pero no hay trineos por delante. Sin efecto.";
    }

    @Override
    public String toString() {
        return "Casilla Moto de Nieve en posicion " + posicion;
    }
}
