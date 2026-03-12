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
        if (j instanceof Pinguino) {
            Pinguino ping = (Pinguino) j;
            int totalItems = ping.getInventario().getTotalItems();
            
            System.out.println("¡Suelo Quebradizo en la casilla " + posicion + "! Tienes " + totalItems + " objetos.");
            
            if (totalItems > 5) {
                System.out.println("¡CRACK! El suelo no aguanta tu peso. Vuelves al inicio.");
                ping.setPosicion(0);
            } else if (totalItems > 0) {
                System.out.println("El suelo se agrieta un poco... Pierdes un turno para salir con cuidado.");
                ping.setTurnosCongelado(ping.getTurnosCongelado() + 1);
                
                if (random.nextDouble() < 0.30) {
                    System.out.println("¡Oh no! Al intentar salir, se te ha caído algo.");
                    Item perdido = ping.getInventario().obtenerItemAleatorio();
                    if (perdido != null) {
                        ping.getInventario().quitarItem(perdido);
                        System.out.println("Has perdido: " + perdido.getNombre());
                    }
                }
            } else {
                System.out.println("Eres ligero como una pluma. Pasas sin problemas.");
            }
        }
    }

    @Override
    public String toString() {
        return "Casilla Terra Trencadís en posicion " + posicion;
    }
}