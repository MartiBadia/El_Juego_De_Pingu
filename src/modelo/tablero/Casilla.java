package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

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

    /**
     * Ejecuta la acción de la casilla y devuelve una descripción textual para la UI.
     * Las subclases deben sobreescribir este método para dar feedback al jugador.
     */
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        realizarAccion(partida, jugador);
        return "";
    }

    @Override
    public String toString() {
        return "Casilla " + posicion + " [" + getClass().getSimpleName() + "]";
    }
}