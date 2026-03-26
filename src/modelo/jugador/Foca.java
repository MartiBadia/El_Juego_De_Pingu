package modelo.jugador;

public class Foca extends Jugador {
    private boolean soborno;
    private int turnosBloqueada; 

    // Constructor
    public Foca(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
        this.posicion = 0;
        this.soborno = false;
        this.turnosBloqueada = 0;
    }

    // --- GETTERS ---
    public boolean isSoborno() {
        return soborno;
    }

    public int getTurnosBloqueada() {
        return turnosBloqueada;
    }

    // --- SETTERS ---
    public void setSoborno(boolean soborno) {
        this.soborno = soborno;
    }

    public void setTurnosBloqueada(int turnosBloqueada) {
        this.turnosBloqueada = turnosBloqueada;
    }

    @Override
    public void moverPosicion(int n) {
        this.posicion = this.posicion + n;
    }

    //si la foca pasa por la casilla de un jugador le hace perder la mitad del inventario
    public void aplastarJugador(Pinguino p) {
        java.util.ArrayList<modelo.items.Item> lista = p.getInventario().getLista();
        int mitad = lista.size() / 2;
        for (int i = 0; i < mitad; i++) {
            lista.remove(0);
        }
    }

    public void golpearJugador(Pinguino p, modelo.tablero.Tablero tablero) {
        p.setPosicion(0);
        System.out.println("Foca golpea a " + p.getNombre() + " y lo manda al inicio.");
    }

    // Devuelve si la foca está sobornada
    public boolean esSobornada() { 
        return soborno; 
    }

    @Override
    public String toString() {
        return "Foca " + nombre + " [" + color + "] Casilla:" + posicion
                + (turnosBloqueada > 0 ? " (bloqueada " + turnosBloqueada + " turnos)" : "");
    }
}