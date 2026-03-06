package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class Evento extends Casilla {
    
    private String[] eventos;

    // Constructor
    public Evento(int pos) {
        this.posicion = pos;
        
        
        this.eventos = new String[]{
            "OBTENER_PEZ",  // - Obtener un pez
            "OBTENER_BOLAS_NIEVE", // - Obtener 1-3 bolas de nieve
            "OBTENER_DADO_RAPIDO", // - Obtener un dado rápido (avanza 5-10 casillas, probabilidad baja)
            "OBTENER_DADO_LENTO"  // - Obtener un dado lento (probabilidad alta, valores entre 1 y 3)
        };
    }

    // Constructor con eventos personalizados
    public Evento(int pos, String[] eventos) {
        this.posicion = pos;
        this.eventos = eventos;
    }

    //Getter
    public String[] getEventos() {
        return eventos;
    }

    // Setter
    public void setEventos(String[] eventos) {
        this.eventos = eventos;
    }

    
    public void realizarAccion(Partida p, Jugador j) {
        int idx = (int)(Math.random() * eventos.length);
        String eventoElegido = eventos[idx];
        System.out.println("Casella d'interrogant! Evento: " + eventoElegido);
    }

    
    public String toString() {
        return "Casilla Evento (?) en posicion " + posicion;
    }
}