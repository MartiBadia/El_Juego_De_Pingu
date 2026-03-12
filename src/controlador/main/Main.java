package controlador.main;

import controlador.gestor.GestorPartida;
import modelo.jugador.Jugador;
import modelo.jugador.Pinguino;
import modelo.jugador.Foca;
import modelo.tablero.Tablero;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SIMULACIÓN - EL JUEGO DE PINGU ===");
        
        // 1. Inicializar el gestor (sin conexión para la simulación rápida)
        GestorPartida gestor = new GestorPartida();
        
        // 2. Crear jugadores
        ArrayList<Jugador> jugadores = new ArrayList<>();
        Pinguino p1 = new Pinguino("Marti", "Azul");
        Pinguino p2 = new Pinguino("Bernat", "Verde");
        Foca cpuFoca = new Foca("Foca Loca", "Gris");
        
        jugadores.add(p1);
        jugadores.add(p2);
        jugadores.add(cpuFoca);
        
        // 3. Generar tablero
        Tablero tablero = new Tablero();
        tablero.generarTableroAleatorio();
        System.out.println("Tablero generado con " + tablero.getTamaño() + " casillas.");
        
        // 4. Iniciar partida
        gestor.nuevaPartida(jugadores, tablero);
        
        System.out.println("\n--- COMIENZA LA PARTIDA ---");
        
        // 5. Bucle de juego
        int maxSimulacion = 100; // Por seguridad
        int turno = 1;
        
        while (!gestor.getPartida().isFinalizada() && turno <= maxSimulacion) {
            System.out.println("\n>> RONDA " + turno);
            gestor.ejecutarTurnoCompleto();
            
            // Mostrar estado breve
            for (Jugador j : jugadores) {
                System.out.println("  " + j.toString());
            }
            
            turno++;
            
            // Pequeña pausa opcional (simulada)
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }
        
        // 6. Resultado final
        System.out.println("\n--- PARTIDA FINALIZADA ---");
        if (gestor.getPartida().isFinalizada()) {
            Jugador ganador = gestor.getPartida().getGanador();
            System.out.println("¡EL GANADOR ES: " + (ganador != null ? ganador.getNombre() : "Nadie") + "!");
        } else {
            System.out.println("La partida terminó por límite de turnos.");
        }
        System.out.println("Turnos totales: " + gestor.getPartida().getTurnos());
    }
}
