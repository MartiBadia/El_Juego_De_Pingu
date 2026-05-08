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
        Casilla resultado = null;
        int i = 0;
        while (i < casillas.size() && resultado == null) {
            Casilla c = casillas.get(i);
            if (c.getPosicion() == posicion) {
                resultado = c;
            }
            i++;
        }
        
        if (resultado == null) {
            resultado = new CasillaNormal(posicion);
        }
        
        return resultado;
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

        int ultimoAgujeroPos = 0; 
        
        int[] ultimaPosTipo = {-10, -10, -10, -10, -10}; 
        int countOsos = 0;
        int countAgujeros = 0;
        int countTrineos = 0;
        int countEventos = 0;

        for (int i = 1; i < this.tamano - 1; i++) {
            if (Math.random() < 0.35) {
                ArrayList<Integer> tiposDisponibles = new ArrayList<>();
                for (int t = 0; t < 5; t++) {
                    if (i - ultimaPosTipo[t] < 4) continue;
                    if (t == 0 && countOsos >= 3) continue;
                    tiposDisponibles.add(t);
                }
                tiposDisponibles.add(5);

                if (!tiposDisponibles.isEmpty()) {
                    int tipo = tiposDisponibles.get((int)(Math.random() * tiposDisponibles.size()));
                    Casilla c = null;

                    switch (tipo) {
                        case 0: 
                            c = new Oso(i);
                            ultimaPosTipo[0] = i;
                            countOsos++;
                            break;
                        case 1: 
                            c = new Agujero(i, ultimoAgujeroPos);
                            ultimoAgujeroPos = i;
                            ultimaPosTipo[1] = i;
                            countAgujeros++;
                            break;
                        case 2: 
                            c = new Trineo(i);
                            ultimaPosTipo[2] = i;
                            countTrineos++;
                            break;
                        case 3: 
                            c = new Evento(i);
                            ultimaPosTipo[3] = i;
                            countEventos++;
                            break;
                        case 4: 
                            c = new SueloQuebradizo(i);
                            ultimaPosTipo[4] = i;
                            break;
                        case 5: 
                            c = new CasillaNormal(i);
                            break;
                    }
                    if (c != null) casillas.add(c);
                }
            }
        }

        // --- ASEGURAR MÍNIMOS ---
        asegurarMinimo(countOsos, 1, "Oso");        // Min 1 Oso
        asegurarMinimo(countTrineos, 4, "Trineo");   // Min 4 Trineos
        asegurarMinimo(countEventos, 4, "Evento");   // Min 4 Eventos
        asegurarMinimo(countAgujeros, 2, "Agujero"); // Min 2 Agujeros

        // Ordenar casillas por posición para que los Agujeros funcionen bien
        casillas.sort((c1, c2) -> Integer.compare(c1.getPosicion(), c2.getPosicion()));
        
        // Re-vincular Agujeros para que apunten al anterior correctamente
        int prevAgujero = 0;
        for (Casilla c : casillas) {
            if (c instanceof Agujero) {
                ((Agujero)c).setPosicionAgujeroAnterior(prevAgujero);
                prevAgujero = c.getPosicion();
            }
        }
        
        casillas.add(new CasillaSalida(0));
        casillas.add(new CasillaMeta(this.tamano - 1));
    }

    private void asegurarMinimo(int actual, int minimo, String tipo) {
        int count = 0;
        // Contar cuántos hay realmente (por si acaso los parámetros 'actual' no están actualizados)
        for(Casilla c : casillas) {
            if (tipo.equals("Oso") && c instanceof Oso) count++;
            if (tipo.equals("Trineo") && c instanceof Trineo) count++;
            if (tipo.equals("Evento") && c instanceof Evento) count++;
            if (tipo.equals("Agujero") && c instanceof Agujero) count++;
        }

        while (count < minimo) {
            int randomPos = 1 + (int)(Math.random() * (this.tamano - 2));
            
            boolean ocupada = false;
            int idxOcupada = 0;
            while (idxOcupada < casillas.size() && !ocupada) {
                if (casillas.get(idxOcupada).getPosicion() == randomPos) {
                    ocupada = true;
                }
                idxOcupada++;
            }

            if (!ocupada) {
                boolean distanciaOk = true;
                int idxDistancia = 0;
                while (idxDistancia < casillas.size() && distanciaOk) {
                    Casilla c = casillas.get(idxDistancia);
                    boolean mismoTipo = false;
                    if (tipo.equals("Oso") && c instanceof Oso) mismoTipo = true;
                    if (tipo.equals("Trineo") && c instanceof Trineo) mismoTipo = true;
                    if (tipo.equals("Evento") && c instanceof Evento) mismoTipo = true;
                    if (tipo.equals("Agujero") && c instanceof Agujero) mismoTipo = true;
                    
                    if (mismoTipo && Math.abs(c.getPosicion() - randomPos) < 4) {
                        distanciaOk = false;
                    }
                    idxDistancia++;
                }

                if (distanciaOk) {
                    Casilla nueva = null;
                    if (tipo.equals("Oso")) nueva = new Oso(randomPos);
                    if (tipo.equals("Trineo")) nueva = new Trineo(randomPos);
                    if (tipo.equals("Evento")) nueva = new Evento(randomPos);
                    if (tipo.equals("Agujero")) nueva = new Agujero(randomPos, 0); // Se re-vinculará después
                    
                    if (nueva != null) {
                        casillas.add(nueva);
                        count++;
                    }
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