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
        System.out.println("¡Has encontrado una Moto de Nieve! Avanzando al siguiente trineo...");
        
        int posActual = j.getPosicion();
        int destino = -1;

        // Buscamos el primer trineo que esté después de la posición actual
        for (Casilla c : p.getTablero().getCasillas()) {
            if (c instanceof Trineo && c.getPosicion() > posActual) {
                destino = c.getPosicion();
                break;
            }
        }

        if (destino != -1) {
            j.setPosicion(destino);
            System.out.println(j.getNombre() + " se ha desplazado en moto hasta la casilla " + destino + " (Trineo).");
            
            // Opcional: Ejecutar la acción del trineo al caer en él
            // p.getTablero().getCasillaEnPosicion(destino).realizarAccion(p, j);
        } else {
            System.out.println("No hay más trineos en el tablero. La moto no te lleva a ninguna parte.");
        }
    }

    @Override
    public String toString() {
        return "Casilla Moto de Nieve en posicion " + posicion;
    }
}
