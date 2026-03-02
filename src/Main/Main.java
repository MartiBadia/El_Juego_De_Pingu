package Main;

import Menu.PantallaMenu;

public class Main {
    public Main() {}

    public static void main(String[] args) {
        Main instancia = new Main();
        instancia.jugar();
    }

    public void jugar() {
        PantallaMenu menuPrincipal = new PantallaMenu();
        menuPrincipal.menu();
    }
}