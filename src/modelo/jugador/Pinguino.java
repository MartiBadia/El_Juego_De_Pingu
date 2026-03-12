package modelo.jugador;

import modelo.items.Inventario;

public class Pinguino extends Jugador {
    private Inventario inv;
    private int turnosCongelado; 

    // Constructor
    public Pinguino(String nombre, String col) {
        this.nombre = nombre;
        this.color = col;  
        this.posicion = 0;
        this.inv = new Inventario();
        this.turnosCongelado = 0;
    }

    // --- GETTERS ---
    public Inventario getInventario() {
        return inv;
    }

    public int getTurnosCongelado() {
        return turnosCongelado;
    }

    // --- SETTERS ---
    public void setInventario(Inventario inv) {
        this.inv = inv;
    }

    public void setTurnosCongelado(int turnosCongelado) {
        this.turnosCongelado = turnosCongelado;
    }

    @Override
    public void moverPosicion(int n) {
        this.posicion = this.posicion + n;
    }

    public void gestionarBatalla(Pinguino p) {
        // 1. Ambos cuentan sus bolas de nieve
        int bolasThis = this.inv.contarPorTipo("Bola de Nieve");
        int bolasP    = p.getInventario().contarPorTipo("Bola de Nieve");

        // 2. Ambos gastan TODAS sus bolas de nieve
        for (modelo.items.Item item : new java.util.ArrayList<>(this.inv.getLista())) {
            if (item instanceof modelo.items.BolaDeNieve) this.inv.quitarItem(item);
        }
        for (modelo.items.Item item : new java.util.ArrayList<>(p.getInventario().getLista())) {
            if (item instanceof modelo.items.BolaDeNieve) p.getInventario().quitarItem(item);
        }

        // 3. El perdedor retrocede tantas casillas como la diferencia
        int diferencia = bolasThis - bolasP;
        if (diferencia > 0) {
            p.moverPosicion(-diferencia);
        } else if (diferencia < 0) {
            this.moverPosicion(diferencia);
        }
    }

    public void usarItem(modelo.items.Item i) {
        inv.quitarItem(i);
    }

    public void añadirItem(modelo.items.Item i) {
        inv.añadirItem(i);
    }

    public void quitarItem(modelo.items.Item i) {
        inv.quitarItem(i);
    }

    @Override
    public String toString() {
        return "Pingüino " + nombre + " [" + color + "] Casilla:" + posicion + " | " + inv.toString();
    }
}