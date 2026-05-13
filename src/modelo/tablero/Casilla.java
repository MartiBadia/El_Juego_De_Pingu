package modelo.tablero;

import modelo.jugador.Jugador;
import modelo.partida.Partida;

/**
 * Clase base para todas las casillas del tablero.
 * Cada casilla tiene una posición y una acción que se ejecuta cuando un jugador cae en ella.
 */
public abstract class Casilla {
    protected int posicion; // Cambiado a protected para acceso directo

    // Constructor vacío
    // Constructor básico sin parámetros
    public Casilla() {}

    // Constructor con posición
    // Crea una casilla situada en un punto concreto del tablero
    public Casilla(int posicion) {
        this.posicion = posicion;
    }

    // --- GETTER ---
    // Nos dice en qué número de casilla estamos
    public int getPosicion() {
        return posicion;
    }

    // --- SETTER ---
    // Cambia la ubicación de la casilla en el tablero
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    // Método que cada tipo de casilla debe rellenar con su lógica (ej: dar un objeto, mover al jugador...)
    public abstract void realizarAccion(Partida partida, Jugador jugador);

    /**
     * Ejecuta la acción de la casilla y devuelve una descripción textual para la UI.
     * Las subclases deben sobreescribir este método para dar feedback al jugador.
     */
    // Igual que realizarAccion, pero además devuelve un texto explicando qué ha pasado
    public String realizarAccionConLog(Partida partida, Jugador jugador) {
        realizarAccion(partida, jugador);
        return "";
    }

    // Identifica la casilla por su número y su tipo de clase
    @Override
    public String toString() {
        return "Casilla " + posicion + " [" + getClass().getSimpleName() + "]";
    }
}