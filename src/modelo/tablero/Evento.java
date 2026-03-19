package modelo.tablero;

import controlador.gestor.GestorJugador;
import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.partida.Partida;

public class Evento extends Casilla {
    
    // Constructor
    public Evento(int pos) {
        this.posicion = pos;
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        realizarAccionConLog(p, j); // delega para no duplicar lógica
    }

    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        if (!(j instanceof Pinguino)) return "";
        Pinguino ping = (Pinguino) j;
        GestorJugador gestorJ = new GestorJugador();

        double azar = Math.random();
        String obtenido;

        if (azar < 0.25) {
            gestorJ.pinguinoEventoPez(ping);
            obtenido = "🐟 ¡Has obtenido un Pez!";
        } else if (azar < 0.60) {
            int antes = ping.getInventario().contarPorTipo("Bola de Nieve");
            gestorJ.pinguinoEventoBolaDeNieve(ping);
            int despues = ping.getInventario().contarPorTipo("Bola de Nieve");
            obtenido = "❄️ ¡Has obtenido " + (despues - antes) + " Bola(s) de Nieve!";
        } else if (azar < 0.70) {
            gestorJ.pinguinoEventoDadoRapido(ping);
            obtenido = "🎲 ¡Has obtenido un Dado Rápido (5-10)!";
        } else {
            gestorJ.pinguinoEventoDadoLento(ping);
            obtenido = "🎲 ¡Has obtenido un Dado Lento (1-3)!";
        }

        return "❓ Casilla Evento: " + obtenido;
    }

    @Override
    public String toString() {
        return "Casilla Evento (?) en posicion " + posicion;
    }
}