package Items;

public class Dado extends Item {
    private int max;
    private int min;
    private boolean esEspecial; 

    // Constructor dado normal (1-6)
    public Dado() {
        this.nombre = "Dado";
        this.cantidad = 1;
        this.max = 6;
        this.min = 1;
        this.esEspecial = false;
    }

    //dado lento o dado rápido 
    public Dado(String nombre, int min, int max, boolean esEspecial) {
        this.nombre = nombre;
        this.cantidad = 1;
        this.min = min;
        this.max = max;
        this.esEspecial = esEspecial;
    }

    // --- GETTERS ---
    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public boolean isEsEspecial() {
        return esEspecial;
    }

    // --- SETTERS ---
    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setEsEspecial(boolean esEspecial) {
        this.esEspecial = esEspecial;
    }

    public int tirarRandom() {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    @Override
    public String toString() {
        return nombre + " [" + min + "-" + max + "]" + (esEspecial ? " (especial)" : "");
    }
}