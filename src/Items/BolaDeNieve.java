package Items;

public class BolaDeNieve extends Item {

    // Constructor por defecto: 1 bola de nieve
    public BolaDeNieve() {
        this.nombre = "Bola de Nieve";
        this.cantidad = 1;
    }

    // Constructor con cantidad personalizada
    // máximo 6 bolas de nieve en el inventario
    public BolaDeNieve(int cantidad) {
        this.nombre = "Bola de Nieve";
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Bola de Nieve (x" + cantidad + ")";
    }
}