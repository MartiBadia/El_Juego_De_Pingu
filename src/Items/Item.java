package Items;

public abstract class Item {
    protected String nombre;   
    protected int cantidad;    

    // Constructor sin nada
    public Item() {
    }

    // Constructor con parametros
    public Item(String nombre, int cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    //Getters
    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    //Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String toString() {
        return nombre + " (x" + cantidad + ")";
    }
}
