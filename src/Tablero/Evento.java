package tablero;

import jugador.Jugador;
import jugador.Pinguino;
import partida.Partida;
import partida.GestorJugador;

public class Evento extends Casilla {
    
    // Constructor
    public Evento(int pos) {
        this.posicion = pos;
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        if (j instanceof Pinguino) {
            Pinguino ping = (Pinguino) j;
            GestorJugador gestorJ = new GestorJugador(); // Usamos gestor para aplicar efectos de forma centralizada
            
            System.out.println("¡Casilla de interrogante (?)!");
            
            // Probabilidades basadas en el enunciado:
            double azar = Math.random();
            
            if (azar < 0.25) {
                // Obtener un pez
                gestorJ.pinguinoEventoPez(ping);
            } else if (azar < 0.60) {
                // Obtener 1-3 bolas de nieve
                gestorJ.pinguinoEventoBolaDeNieve(ping);
            } else if (azar < 0.70) {
                // Obtener un dado rápido (5-10, baja prob)
                gestorJ.pinguinoEventoDadoRapido(ping);
            } else {
                // Obtener un dado lento (1-3, alta prob)
                gestorJ.pinguinoEventoDadoLento(ping);
            }
        }
    }

    @Override
    public String toString() {
        return "Casilla Evento (?) en posicion " + posicion;
    }
}