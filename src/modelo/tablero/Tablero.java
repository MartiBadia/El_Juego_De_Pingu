package modelo.tablero;

import java.util.ArrayList;

public class Tablero {
    private ArrayList<Casilla> casillas;
    private int tamano; // Total de posiciones del tablero
    public static final int TAMANO_MINIMO = 50; 

    // Constructor (crea el tablero vacío)
    public Tablero() {
        this.casillas = new ArrayList<>();
        this.tamano = TAMANO_MINIMO;
    }

    // --- GETTER ---
    public ArrayList<Casilla> getCasillas() { return casillas; }

    public int getTamaño() { return tamano; }

    // Obtiene la casilla que corresponde a una posición concreta del jugador
    public Casilla getCasillaEnPosicion(int posicion) {
        for (Casilla c : casillas) {
            if (c.getPosicion() == posicion) return c;
        }
        return null; // Casilla normal (sin efecto especial)
    }

    // --- SETTER ---
    public void setCasillas(ArrayList<Casilla> casillas) {
        this.casillas = casillas;
    }

    public void añadirCasilla(Casilla c) {
        casillas.add(c);
    }

    
    public void generarTableroAleatorio() {
        casillas.clear();
        this.tamano = TAMANO_MINIMO; // Exactamente 50 casillas.

        int ultimoAgujeroPos = 0; // Referencia para el retroceso de Agujeros y Suelos
        Trineo ultimoTrineo = null;

        for (int i = 1; i < this.tamano - 1; i++) {
            // Probabilidad de casilla especial (35%)
            if (Math.random() < 0.35) {
                int tipo = (int)(Math.random() * 6);
                Casilla c = null;

                switch (tipo) {
                    case 0: // Oso 
                        c = new Oso(i);
                        break;
                    case 1: // Agujero 
                        c = new Agujero(i, ultimoAgujeroPos);
                        ultimoAgujeroPos = i; // Este pasa a ser el anterior para el siguiente
                        break;
                    case 2: // Trineo 
                        c = new Trineo(i);
                        if (ultimoTrineo != null) {
                            ultimoTrineo.setPosicionSiguienteTrineo(i);
                        }
                        ultimoTrineo = (Trineo) c;
                        break;
                    case 3: // Evento
                        c = new Evento(i);
                        break;
                    case 4: // Suelo Quebradizo
                        c = new SueloQuebradizo(i);
                        break;
                    case 5:
                    	c = new MotoNieve(i);
                    	break;
                    
                }

                if (c != null) {
                    casillas.add(c);
                }
            }
        }
    }

    public void actualizarTablero() {
        // En un futuro para efectos dinámicos
    }

    @Override
    public String toString() {
        return "Tablero de " + tamano + " casillas con " + casillas.size() + " casillas especiales.";
    }
}