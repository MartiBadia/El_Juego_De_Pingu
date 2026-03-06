package Items;

public class BolaDeNieve extends Item {

    // Constructor 1
    public BolaDeNieve() {
        this.nombre = "Bola de Nieve";
        this.cantidad = 1;
    }

    // Constructor con cantidad personalizada y maximo de 6 bolas de cantidad
    public BolaDeNieve(int cantidad) {
        this.nombre = "Bola de Nieve";
        this.cantidad = cantidad;
    }

    
    public String toString() {
        return "Bola de Nieve (x" + cantidad + ")";
    }
}