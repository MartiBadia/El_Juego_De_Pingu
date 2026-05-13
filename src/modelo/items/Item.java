package modelo.items;

/**
 * Clase base para todos los objetos del juego.
 * Los pingüinos pueden guardarlos en su inventario para usarlos después.
 */
public abstract class Item {
    protected String nombre;   
    protected int cantidad;    

    // Constructor vacío
    // Constructor por defecto
    public Item() {
    }

    // Constructor con parámetros
    // Crea un objeto con su nombre y una cantidad inicial
    public Item(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    // --- GETTERS ---
    // Devuelve el nombre descriptivo del objeto
    public String getNombre() {
        return nombre;
    }

    // Nos dice cuántas unidades tenemos de este objeto
    public int getCantidad() {
        return cantidad;
    }

    // --- SETTERS ---
    // Cambia el nombre del objeto
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Actualiza la cantidad de unidades
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Muestra el objeto y su cantidad en texto
    @Override
    public String toString() {
        return nombre + " (x" + cantidad + ")";
    }
}
