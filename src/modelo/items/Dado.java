package modelo.items;

public class Dado extends Item {
    private int max;
    private int min;
    private boolean esEspecial; 

    // Constructor dado normal (1-6)
    // Crea un dado estándar de 6 caras (del 1 al 6)
    public Dado() {
        this.nombre = "Dado";
        this.cantidad = 1;
        this.max = 6;
        this.min = 1;
        this.esEspecial = false;
    }

    //dado lento o dado rápido 
    // Crea un dado personalizado (por ejemplo, uno lento de 1-3 o uno rápido de 5-10)
    public Dado(String nombre, int min, int max, boolean esEspecial) {
        this.nombre = nombre;
        this.cantidad = 1;
        this.min = min;
        this.max = max;
        this.esEspecial = esEspecial;
    }

    // --- GETTERS ---
    // Nos dice el valor máximo que puede salir en este dado
    public int getMax() {
        return max;
    }

    // Nos dice el valor mínimo que puede salir
    public int getMin() {
        return min;
    }

    // Indica si el dado tiene algún efecto o rango especial
    public boolean isEsEspecial() {
        return esEspecial;
    }

    // --- SETTERS ---
    // Cambia el valor máximo posible
    public void setMax(int max) {
        this.max = max;
    }

    // Cambia el valor mínimo posible
    public void setMin(int min) {
        this.min = min;
    }

    // Marca el dado como especial o normal
    public void setEsEspecial(boolean esEspecial) {
        this.esEspecial = esEspecial;
    }

    // "Lanza" el dado y nos devuelve un número aleatorio dentro del rango [min, max]
    public int tirarRandom() {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    @Override
    public String toString() {
        return nombre + " [" + min + "-" + max + "]" + (esEspecial ? " (especial)" : "");
    }
}