package Tablero;

import Jugador.Jugador;
import Partida.Partida;

public class Agujero extends Casilla {
    private int turnosAtrapado; // numero de turnos que el jugador queda atrapado
    private boolean esPrimero;  // Si es el primer agujero, el jugador va al inicio del tablero

    // constructor
    public Agujero(int pos) {
        this.posicion = pos;
        this.turnosAtrapado = 1; // 1 turno atrapado
        this.esPrimero = false;
    }

    // constructor con parametros completos
    public Agujero(int pos, boolean esPrimero) {
        this.posicion = pos;
        this.turnosAtrapado = 1;
        this.esPrimero = esPrimero;
    }

    //Getterd
    public int getTurnosAtrapado() {
        return turnosAtrapado;
    }

    public boolean isEsPrimero() {
        return esPrimero;
    }

    // setters
    public void setTurnosAtrapado(int turnosAtrapado) {
        this.turnosAtrapado = turnosAtrapado;
    }

    public void setEsPrimero(boolean esPrimero) {
        this.esPrimero = esPrimero;
    }

  
    public void realizarAccion(Partida p, Jugador j) {
        
    }
}