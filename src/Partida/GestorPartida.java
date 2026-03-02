package Partida;

import Tablero.Tablero;
import Jugador.Jugador;
import GestionBBDD.BBDD;
import java.util.ArrayList;
import java.util.Random;

public class GestorPartida {
    private Partida partida;
    private GestorTablero gestorTablero;
    private GestorJugador gestorJugador;
    private BBDD bbdd;
    private Random random;

    public GestorPartida() {
        this.random = new Random();
        this.bbdd = new BBDD();
    }

    public void nuevaPartida(ArrayList<Jugador> jugadores, Tablero tablero) {
        this.partida = new Partida(tablero, jugadores);
    }

    public int tirarDado(Jugador j) {
        return random.nextInt(6) + 1;
    }

    public void ejecutarTurnoCompleto() {
        for (Jugador j : partida.getJugadores()) {
            procesarTurnoJugador(j);
        }
    }

    public void procesarTurnoJugador(Jugador j) {
        int avance = tirarDado(j);
        j.moverPosicion(avance);
    }

    public void actualizarEstadoTablero() {
    	
    }
    public void siguienteTurno() {
    	
    }
    public Partida getPartida() { 
    	return this.partida; 
    	
    }
    public void guardarPartida() { 
    	bbdd.guardarBBDD(this.partida); 
    	
    }
    public void cargaPartida(int id) { 
    	this.partida = (Partida) bbdd.cargarBBDD(id); 
    	
    }
}