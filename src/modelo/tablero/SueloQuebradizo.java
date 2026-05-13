package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.partida.Partida;
import modelo.items.Item;
import java.util.Random;

/**
 * Una casilla peligrosa que reacciona al peso del jugador (número de objetos).
 * Si llevas demasiado encima, el hielo se rompe y te manda al inicio.
 */
public class SueloQuebradizo extends Casilla {
    
    private Random random;

    // Constructor para el suelo frágil
    public SueloQuebradizo(int pos) {
        super(pos);
        this.random = new Random();
    }

    // Llama al método que gestiona la rotura del hielo
    @Override
    public void realizarAccion(Partida p, Jugador j) {
        realizarAccionConLog(p, j); // delega para no duplicar lógica
    }

    // Si el jugador lleva muchos objetos, el hielo se rompe y vuelve al inicio. 
    // Si lleva pocos, solo pierde un turno y quizás algún objeto.
    @Override
    public String realizarAccionConLog(Partida p, Jugador j) {
        if (!(j instanceof Pinguino)) return "";
        Pinguino ping = (Pinguino) j;
        int totalItems = ping.getInventario().getTotalItems();
        // Restamos los dados del recuento, ya que para el jugador no cuentan como "objetos" de inventario en este contexto
        int objetosEspeciales = totalItems - ping.getInventario().contarPorTipo("Dado");
        
        if (objetosEspeciales <= 0) return ""; // No mostrar nada si no tiene objetos especiales

        StringBuilder log = new StringBuilder();
        log.append("Suelo Quebradizo (tienes ").append(objetosEspeciales).append(" objeto(s) especial(es)): ");

        if (objetosEspeciales > 5) {
            ping.setPosicion(0);
            log.append("¡El suelo no aguanta! Vuelves al inicio.");
        } else {
            // Entre 1 y 5 objetos especiales: pierde turno y probabilidad de perder uno
            ping.setTurnosCongelado(ping.getTurnosCongelado() + 1);
            log.append("El suelo se agrieta. Pierdes un turno.");
            if (random.nextDouble() < 0.30) {
                // Intentamos quitar un objeto que NO sea un dado
                modelo.items.Item perdido = ping.getInventario().obtenerItemAleatorio();
                if (perdido != null && !(perdido instanceof modelo.items.Dado)) {
                    ping.getInventario().quitarItem(perdido);
                    log.append(" Además pierdes: ").append(perdido.getNombre()).append(".");
                }
            }
        }
        return log.toString();
    }

    @Override
    public String toString() {
        return "Casilla suelo quebradizo en posicion " + posicion;
    }
}