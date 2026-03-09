package partida;

import jugador.Jugador;
import tablero.Casilla;
import tablero.Tablero;

// TODO: Nivell INTERMIG/IMPOSSIBLE - Faltan implementar tipos de casillas obligatorios:
// - Caselles de terra trencadis (afecten segons l'inventari).
// - Motos de neu (permeten avançar fins el següent trineu).
public class GestorTablero {

    public GestorTablero() {
    }

    /**
     * Obtiene la casilla especial que corresponde a la posición del jugador
     * y ejecuta su acción. Si no hay casilla especial (casilla normal), no hace nada.
     */
    public void ejecutarCasilla(Partida partida, Jugador j) {
        Tablero tablero = partida.getTablero();
        // Busca si la posición actual del jugador tiene una casilla especial
        Casilla c = tablero.getCasillaEnPosicion(j.getPosicion());
        if (c != null) {
            c.realizarAccion(partida, j);
        }
    }

    public void ejecutarCasilla(Partida partida, Jugador j, Casilla c) {
        if (c != null) {
            c.realizarAccion(partida, j);
        }
    }

    /**
     * Comprueba si la partida ha terminado.
     * La partida termina cuando algún jugador llega o supera la última casilla del tablero.
     * En ese caso, marca la partida como finalizada y establece al ganador.
     */
    public void comprobarFinTurno(Partida partida) {
        int totalCasillas = partida.getTablero().getTamaño();

        for (Jugador j : partida.getJugadores()) {
            if (j.getPosicion() >= totalCasillas) {
                // Este jugador ha llegado al final -> gana
                partida.setFinalizada(true);
                partida.setGanador(j);
                System.out.println("¡" + j.getNombre() + " ha ganado la partida!");
                return;
            }
        }
    }
}