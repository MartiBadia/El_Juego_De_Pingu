package modelo.jugador;

import modelo.items.Inventario;
import modelo.items.Item;

public abstract class Jugador {
    protected int posicion; // Protegido para que Pinguino y Foca lo vean
    protected String nombre;
    protected String color;
    protected String skin; // Nombre del archivo de imagen
    protected boolean esIA;

    // Constructor vacío básico
    // Constructor por defecto, no hace nada especial
    public Jugador() {
    }

    // Constructor con parámetros
    // Constructor básico para crear un jugador con nombre y color
    public Jugador(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
        this.posicion = 0;
        this.esIA = false;
    }

    // Constructor con parámetros incluyendo IA
    // Constructor por si queremos especificar si el jugador es una IA o no
    public Jugador(String nombre, String color, boolean esIA) {
        this.nombre = nombre;
        this.color = color;
        this.posicion = 0;
        this.esIA = esIA;
    }

    // --- GETTERS ---
    // Indica si este jugador lo controla la máquina
    public boolean isEsIA() {
        return esIA;
    }

    // Cambia el estado de IA del jugador
    public void setEsIA(boolean esIA) {
        this.esIA = esIA;
    }

    // Nos da la posición actual en el tablero
    public int getPosicion() {
        return posicion;
    }

    // Devuelve el nombre del jugador
    public String getNombre() {
        return nombre;
    }

    // Devuelve el color asignado al jugador
    public String getColor() {
        return color;
    }

    // --- SETTERS ---
    // Actualiza la posición del jugador directamente
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    // Cambia el nombre del jugador
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Cambia el color del jugador
    public void setColor(String color) {
        this.color = color;
    }

    // Devuelve la ruta o nombre de la skin (imagen) del personaje
    public String getSkin() {
        return skin;
    }

    // Asigna una skin específica al personaje
    public void setSkin(String skin) {
        this.skin = skin;
    }

    // Suma n casillas a la posición actual
    public void moverPosicion(int n) {
        this.posicion += n;
    }

    /** Mueve al jugador n casillas sin sobrepasar maxPos. */
    // Mueve al jugador pero controlando que no se pase del final ni baje de cero
    public void moverPosicion(int n, int maxPos) {
        this.posicion = Math.min(this.posicion + n, maxPos);
        if (this.posicion < 0) this.posicion = 0;
    }

    // Representación en texto para depuración o logs rápidos
    @Override
    public String toString() {
        return nombre + " [" + color + "] -> Casilla " + posicion;
    }
}