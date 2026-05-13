package modelo.items;

// Un objeto especial que permite avanzar mucho más rápido por el tablero
public class MotoNieve extends Item {

    // Constructor básico para la moto
    public MotoNieve() {
        super("Moto de Nieve", 1);
    }

    @Override
    public String toString() {
        return "🏍️ Moto de Nieve";
    }
}
