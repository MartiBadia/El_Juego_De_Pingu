package Tablero;

import Partida.Partida;
import Jugador.Jugador;

public abstract class Casilla {
    protected int posicion; // Cambiado a protected para acceso directo

    public Casilla() {} // Constructor vacío para evitar el uso de super

    public abstract void realizarAccion(Partida partida, Jugador jugador);
}