package controlador.gestor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GestorUsuarios {
    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private Map<String, String> usuarios;

    public GestorUsuarios() {
        usuarios = new HashMap<>();
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    usuarios.put(partes[0].trim(), partes[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void guardarUsuario(String username, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            bw.write(username + "," + password);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }
    }

    public boolean registrarUsuario(String username, String password) {
        if (usuarios.containsKey(username)) {
            return false; // El usuario ya existe
        }
        usuarios.put(username, password);
        guardarUsuario(username, password);
        return true;
    }

    public boolean validarLogin(String username, String password) {
        String pwdGuardada = usuarios.get(username);
        return pwdGuardada != null && pwdGuardada.equals(password);
    }
}
