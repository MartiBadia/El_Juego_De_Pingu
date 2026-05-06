package modelo.items;

/**
 * Representa el ítem Moto de Nieve.
 * Es un vehículo potente que permite al pingüino avanzar una gran distancia.
 */
public class MotoNieve extends Item {

    public MotoNieve() {
        super("Moto de Nieve", 1);
    }

    @Override
    public String toString() {
        return "🏍️ Moto de Nieve";
    }
}
