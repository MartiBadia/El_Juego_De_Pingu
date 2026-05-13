package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

// El trineo sirve para saltar rápidamente a la siguiente casilla de trineo
public class Trineo extends Casilla {

    private static final int AVANCE = 12;

    // Constructor que pone el trineo en su sitio
    public Trineo(int posicion) {
        super(posicion);
    }

    // Llama a la lógica principal del trineo
    @Override
    public void realizarAccion(Partida p, Jugador j) {
        realizarAccionConLog(p, j);
    }

    // Calcula a qué trineo saltar y gestiona choques con otros jugadores al aterrizar
    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        int posSiguienteTrineo = -1;
        
        // 1. Buscamos la siguiente casilla que sea un Trineo
        java.util.ArrayList<Casilla> casillas = p.getTablero().getCasillas();
        int i = 0;
        while (i < casillas.size() && posSiguienteTrineo == -1) {
            Casilla c = casillas.get(i);
            if (c instanceof Trineo && c.getPosicion() > this.getPosicion()) {
                posSiguienteTrineo = c.getPosicion();
            }
            i++;
        }

        if (posSiguienteTrineo != -1) {
            j.setPosicion(posSiguienteTrineo);
            String msg = "¡" + j.getNombre() + " se desliza por el trineo hasta la casilla " + (posSiguienteTrineo + 1) + "!";
            
            // 2. Si el que se desliza es una Foca, comprobamos si choca con un Pinguino
            if (j instanceof modelo.jugador.Foca) {
                for (Jugador otro : p.getJugadores()) {
                    if (otro instanceof modelo.jugador.Pinguino && otro.getPosicion() == posSiguienteTrineo) {
                        controlador.gestor.GestorJugador gestorJ = new controlador.gestor.GestorJugador();
                        msg += "\n" + gestorJ.focaInteractuaPinguino((modelo.jugador.Pinguino) otro, (modelo.jugador.Foca) j, p.getTablero());
                    }
                }
            }
            
            // 3. Si el que se desliza es un Pinguino, comprobamos si choca con otro Pinguino
            if (j instanceof modelo.jugador.Pinguino) {
                for (Jugador otro : p.getJugadores()) {
                    if (otro instanceof modelo.jugador.Pinguino && otro != j && otro.getPosicion() == posSiguienteTrineo) {
                        controlador.gestor.GestorJugador gestorJ = new controlador.gestor.GestorJugador();
                        gestorJ.pinguinoLuchaPinguino((modelo.jugador.Pinguino) j, (modelo.jugador.Pinguino) otro);
                        msg += "\n¡Batalla en el trineo! " + j.getNombre() + " vs " + otro.getNombre() + ". Ambos usan sus bolas de nieve.";
                    }
                }
            }
            return msg;
        } else {
            return "¡" + j.getNombre() + " ha caído en un Trineo! Pero no hay más trineos adelante...";
        }
    }

    @Override
    public String toString() {
        return "Casilla Trineo en posicion " + posicion;
    }
}
