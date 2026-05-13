package modelo.jugador;

/**
 * Representa a las focas del juego, que actúan como obstáculos móviles.
 * Pueden ser sobornadas con comida para que no molesten.
 */
public class Foca extends Jugador {
    private boolean soborno;
    private int turnosBloqueada; 

    // Constructor
    // Crea una foca lista para jugar, inicialmente sin sobornos ni bloqueos
    public Foca(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
        this.posicion = 0;
        this.soborno = false;
        this.turnosBloqueada = 0;
    }

    // --- GETTERS ---
    // Indica si la foca ha sido sobornada (probablemente con peces)
    public boolean isSoborno() {
        return soborno;
    }

    // Devuelve cuántos turnos estará la foca sin poder moverse
    public int getTurnosBloqueada() {
        return turnosBloqueada;
    }

    // --- SETTERS ---
    // Cambia el estado de soborno de la foca
    public void setSoborno(boolean soborno) {
        this.soborno = soborno;
    }

    // Establece el tiempo que la foca estará bloqueada
    public void setTurnosBloqueada(int turnosBloqueada) {
        this.turnosBloqueada = turnosBloqueada;
    }

    // Mueve la foca n posiciones en el tablero
    @Override
    public void moverPosicion(int n) {
        this.posicion = this.posicion + n;
    }

    // si la foca pasa por la casilla de un jugador le roba el 50% del inventario
    // Lógica para cuando la foca pilla a un pingüino: le roba la mitad de sus cosas
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

    // Un golpe directo que manda al pingüino de vuelta a la casilla de salida
    public void golpearJugador(Pinguino p, modelo.tablero.Tablero tablero) {
        p.setPosicion(0);
        System.out.println(" Foca golpea a " + p.getNombre() + " y lo manda al inicio.");
    }

    // Devuelve si la foca está sobornada
    // Comprobación rápida para saber si está bajo soborno
    public boolean esSobornada() { 
        return soborno; 
    }

    // Muestra el nombre, color y si está bloqueada en formato texto
    @Override
    public String toString() {
        return "Foca " + nombre + " [" + color + "] Casilla:" + posicion
                + (turnosBloqueada > 0 ? " (bloqueada " + turnosBloqueada + " turnos)" : "");
    }
}