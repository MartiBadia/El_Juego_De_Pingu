package Tablero;

import Partida.Partida;
import Jugador.Jugador;

public abstract class Casilla {
    protected int posicion; // cambiado a protected para acceso directo

    // Constructor vacío
    public Casilla() {}

    // Constructor de la posicion
    public Casilla(int posicion) {
        this.posicion = posicion;
    }

    // getter
    public int getPosicion() {
        return posicion;
    }

    //setters
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public abstract void realizarAccion(Partida partida, Jugador jugador);

    
    public String toString() {
        return "Casilla " + posicion + " [" + getClass().getSimpleName() + "]";
    }
}