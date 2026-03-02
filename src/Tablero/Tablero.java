package Tablero;

import Partida.Partida;
import Jugador.Jugador;
import java.util.ArrayList;

public class Tablero {
    private ArrayList<Casilla> casillas;

    public Tablero() {
        this.casillas = new ArrayList<>();
    }

    public void actualizarTablero() {}
    public ArrayList<Casilla> getCasillas() { return casillas; }
}