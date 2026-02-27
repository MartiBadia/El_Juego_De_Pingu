package Jugador;

public class Foca extends Jugador {
    private boolean soborno;

    public Foca(String nombre, String color) {
        this.nombre = nombre; // Asignación directa
        this.color = color;  // Asignación directa
        this.posicion = 0;
        this.soborno = false;
    }

    public void moverPosicion(int n) {
        this.posicion = this.posicion + n;
    }
    
    public void aplastarJugador(Pinguino p) {
    	
    }
    public void golpearJugador(Pinguino p) {
    	
    }
    public boolean esSobornada() { 
    	
    	
    	return soborno; 
    	
    }
}