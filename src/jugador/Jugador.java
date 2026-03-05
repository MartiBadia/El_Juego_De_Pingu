package jugador;

import modelo.items.Inventario;
import modelo.items.Item;

public abstract class Jugador {
    protected int posicion; // Protegido para que Pinguino y Foca lo vean
    protected String nombre;
    protected String color;

    // Constructor vacío básico
    public Jugador() {
    }

    // Constructor con parámetros
    public Jugador(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
        this.posicion = 0;
    }

    // --- GETTERS ---
    public int getPosicion() {
        return posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getColor() {
        return color;
    }

    // --- SETTERS ---
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void moverPosicion(int n) {
    	this.posicion += n;
    }

    @Override
    public String toString() {
        return nombre + " [" + color + "] -> Casilla " + posicion;
    }
}