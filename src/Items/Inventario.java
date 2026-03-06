package Items;

import java.util.ArrayList;
import java.util.Random;

public class Inventario {
    private ArrayList<Item> lista;

    // Limites del inventario segun el enunciado
    public static final int MAX_DADOS = 3;        // Máximo 3 dados
    public static final int MAX_PECES = 2;        // Máximo 2 peces
    public static final int MAX_BOLAS_NIEVE = 6;  // Máximo 6 bolas de nieve

    public Inventario() {
        this.lista = new ArrayList<>();
    }

    //Getter
    public ArrayList<Item> getLista() { 
        return lista; 
    }

    //Setters
    public void setLista(ArrayList<Item> lista) {
        this.lista = lista;
    }

    // Añade un item si no se supera el limite correspondiente
    public boolean añadirItem(Item item) {
        if (item instanceof Dado && contarPorTipo("Dado") >= MAX_DADOS) return false;
        if (item instanceof Pez && contarPorTipo("Pez") >= MAX_PECES) return false;
        if (item instanceof BolaDeNieve && contarPorTipo("Bola de Nieve") >= MAX_BOLAS_NIEVE) return false;
        lista.add(item);
        return true;
    }

    // Quita un ítem de la lista
    public boolean quitarItem(Item item) {
        return lista.remove(item);
    }

    // Cuenta items de un tipo concreto por nombre
    public int contarPorTipo(String nombre) {
        int count = 0;
        for (Item i : lista) {
            if (i.getNombre().equals(nombre)) count++;
        }
        return count;
    }

    // Devuelve un item aleatorio del inventario (para el evento "perder objeto aleatorio")
    public Item obtenerItemAleatorio() {
        if (lista.isEmpty()) return null;
        return lista.get(new Random().nextInt(lista.size()));
    }

    // Vacia completamente el inventario
    public void vaciarInventario() {
        lista.clear();
    }

    public boolean estaVacio() {
        return lista.isEmpty();
    }

    public int getTotalItems() {
        return lista.size();
    }

    public String toString() {
        if (lista.isEmpty()) return "Inventario vacío";
        StringBuilder sb = new StringBuilder("Inventario: ");
        for (Item i : lista) sb.append(i.toString()).append(" | ");
        return sb.toString();
    }
}