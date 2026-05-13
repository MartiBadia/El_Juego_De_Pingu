package modelo.jugador;

import modelo.items.Inventario;

/**
 * Representa a los jugadores pingüinos de la partida.
 * Tienen un inventario para guardar objetos y pueden ser congelados por el suelo quebradizo.
 */
public class Pinguino extends Jugador {
    private Inventario inv;
    private int turnosCongelado; 

    // Constructor
    // Crea un pingüino con su inventario vacío y sin estar congelado
    public Pinguino(String nombre, String col) {
        this.nombre = nombre;
        this.color = col;  
        this.posicion = 0;
        this.inv = new Inventario();
        this.turnosCongelado = 0;
    }

    // --- GETTERS ---
    // Nos da el inventario del pingüino para ver qué objetos tiene
    public Inventario getInventario() {
        return inv;
    }

    // Indica cuántos turnos le quedan sin poder moverse
    public int getTurnosCongelado() {
        return turnosCongelado;
    }

    // --- SETTERS ---
    // Cambia el inventario completo (útil para cargar partidas)
    public void setInventario(Inventario inv) {
        this.inv = inv;
    }

    // Establece el número de turnos que el pingüino estará atrapado
    public void setTurnosCongelado(int turnosCongelado) {
        this.turnosCongelado = turnosCongelado;
    }

    // Mueve al pingüino sumando n a su posición
    @Override
    public void moverPosicion(int n) {
        this.posicion = this.posicion + n;
    }

    // Lógica para cuando dos pingüinos pelean con bolas de nieve. 
    // Gana el que más bolas tenga y el otro retrocede la diferencia.
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

    // Gasta un objeto del inventario
    public void usarItem(modelo.items.Item i) {
        inv.quitarItem(i);
    }

    // Guarda un objeto nuevo en el inventario
    public void añadirItem(modelo.items.Item i) {
        inv.añadirItem(i);
    }

    // Elimina un objeto del inventario sin usarlo
    public void quitarItem(modelo.items.Item i) {
        inv.quitarItem(i);
    }

    // Muestra el estado del pingüino y su inventario de forma legible
    @Override
    public String toString() {
        return " Pingüino " + nombre + " [" + color + "] Casilla:" + posicion + " | " + inv.toString();
    }
}