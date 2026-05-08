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

    // si la foca pasa por la casilla de un jugador le roba el 50% del inventario
    public String aplastarJugador(Pinguino p) {
        java.util.ArrayList<modelo.items.Item> lista = p.getInventario().getLista();
        if (lista.isEmpty()) return "";
        
        // Calculamos la mitad redondeando hacia arriba (si tiene 1, roba 1; si tiene 3, roba 2)
        int cantidadARobar = (int) Math.ceil(lista.size() / 2.0);
        int robados = 0;
        java.util.Random rnd = new java.util.Random();

        for (int i = 0; i < cantidadARobar; i++) {
            if (!lista.isEmpty()) {
                int idx = rnd.nextInt(lista.size());
                lista.remove(idx);
                robados++;
            }
        }
        if (robados == 0) return "";
        return "¡La foca ha pasado sobre " + p.getNombre() + " pero no tenía nada!!";
    }

    public void golpearJugador(Pinguino p, modelo.tablero.Tablero tablero) {
        p.setPosicion(0);
        System.out.println(" Foca golpea a " + p.getNombre() + " y lo manda al inicio.");
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