package Jugador;

import Items.Inventario;

public class Pinguino extends Jugador {
    private Inventario inv;

    public Pinguino(String nombre, String col) {
        this.nombre = nombre; // Asignación directa
        this.color = color;  // Asignación directa
        this.posicion = 0;
        this.inv = new Inventario();
    }

    public void moverPosicion(int n) {
        this.posicion = this.posicion + n;
    }
    
    public void gestionarBatalla(Pinguino p) {
    	
    }
    public void usarItem(Items.Item i) {
    	
    }
    public void añadirItem(Items.Item i) {
    	
    }
    public void quitarItem(Items.Item i) {
    	
    }
}