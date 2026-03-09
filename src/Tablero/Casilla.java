package tablero;

import jugador.Jugador;
import partida.Partida;

public abstract class Casilla {
    protected int posicion; // Cambiado a protected para acceso directo

    // Constructor vacío
    public Casilla() {}

    // Constructor con posición
    public Casilla(int posicion) {
        this.posicion = posicion;
    }

    // --- GETTER ---
    public int getPosicion() {
        return posicion;
    }

    // --- SETTER ---
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public abstract void realizarAccion(Partida partida, Jugador jugador);

    @Override
    public String toString() {
        return "Casilla " + posicion + " [" + getClass().getSimpleName() + "]";
    }
}