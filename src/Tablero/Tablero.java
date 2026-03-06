package Tablero;

import Partida.Partida;
import Jugador.Jugador;
import java.util.ArrayList;

public class Tablero {
    private ArrayList<Casilla> casillas;
    public static final int TAMAÑO_MINIMO = 50; // minimo de 50 casillas

    // Constructor crea el tablero sin nada
    public Tablero() {
        this.casillas = new ArrayList<>();
    }

    //getter
    public ArrayList<Casilla> getCasillas() { return casillas; }

    public int getTamaño() { return casillas.size(); }

    // Obtiene la casilla que corresponde a una posición concreta del jugador
    public Casilla getCasillaEnPosicion(int posicion) {
        for (Casilla c : casillas) {
            if (c.getPosicion() == posicion) return c;
        }
        return null; // Casilla normal (sin evento especial)
    }

    //setters 
    public void setCasillas(ArrayList<Casilla> casillas) {
        this.casillas = casillas;
    }

    public void añadirCasilla(Casilla c) {
        casillas.add(c);
    }

    public void actualizarTablero() {}

   
    public String toString() {
        return "Tablero con " + casillas.size() + " casillas";
    }
}