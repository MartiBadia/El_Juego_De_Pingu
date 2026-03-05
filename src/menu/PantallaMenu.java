package menu;
import partida.GestorPartida;

public class PantallaMenu {
    private GestorPartida gestor = new GestorPartida();
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