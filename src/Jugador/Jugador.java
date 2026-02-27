package Jugador;

import Items.Inventario;
import Items.Item;

public abstract class Jugador {
    protected int posicion; // Protegido para que Pinguino y Foca lo vean
    protected String nombre;
    protected String color;

    public Jugador() {
    	
    } // Constructor básico

    public abstract void moverPosicion(int n);
}