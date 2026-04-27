package controlador.gestor;

import modelo.items.BolaDeNieve;
import modelo.items.Dado;
import modelo.items.Pez;
import modelo.items.MotoNieve;
import modelo.jugador.Foca;
import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.tablero.Tablero;

import java.util.Random;
import java.util.ArrayList;

public class GestorJugador {

    private Random random;

    public GestorJugador() {
        this.random = new Random();
    }


    public void jugadorUsaItem(Pinguino p, String nombreItem) {
        ArrayList<modelo.items.Item> listaItems = p.getInventario().getLista();
        modelo.items.Item itemAUsar = null;
        
        int i = 0;
        while (i < listaItems.size() && itemAUsar == null) {
            modelo.items.Item it = listaItems.get(i);
            if (it.getNombre().equals(nombreItem)) {
                itemAUsar = it;
            }
            i++;
        }

        if (itemAUsar != null) {
            p.getInventario().quitarItem(itemAUsar);
            System.out.println(p.getNombre() + " usa: " + nombreItem);
            
            if (nombreItem.equals("Moto de Nieve")) {
                p.moverPosicion(20); // Efecto de la moto: avanza 20 casillas
                System.out.println(p.getNombre() + " ruge con la Moto de Nieve y avanza 20 casillas!");
            }
        } else {
            System.out.println(p.getNombre() + " no tiene " + nombreItem + " en el inventario.");
        }
    }

    public void jugadorUsaNuevo(Pinguino p, String nombreItem) {
        jugadorUsaItem(p, nombreItem);
    }

    /**
     * El jugador retrocede 'pasos' posiciones (recibe daño).
     * La posición no puede bajar de 0.
     */
    public void jugadorRecibeDaño(Jugador j, int pasos, Tablero t) {
        j.moverPosicion(-pasos);
        if (j.getPosicion() < 0) j.setPosicion(0);
        System.out.println(j.getNombre() + " retrocede " + pasos + " -> casilla " + j.getPosicion());
    }

    public void jugadorSetNuevo(Jugador j, int pasos, Tablero t) {
        jugadorRecibeDaño(j, pasos, t);
    }

    /**
     * Lógica al finalizar el turno de un jugador:
     * - Decrementa los contadores de congelado/bloqueo si procede.
     */
    public void jugadorFinalizaTurno(Jugador j) {
        if (j instanceof Pinguino) {
            Pinguino p = (Pinguino) j;
            if (p.getTurnosCongelado() > 0) {
                p.setTurnosCongelado(p.getTurnosCongelado() - 1);
                System.out.println(p.getNombre() + " sigue congelado. Turnos restantes: " + p.getTurnosCongelado());
            }
        }
        if (j instanceof Foca) {
            Foca f = (Foca) j;
            if (f.getTurnosBloqueada() > 0) {
                f.setTurnosBloqueada(f.getTurnosBloqueada() - 1);
                if (f.getTurnosBloqueada() == 0) {
                    f.setSoborno(false);
                    System.out.println("La foca ya no está bloqueada.");
                }
            }
        }
    }

    /**
     * Evento: el pingüino obtiene un pez (si no supera el máximo de 2).
     */
    public void pinguinoEventoPez(Pinguino p) {
        boolean añadido = p.getInventario().añadirItem(new Pez());
        if (añadido) {
            System.out.println(p.getNombre() + " obtiene un Pez.");
        } else {
            System.out.println(p.getNombre() + " ya tiene el máximo de peces (2).");
        }
    }

    /**
     * Evento: el pingüino obtiene entre 1 y 3 bolas de nieve aleatorias.
     */
    public void pinguinoEventoBolaDeNieve(Pinguino p) {
        int cantidad = random.nextInt(3) + 1; // 1 a 3
        int añadidas = 0;
        for (int i = 0; i < cantidad; i++) {
            if (p.getInventario().añadirItem(new BolaDeNieve())) añadidas++;
        }
        System.out.println(p.getNombre() + " obtiene " + añadidas + " Bola(s) de Nieve.");
    }

    /**
     * Evento: el pingüino obtiene un dado rápido (avanza 5-10, probabilidad baja).
     */
    public void pinguinoEventoDadoRapido(Pinguino p) {
        boolean añadido = p.getInventario().añadirItem(new Dado("Dado Rapido", 5, 10, true));
        if (añadido) {
            System.out.println(p.getNombre() + " obtiene un Dado Rapido [5-10].");
        } else {
            System.out.println(p.getNombre() + " ya tiene el máximo de dados (3).");
        }
    }

    
    public void pinguinoEventoDadoLento(Pinguino p) {
        boolean añadido = p.getInventario().añadirItem(new Dado("Dado Lento", 1, 3, true));
        if (añadido) {
            System.out.println(p.getNombre() + " obtiene un Dado Lento [1-3].");
        } else {
            System.out.println(p.getNombre() + " ya tiene el máximo de dados (3).");
        }
    }


    /**
     * Evento: el pingüino obtiene una moto de nieve (máximo 1).
     */
    public void pinguinoEventoMotoNieve(Pinguino p) {
        boolean añadido = p.getInventario().añadirItem(new MotoNieve());
        if (añadido) {
            System.out.println(p.getNombre() + " obtiene una Moto de Nieve.");
        } else {
            System.out.println(p.getNombre() + " ya tiene una moto de nieve.");
        }
    }


    public void pinguinoLuchaPinguino(Pinguino p1, Pinguino p2) {
        System.out.println("¡Batalla! " + p1.getNombre() + " vs " + p2.getNombre());
        p1.gestionarBatalla(p2);
    }


    public void focaInteractuaPinguino(Pinguino p, Foca f, Tablero tablero) {
        if (f.isSoborno()) {
            System.out.println("Foca ya está sobornada (soborno=" + f.isSoborno() + ")");
            return;
        }
        
        // La foca golpea al pingüino → al inicio (posición 0)
        f.golpearJugador(p, tablero);
        System.out.println("¡La foca golpea a " + p.getNombre() + "! -> casilla " + p.getPosicion());
    }

    // Versión sin tablero (compatibilidad con firma anterior)
    public void focaInteractuaPinguino(Pinguino p, Foca f) {
        System.out.println("Usa focaInteractuaPinguino(p, f, tablero) para la lógica completa.");
    }

    public void pinguinoPierdeUnTurno(Pinguino p) {
        p.setTurnosCongelado(p.getTurnosCongelado() + 1);
        System.out.println(p.getNombre() + " pierde un turno.");
    }

    public void pinguinoEvento(Pinguino p, String tipo) {
        switch (tipo) {
            case "Pez": pinguinoEventoPez(p); break;
            case "Bola": pinguinoEventoBolaDeNieve(p); break;
            case "DadoRapido": pinguinoEventoDadoRapido(p); break;
            case "DadoLento": pinguinoEventoDadoLento(p); break;
            case "MotoNieve": pinguinoEventoMotoNieve(p); break;
        }
    }

    void pinguinoPierdeItemAleatorio(Pinguino p) {
        modelo.items.Item item = p.getInventario().obtenerItemAleatorio();
        if (item != null) {
            p.getInventario().quitarItem(item);
            System.out.println(p.getNombre() + " pierde: " + item.getNombre());
        }
    }
}