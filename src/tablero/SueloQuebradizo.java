package tablero;

import jugador.Jugador;
import jugador.Pinguino;
import partida.Partida;
import modelo.items.Item;

public class SueloQuebradizo extends Casilla {
    
    // Constructor
    public SueloQuebradizo(int pos) {
        this.posicion = pos;
    }

    @Override
    public void realizarAccion(Partida p, Jugador j) {
        // Según el enunciado INTERMIG: "Terra trencadís: afecten segons l'inventari del jugador."
        if (j instanceof Pinguino) {
            Pinguino ping = (Pinguino) j;
            int totalItems = ping.getInventario().getTotalItems();
            
            System.out.println("¡Suelo Quebradizo! Tienes " + totalItems + " objetos.");
            
            if (totalItems > 5) {
                // "Més de 5 objectes -> el jugador cau i retorna a l'inici."
                System.out.println("Pesas demasiado... ¡Al inicio!");
                ping.setPosicion(0);
            } else if (totalItems > 0) {
                // "Fins a 5 objectes -> perd un torn."
                System.out.println("El suelo cruje... ¡Pierdes un turno para salir con cuidado!");
                ping.setTurnosCongelado(ping.getTurnosCongelado() + 1);
                
                // Eventos adicionales mencionados en el enunciado
                double azar = Math.random();
                if (azar < 0.20) {
                    System.out.println("¡Oh no! Se te ha caído un objeto en la grieta.");
                    Item perdido = ping.getInventario().obtenerItemAleatorio();
                    if (perdido != null) ping.getInventario().quitarItem(perdido);
                }
            } else {
                // "Sense objectes -> passa sense penalització."
                System.out.println("Eres ligero como una pluma. Pasas sin problemas.");
            }
        }
    }

    @Override
    public String toString() {
        return "Casilla Terra Trencadís en posicion " + posicion;
    }
}
