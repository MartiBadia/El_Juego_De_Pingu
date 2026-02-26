package Items;

public class Dado extends Item {
    private int max;
    private int min;

    public Dado() {
        this.nombre = "Dado";
        this.cantidad = 1;
        this.max = 6;
        this.min = 1;
    }

    public int tirarRandom() {
        return (int)(Math.random() * (max - min + 1) + min);
    }
}