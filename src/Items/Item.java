package Items;

public abstract class Item {
    protected String nombre;   
    protected int cantidad;    

    // Constructor vacío
    public Item() {
    }

    // Constructor con parámetros
    public Item(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    // --- GETTERS ---
    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    // --- SETTERS ---
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return nombre + " (x" + cantidad + ")";
    }
}
