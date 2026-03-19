package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.partida.Partida;
import modelo.items.Item;
import java.util.Random;

public class SueloQuebradizo extends Casilla {
    
    private Random random;

    public SueloQuebradizo(int pos) {
        super(pos);
        this.random = new Random();
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        realizarAccionConLog(p, j); // delega para no duplicar lógica
    }

    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        if (!(j instanceof Pinguino)) return "";
        Pinguino ping = (Pinguino) j;
        int totalItems = ping.getInventario().getTotalItems();
        StringBuilder log = new StringBuilder();
        log.append("🧊 Suelo Quebradizo (tienes ").append(totalItems).append(" objeto(s)): ");

        if (totalItems > 5) {
            ping.setPosicion(0);
            log.append("¡El suelo no aguanta! Vuelves al inicio.");
        } else if (totalItems > 0) {
            ping.setTurnosCongelado(ping.getTurnosCongelado() + 1);
            log.append("El suelo se agrieta. Pierdes un turno.");
            if (random.nextDouble() < 0.30) {
                modelo.items.Item perdido = ping.getInventario().obtenerItemAleatorio();
                if (perdido != null) {
                    ping.getInventario().quitarItem(perdido);
                    log.append(" Además pierdes: ").append(perdido.getNombre()).append(".");
                }
            }
        } else {
            log.append("Sin objetos, pasas sin problemas.");
        }
        return log.toString();
    }

    @Override
    public String toString() {
        return "Casilla suelo quebradizo en posicion " + posicion;
    }
}