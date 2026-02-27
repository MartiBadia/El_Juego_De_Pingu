package Menu;

import Partida.GestorPartida;

public class PantallaMenu {
    private GestorPartida gestor;

    public PantallaMenu() {
        this.gestor = new GestorPartida();
    }

    public void menu() {
        System.out.println("--- MENU PRINCIPAL ---");
    }

    public void botonNuevaPartida() {
        gestor.nuevaPartida(null, null);
    }

    public void botonCargaPartida(int id) {
        gestor.cargaPartida(id);
    }

    public void botonSalir() {
        System.exit(0);
    }
}
