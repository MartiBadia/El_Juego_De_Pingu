package controlador.gestor;

import modelo.jugador.Jugador;
import modelo.partida.Partida;
import modelo.tablero.Casilla;
import modelo.tablero.Tablero;
import java.util.ArrayList;

public class GestorTablero {

    public GestorTablero() {
    }

    /**
     * Obtiene la casilla especial que corresponde a la posición del jugador
     * y ejecuta su acción. Si no hay casilla especial (casilla normal), no hace nada.
     */
    public void ejecutarCasilla(Partida partida, Jugador j) {
        Tablero tablero = partida.getTablero();
        // Obtiene la casilla de la posición actual y ejecuta su acción
        Casilla c = tablero.getCasillaEnPosicion(j.getPosicion());
        c.realizarAccion(partida, j);
    }

    public void ejecutarCasilla(Partida partida, Jugador j, Casilla c) {
        c.realizarAccion(partida, j);
    }

    
     //Comprueba si la partida ha terminado.
    
     
    public void comprobarFinTurno(Partida partida) {
        int totalCasillas = partida.getTablero().getTamaño();
        ArrayList<Jugador> jugadores = partida.getJugadores();
        
        Jugador ganador = null;
        int i = 0;
        while (i < jugadores.size() && ganador == null) {
            Jugador j = jugadores.get(i);
            if (j.getPosicion() >= totalCasillas - 1) {
                ganador = j;
            }
            i++;
        }

        if (ganador != null) {
            // El primero que llega a la última casilla gana.
            partida.setFinalizada(true);
            partida.setGanador(ganador);
            System.out.println("¡" + ganador.getNombre() + " ha ganado la partida!");
        }
    }
}