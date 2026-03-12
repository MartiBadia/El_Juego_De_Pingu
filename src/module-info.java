/**
 * 
 */
/**
 * 
 */
module Pingu {
    requires java.sql;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

 
    opens vista.menu to javafx.fxml;
    opens vista to javafx.fxml;

    exports controlador.gestionbbdd;
    exports controlador.gestor;
    exports controlador.main;
    exports modelo.items;
    exports modelo.jugador;
    exports modelo.partida;
    exports modelo.tablero;
    exports vista.menu;
    exports vista;

}