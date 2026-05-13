package modelo.items;

import java.util.ArrayList;
import java.util.Random;

/**
 * Gestiona la colección de objetos que lleva un pingüino.
 * Controla los límites de carga para cada tipo de objeto.
 */
public class Inventario {
    private ArrayList<Item> lista;

    // Límites del inventario según el enunciado
    public static final int MAX_DADOS = 3;    
    public static final int MAX_PECES = 2;        
    public static final int MAX_BOLAS_NIEVE = 6; 
    public static final int MAX_MOTOS = 1;

    // Prepara una mochila vacía para el jugador
    public Inventario() {
        this.lista = new ArrayList<>();
    }

    // --- GETTER ---
    // Nos da la lista de todos los cacharros que lleva el jugador
    public ArrayList<Item> getLista() { 
        return lista; 
    }

    // --- SETTER ---
    // Permite cambiar todo el inventario de una vez
    public void setLista(ArrayList<Item> lista) {
        this.lista = lista;
    }

    // Añade un ítem si no se supera el límite correspondiente
    // Intenta meter un objeto en la mochila. Si ya está llena de ese tipo, devuelve false.
    public boolean añadirItem(Item item) {
        if (item instanceof Dado && contarPorTipo("Dado") >= MAX_DADOS) {
        	return false;
        }
        if (item instanceof Pez && contarPorTipo("Pez") >= MAX_PECES) {
        	return false;
        }
        if (item instanceof BolaDeNieve && contarPorTipo("Bola de Nieve") >= MAX_BOLAS_NIEVE) {
        	return false;
        }
        if (item instanceof MotoNieve && contarPorTipo("Moto de Nieve") >= MAX_MOTOS) {
            return false;
        }
        lista.add(item);
        return true;
    }

    // Quita un ítem de la lista
    // Saca un objeto específico de la mochila
    public boolean quitarItem(Item item) {
        return lista.remove(item);
    }
    
    // Busca un objeto por su nombre exacto
    public Item obtenerItemPorNombre(String nombre) {
        Item encontrado = null;
        int i = 0;
        while (i < lista.size() && encontrado == null) {
            Item item = lista.get(i);
            if (item.getNombre().equals(nombre)) {
                encontrado = item;
            }
            i++;
        }
        return encontrado;
    }

    // Cuenta ítems de un tipo concreto por nombre
    // Cuenta cuántos objetos de un tipo tenemos (ej: cuántos peces llevamos)
    public int contarPorTipo(String nombre) {
        int count = 0;
        for (Item i : lista) {
            if (i.getNombre().equals(nombre)) count++;
        }
        return count;
    }

    // Devuelve un ítem aleatorio del inventario (para el evento "perder objeto aleatorio")
    // Elige un objeto al azar para perderlo (el pez suele estar más a salvo)
    public Item obtenerItemAleatorio() {
        if (lista.isEmpty()) return null;
        
        // Intentar no perder el pez si hay otros objetos
        ArrayList<Item> noPeces = new ArrayList<>();
        for (Item it : lista) {
            if (!(it instanceof Pez)) noPeces.add(it);
        }
        
        if (!noPeces.isEmpty()) {
            return noPeces.get(new Random().nextInt(noPeces.size()));
        }
        
        return lista.get(new Random().nextInt(lista.size()));
    }

    // Vacía completamente el inventario
    // Lo tira todo al suelo, deja el inventario a cero
    public void vaciarInventario() {
        lista.clear();
    }

    // Comprueba si la mochila está totalmente vacía
    public boolean estaVacio() {
        return lista.isEmpty();
    }

    // Nos da el número total de bultos que llevamos en la mochila
    public int getTotalItems() {
        return lista.size();
    }

    @Override
    public String toString() {
        if (lista.isEmpty()) return "Inventario vacío";
        StringBuilder sb = new StringBuilder("Inventario: ");
        for (Item i : lista) sb.append(i.toString()).append(" | ");
        return sb.toString();
    }
}