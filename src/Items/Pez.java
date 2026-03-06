package Items;

public class Pez extends Item {

    // Constructor por defecto: 1 pez
    public Pez() {
        this.nombre = "Pez";
        this.cantidad = 1;
    }

    // Constructor con cantidad personalizada
    // El enunciado dice: maximo 2 peces (para sobornar a la foca)
    public Pez(int cantidad) {
        this.nombre = "Pez";
        this.cantidad = cantidad;
    }

    public String toString() {
        return "Pez (x" + cantidad + ")";
    }
}