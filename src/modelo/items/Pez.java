package modelo.items;

public class Pez extends Item {

    // Constructor por defecto: 1 pez
    // Crea un pez (sirve para sobornar focas)
    public Pez() {
        this.nombre = "Pez";
        this.cantidad = 1;
    }

    // Constructor con cantidad personalizada
    // Crea un grupo de peces
    public Pez(int cantidad) {
        this.nombre = "Pez";
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Pez (x" + cantidad + ")";
    }
}