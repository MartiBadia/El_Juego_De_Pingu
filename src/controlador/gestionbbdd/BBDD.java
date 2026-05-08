package controlador.gestionbbdd;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import modelo.partida.Partida;

public class BBDD {

	public static Connection conectarBaseDatos(Scanner scan) {
		System.out.println("Selecciona centro o fuera de centro (CENTRO/FUERA):");
		String entorno = scan.nextLine().trim().toLowerCase();
		System.out.println("¿Usuario?");
		String user = scan.nextLine().trim();
		System.out.println("¿Contraseña?");
		String pwd = scan.nextLine();
		return conectarBaseDatos(user, pwd, entorno);
	}

	public static Connection conectarBaseDatos(String user, String pwd, String entorno) {
		String url = entorno.equalsIgnoreCase("centro") 
				? "jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2"
				: "jdbc:oracle:thin:@//oracle.ilerna.com:1521/XEPDB2";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, user, pwd);
			if (con != null && con.isValid(5)) {
				System.out.println("Conectados a la base de datos (" + entorno.toUpperCase() + ").");
				return con;
			}
		} catch (Exception e) {
			System.out.println("Error de conexión: " + e.getMessage());
		}
		return null;
	}

	public static Connection conectarPredeterminado() {
		// Intentamos conectar con las credenciales de grupo exigidas
		Connection con = conectarBaseDatos("DW2526_GR09_PINGU", "AMBHL00", "FUERA");
		if (con == null) con = conectarBaseDatos("DW2526_GR09_PINGU", "AMBHL00", "CENTRO");
		
		return con;
	}

	private int obtenerSiguienteID(Connection con) {
		String sql = "SELECT SEQ_GENERAL.NEXTVAL FROM DUAL";
		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			System.err.println("Error al obtener ID de la secuencia: " + e.getMessage());
		}
		return 0;
	}
	// ══════════════════════════════════════════════════
	//  MÉTODOS DE UTILIDAD
	// ══════════════════════════════════════════════════

	public boolean registrarUsuario(Connection con, String user, String pass) {
		if (con == null) return false;
		String sql = "INSERT INTO USUARIOS_JUEGO (USERNAME, PASSWORD) VALUES (?, ?)";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, user);
			pstmt.setString(2, pass);
			int filas = pstmt.executeUpdate();
			return filas > 0;
		} catch (SQLException e) {
			System.err.println("Error al registrar usuario: " + e.getMessage());
			return false;
		}
	}

	public boolean loginUsuario(Connection con, String user, String pass) {
		if (con == null) return false;
		String sql = "SELECT USERNAME FROM USUARIOS_JUEGO WHERE USERNAME = ? AND PASSWORD = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, user);
			pstmt.setString(2, pass);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("Error en login: " + e.getMessage());
			return false;
		}
	}

	public boolean existeUsuario(Connection con, String user) {
		if (con == null) return false;
		String sql = "SELECT USERNAME FROM USUARIOS_JUEGO WHERE USERNAME = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, user);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			System.err.println("Error al verificar existencia: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Cierra la conexión con la BBDD.
	 */
	public static void cerrar(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ignored) {
			}
		}
	}

	/**
	 * Realiza una inserción en la base de datos.
	 *
	 * @param con Objeto Connection que representa la conexión a la base de datos.
	 * @param sql Sentencia SQL de inserción que hayáis creado.
	 */
	public static int insert(Connection con, String sql) {
		return executeInsUpDel(con, sql, "Insert");
	}

	/**
	 * Realiza una actualización en la base de datos.
	 *
	 * @param con Objeto Connection que representa la conexión a la base de datos.
	 * @param sql Sentencia SQL de actualización que hayáis creado.
	 */
	public static int update(Connection con, String sql) {
		return executeInsUpDel(con, sql, "Update");
	}

	/**
	 * Realiza una eliminación en la base de datos.
	 *
	 * @param con Objeto Connection que representa la conexión a la base de datos.
	 * @param sql Sentencia SQL de eliminación que hayáis creado.
	 */
	public static int delete(Connection con, String sql) {
		return executeInsUpDel(con, sql, "Delete");
	}

	/**
	 * Realiza una consulta en la base de datos y devuelve los resultados.
	 *
	 * @param con Objeto Connection que representa la conexión a la base de datos.
	 * @param sql Sentencia SQL de consulta.
	 * @return Devuelve un ArrayList con todas las filas del SELECT. Cada fila es un
	 *         Map con sus columnas (columna -> valor).
	 */
	public static ArrayList<LinkedHashMap<String, String>> select(Connection con, String sql) {

		ArrayList<LinkedHashMap<String, String>> resultados = new ArrayList<>();

		if (con == null) {
			System.out.println("No hay conexión. Llama antes a conectarBaseDatos().");
			return resultados;
		}

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

			ResultSetMetaData meta = rs.getMetaData();
			int numColumnas = meta.getColumnCount();

			while (rs.next()) {
				LinkedHashMap<String, String> fila = new LinkedHashMap<>();

				for (int i = 1; i <= numColumnas; i++) {
					String columna = meta.getColumnLabel(i);
					String valor = rs.getString(i);
					fila.put(columna, valor);
				}

				resultados.add(fila);
			}

		} catch (SQLException e) {
			System.out.println("Error en SELECT: " + e.getMessage());
		}

		return resultados;
	}

	/**
	 * Imprime los resultados de una consulta SELECT en la base de datos. EN ESTE
	 * CASO SÍ PODÉIS IMPRIMIR MÁS DE UNA FILA.
	 *
	 * @param con                         Objeto Connection que representa la
	 *                                    conexión a la base de datos.
	 * @param sql                         Sentencia SQL de consulta.
	 * @param listaElementosSeleccionados Array de Strings con los nombres de las
	 *                                    columnas seleccionadas.
	 */
	public static void print(Connection con, String sql, String[] listaElementosSeleccionados) {
		if (con == null) {
			System.out.println("No hay conexión. Llama antes a conectarBaseDatos().");
			return;
		}

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

			int fila = 0;
			boolean hayResultados = false;

			while (rs.next()) {
				hayResultados = true;
				fila++;
				System.out.println("---- Fila " + fila + " ----");
				for (String col : listaElementosSeleccionados) {
					System.out.println(col + ": " + rs.getString(col));
				}
			}

			if (!hayResultados) {
				System.out.println("No se ha encontrado nada");
			}

		} catch (SQLException e) {
			System.out.println("Error en SELECT: " + e.getMessage());
		}
	}

	/**
	 * Ejecuta las consultas Insert, Update o Delete.
	 *
	 * @param con      Objeto Connection que representa la conexión a la base de
	 *                 datos.
	 * @param sql      Sentencia SQL que se va a ejecutar.
	 * @param etiqueta Consulta a ejecutar -> Insert / Update / Delete
	 * @return Número de filas afectadas
	 */
	public static int executeInsUpDel(Connection con, String sql, String etiqueta) {
		if (con == null) {
			System.out.println("No hay conexión. Llama antes a conectarBaseDatos().");
			return 0;
		}

		try (Statement st = con.createStatement()) {
			int filas = st.executeUpdate(sql);
			System.out.println(etiqueta + " hecho correctamente. Filas afectadas: " + filas);
			return filas;
		} catch (SQLException e) {
			System.out.println("Ha habido un error en " + etiqueta + ": " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Elimina una partida de la BD. El borrado en cascada se encarga del resto.
	 */
	public boolean eliminarPartida(Connection con, int idPartida) {
		// 1) Borrar items de inventario (asociados a jugadores de esta partida)
		delete(con, "DELETE FROM INVENTARIO_ITEMS WHERE ID_ESTADO IN (SELECT ID_ESTADO FROM JUGADOR_ESTADO WHERE ID_PARTIDA = " + idPartida + ")");
		
		// 2) Borrar estados de los jugadores
		delete(con, "DELETE FROM JUGADOR_ESTADO WHERE ID_PARTIDA = " + idPartida);
		
		// 3) Borrar casillas especiales del tablero
		delete(con, "DELETE FROM CASILLAS_ESPECIALES WHERE ID_PARTIDA = " + idPartida);
		
		// 4) Finalmente borrar la partida
		String sql = "DELETE FROM PARTIDA WHERE ID_PARTIDA = " + idPartida;
		return executeInsUpDel(con, sql, "Delete") > 0;
	}

	/**
	 * Incrementa en 1 el contador de partidas jugadas para un usuario.
	 */
	public void sumarPartidaJugada(Connection con, String username) {
		if (con == null || username == null) return;
		String sql = "UPDATE USUARIOS_JUEGO SET PARTIDAS_JUGADAS = PARTIDAS_JUGADAS + 1 WHERE USERNAME = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.executeUpdate();
			System.out.println("Suma de partida jugada registrada para: " + username);
		} catch (SQLException e) {
			System.err.println("Error al sumar partida jugada: " + e.getMessage());
		}
	}

	/**
	 * Incrementa en 1 el contador de partidas ganadas para un usuario.
	 */
	public void sumarPartidaGanada(Connection con, String username) {
		if (con == null || username == null) return;
		String sql = "UPDATE USUARIOS_JUEGO SET PARTIDAS_GANADAS = PARTIDAS_GANADAS + 1 WHERE USERNAME = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.executeUpdate();
			System.out.println("Suma de partida ganada registrada para: " + username);
		} catch (SQLException e) {
			System.err.println("Error al sumar partida ganada: " + e.getMessage());
		}
	}

	/**
	 * Devuelve una lista con TODAS las partidas de UN usuario específico.
	 */
	public ArrayList<LinkedHashMap<String, String>> listarPartidas(Connection con, String username) {
		String sql = "SELECT ID_PARTIDA, TURNOS, FINALIZADA FROM PARTIDA WHERE USERNAME = ? AND FINALIZADA = 'N' ORDER BY ID_PARTIDA DESC";
		ArrayList<LinkedHashMap<String, String>> resultados = new ArrayList<>();
		try (PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				ResultSetMetaData meta = rs.getMetaData();
				int numColumnas = meta.getColumnCount();
				while (rs.next()) {
					LinkedHashMap<String, String> fila = new LinkedHashMap<>();
					for (int i = 1; i <= numColumnas; i++) {
						fila.put(meta.getColumnLabel(i), rs.getString(i));
					}
					resultados.add(fila);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al listar partidas: " + e.getMessage());
		}
		return resultados;
	}

	/**
	 * Obtiene el top 10 de jugadores con más victorias.
	 */
	public ArrayList<LinkedHashMap<String, String>> obtenerRankingVictorias(Connection con) {
		String sql = "SELECT USERNAME, PARTIDAS_GANADAS FROM USUARIOS_JUEGO ORDER BY PARTIDAS_GANADAS DESC FETCH FIRST 10 ROWS ONLY";
		return select(con, sql);
	}

	/**
	 * Obtiene el top 10 de jugadores con más partidas jugadas.
	 */
	public ArrayList<LinkedHashMap<String, String>> obtenerRankingParticipacion(Connection con) {
		String sql = "SELECT USERNAME, PARTIDAS_JUGADAS FROM USUARIOS_JUEGO ORDER BY PARTIDAS_JUGADAS DESC FETCH FIRST 10 ROWS ONLY";
		return select(con, sql);
	}
	
	/*
	 * Métodos de guardado/carga de partida (OBLIGATORIO según el enunciado).
	 * La información guardada incluye:
	 *   - ID y turno de la partida
	 *   - Posición de cada jugador
	 *   - Inventario de cada jugador (número de dados, peces y bolas de nieve)
	 *
	 * Esquema de tablas asumido:
	 *   PARTIDA(ID_PARTIDA, TURNOS, JUGADOR_ACTUAL, FINALIZADA)
	 *   JUGADOR_PARTIDA(ID_PARTIDA, NOMBRE, COLOR, POSICION, TURNOSCONGELADO, N_DADOS, N_PECES, N_BOLAS)
	 */

	/**
	 * Guarda el estado actual de la partida en la base de datos.
	 * Primero inserta la partida y luego el estado de cada jugador.
	 *
	 * @param con Conexión activa a la BBDD
	 * @param p   Partida a guardar
	 */
	// En tu archivo BBDD.java, modifica el método guardarBBDD:

	public void guardarBBDD(Connection con, Partida p, String username) {
	    if (con == null || p == null) {
	        System.out.println("No se puede guardar: conexión o partida nulos.");
	        return;
	    }

	    int idPartida = p.getIdPartida();
	    String ganador = (p.isFinalizada() && p.getGanador() != null) ? p.getGanador().getNombre() : null;
	    String finalizadaStr = p.isFinalizada() ? "S" : "N";

	    try {
	        if (idPartida > 0) {
	            // 1) UPDATE EN PARTIDA
	            String sqlUpdate = "UPDATE PARTIDA SET TURNOS = ?, JUGADOR_ACTUAL = ?, FINALIZADA = ?, GANADOR = ? WHERE ID_PARTIDA = ?";
	            try (PreparedStatement pstmt = con.prepareStatement(sqlUpdate)) {
	                pstmt.setInt(1, p.getTurnos());
	                pstmt.setString(2, p.getJugadores().get(p.getJugadorActualIndice()).getNombre());
	                pstmt.setString(3, finalizadaStr);
	                pstmt.setString(4, ganador);
	                pstmt.setInt(5, idPartida);
	                pstmt.executeUpdate();
	            }
	            // Limpieza para re-insertar
	            delete(con, "DELETE FROM INVENTARIO_ITEMS WHERE ID_ESTADO IN (SELECT ID_ESTADO FROM JUGADOR_ESTADO WHERE ID_PARTIDA = " + idPartida + ")");
	            delete(con, "DELETE FROM JUGADOR_ESTADO WHERE ID_PARTIDA = " + idPartida);
	            delete(con, "DELETE FROM CASILLAS_ESPECIALES WHERE ID_PARTIDA = " + idPartida);
	        } else {
	            // 1) INSERT EN PARTIDA (Obtenemos ID de secuencia manualmente para evitar errores de sincronización)
	            idPartida = obtenerSiguienteID(con);
	            p.setIdPartida(idPartida);
	            
	            String sqlPartida = "INSERT INTO PARTIDA (ID_PARTIDA, TURNOS, JUGADOR_ACTUAL, FINALIZADA, USERNAME, GANADOR) VALUES (?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = con.prepareStatement(sqlPartida)) {
	                pstmt.setInt(1, idPartida);
	                pstmt.setInt(2, p.getTurnos());
	                pstmt.setString(3, p.getJugadores().get(p.getJugadorActualIndice()).getNombre());
	                pstmt.setString(4, finalizadaStr);
	                pstmt.setString(5, username);
	                pstmt.setString(6, ganador);
	                pstmt.executeUpdate();
	            }
	        }

	        // 2) INSERT EN JUGADOR_ESTADO Y INVENTARIO_ITEMS
	        for (modelo.jugador.Jugador j : p.getJugadores()) {
	            String tipo = (j instanceof modelo.jugador.Pinguino) ? "PINGUINO" : "FOCA";
	            int stop = 0, bloqueo = 0;
	            String soborno = "N";

	            if (j instanceof modelo.jugador.Pinguino) {
	                stop = ((modelo.jugador.Pinguino) j).getTurnosCongelado();
	            } else if (j instanceof modelo.jugador.Foca) {
	                soborno = ((modelo.jugador.Foca) j).isSoborno() ? "S" : "N";
	                bloqueo = ((modelo.jugador.Foca) j).getTurnosBloqueada();
	            }

	            int idEstado = obtenerSiguienteID(con);
	            String sqlJ = "INSERT INTO JUGADOR_ESTADO (ID_ESTADO, ID_PARTIDA, NOMBRE_JUGADOR, TIPO_JUGADOR, COLOR, POSICION, TURNOS_STOP, SOBORNADA, TURNOS_BLOQUEO, SKIN) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = con.prepareStatement(sqlJ)) {
	                pstmt.setInt(1, idEstado);
	                pstmt.setInt(2, idPartida);
	                pstmt.setString(3, j.getNombre());
	                pstmt.setString(4, tipo);
	                pstmt.setString(5, j.getColor());
	                pstmt.setInt(6, j.getPosicion());
	                pstmt.setInt(7, stop);
	                pstmt.setString(8, soborno);
	                pstmt.setInt(9, bloqueo);
	                pstmt.setString(10, j.getSkin());
	                pstmt.executeUpdate();
	            }

	            if (j instanceof modelo.jugador.Pinguino) {
	                modelo.items.Inventario inv = ((modelo.jugador.Pinguino) j).getInventario();
	                java.util.Map<String, Integer> conteo = new java.util.HashMap<>();
	                for (modelo.items.Item it : inv.getLista()) {
	                    conteo.put(it.getNombre(), conteo.getOrDefault(it.getNombre(), 0) + 1);
	                }
	                
	                for (java.util.Map.Entry<String, Integer> entry : conteo.entrySet()) {
	                    String sqlItem = "INSERT INTO INVENTARIO_ITEMS (ID_ESTADO, TIPO_ITEM, CANTIDAD) VALUES (?, ?, ?)";
	                    try (PreparedStatement pstmt = con.prepareStatement(sqlItem)) {
	                        pstmt.setInt(1, idEstado);
	                        pstmt.setString(2, entry.getKey());
	                        pstmt.setInt(3, entry.getValue());
	                        pstmt.executeUpdate();
	                    }
	                }
	            }
	        }

	        // 3) INSERT EN CASILLAS_ESPECIALES
	        for (modelo.tablero.Casilla c : p.getTablero().getCasillas()) {
	            String datoExtra = "";
	            if (c instanceof modelo.tablero.Agujero) datoExtra = String.valueOf(((modelo.tablero.Agujero) c).getPosicionAgujeroAnterior());

	            String sqlC = "INSERT INTO CASILLAS_ESPECIALES (ID_PARTIDA, POSICION, TIPO_CASILLA, DATO_EXTRA) VALUES (?, ?, ?, ?)";
	            try (PreparedStatement pstmt = con.prepareStatement(sqlC)) {
	                pstmt.setInt(1, idPartida);
	                pstmt.setInt(2, c.getPosicion());
	                pstmt.setString(3, c.getClass().getSimpleName());
	                pstmt.setString(4, datoExtra);
	                pstmt.executeUpdate();
	            }
	        }
	        System.out.println("Partida " + idPartida + " guardada correctamente.");
	        
	        // Si la partida ha finalizado, sumamos la victoria al ganador (OBLIGATORIO)
	        if ("S".equals(finalizadaStr) && ganador != null) {
	            sumarPartidaGanada(con, ganador);
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Error crítico al guardar en BBDD: " + e.getMessage());
	    }
	}



	/**
	 * Carga una partida desde la base de datos a partir de su ID.
	 * Reconstuiye los jugadores con su posición e inventario.
	 *
	 * @param con Conexión activa a la BBDD
	 * @param id  ID de la partida a cargar
	 * @return Objeto Partida reconstruido, o null si no existe
	 */
	public Partida cargarBBDD(Connection con, int id) {
		if (con == null) {
			System.out.println("No se puede cargar: conexión nula.");
			return null;
		}

		// 1) Leer datos de la partida
		String sqlPartida = "SELECT * FROM PARTIDA WHERE ID_PARTIDA = " + id;
		ArrayList<LinkedHashMap<String, String>> filas = select(con, sqlPartida);
		if (filas.isEmpty()) {
			System.out.println("No se encontró la partida con ID " + id);
			return null;
		}

		// 2) Leer jugadores de esa partida (tabla correcta: JUGADOR_ESTADO)
		String sqlJugadores = "SELECT * FROM JUGADOR_ESTADO WHERE ID_PARTIDA = " + id;
		ArrayList<LinkedHashMap<String, String>> filasJ = select(con, sqlJugadores);

		if (filasJ.isEmpty()) {
			System.out.println("Error: La partida " + id + " no tiene jugadores registrados. Carga abortada.");
			return null;
		}

		ArrayList<modelo.jugador.Jugador> jugadores = new ArrayList<>();
		for (LinkedHashMap<String, String> fila : filasJ) {
			String nombre  = fila.get("NOMBRE_JUGADOR");
			String color   = fila.get("COLOR");
			String tipo    = fila.get("TIPO_JUGADOR");
			int posicion   = Integer.parseInt(fila.getOrDefault("POSICION", "0"));

			if ("FOCA".equalsIgnoreCase(tipo)) {
				modelo.jugador.Foca foca = new modelo.jugador.Foca(nombre, color);
				foca.setPosicion(posicion);
				foca.setPosicion(posicion);
				foca.setSoborno("S".equalsIgnoreCase(fila.getOrDefault("SOBORNADA", "N")));
				foca.setTurnosBloqueada(Integer.parseInt(fila.getOrDefault("TURNOS_BLOQUEO", "0")));
				foca.setSkin(fila.getOrDefault("SKIN", "foca.png"));
				jugadores.add(foca);
			} else {
				// Por defecto: Pinguino
				modelo.jugador.Pinguino ping = new modelo.jugador.Pinguino(nombre, color);
				ping.setPosicion(posicion);
				ping.setTurnosCongelado(Integer.parseInt(fila.getOrDefault("TURNOS_STOP", "0")));
				ping.setSkin(fila.getOrDefault("SKIN", "skin_dino.png"));

				// Reconstruir inventario desde INVENTARIO_ITEMS
				String idEstadoStr = fila.get("ID_ESTADO");
				if (idEstadoStr != null) {
					int idEstado = Integer.parseInt(idEstadoStr);
					String sqlItems = "SELECT TIPO_ITEM, CANTIDAD FROM INVENTARIO_ITEMS WHERE ID_ESTADO = " + idEstado;
					ArrayList<LinkedHashMap<String, String>> filasI = select(con, sqlItems);
					for (LinkedHashMap<String, String> fi : filasI) {
						String tipoItem = fi.get("TIPO_ITEM");
						int cantidad    = Integer.parseInt(fi.getOrDefault("CANTIDAD", "0"));
						for (int k = 0; k < cantidad; k++) {
							if ("Dado".equals(tipoItem))            ping.getInventario().añadirItem(new modelo.items.Dado());
							else if ("Dado Rapido".equals(tipoItem)) ping.getInventario().añadirItem(new modelo.items.Dado("Dado Rapido", 5, 10, true));
							else if ("Dado Lento".equals(tipoItem))  ping.getInventario().añadirItem(new modelo.items.Dado("Dado Lento", 1, 3, true));
							else if ("Pez".equals(tipoItem))        ping.getInventario().añadirItem(new modelo.items.Pez());
							else if ("Moto de Nieve".equals(tipoItem)) ping.getInventario().añadirItem(new modelo.items.MotoNieve());
							else if ("Bola de Nieve".equals(tipoItem)) ping.getInventario().añadirItem(new modelo.items.BolaDeNieve());
						}
					}
				}
				jugadores.add(ping);
			}
		}

		// 3) Reconstruir el tablero con sus casillas especiales desde CASILLAS_ESPECIALES
		modelo.tablero.Tablero tablero = new modelo.tablero.Tablero();
		String sqlCasillas = "SELECT POSICION, TIPO_CASILLA, DATO_EXTRA FROM CASILLAS_ESPECIALES WHERE ID_PARTIDA = " + id + " ORDER BY POSICION";
		ArrayList<LinkedHashMap<String, String>> filasC = select(con, sqlCasillas);
		for (LinkedHashMap<String, String> fc : filasC) {
			int pos       = Integer.parseInt(fc.getOrDefault("POSICION", "0"));
			String extra  = fc.getOrDefault("DATO_EXTRA", "0");
			int datoExtra = (extra == null || extra.isEmpty()) ? 0 : Integer.parseInt(extra);
			String tipo   = fc.get("TIPO_CASILLA");
			modelo.tablero.Casilla c = null;
			switch (tipo) {
				case "Oso":              c = new modelo.tablero.Oso(pos); break;
				case "Agujero":          c = new modelo.tablero.Agujero(pos, datoExtra); break;
				case "Evento":           c = new modelo.tablero.Evento(pos); break;
				case "SueloQuebradizo":  c = new modelo.tablero.SueloQuebradizo(pos); break;
				case "Trineo":           c = new modelo.tablero.Trineo(pos); break;
				case "CasillaSalida":    c = new modelo.tablero.CasillaSalida(pos); break;
				case "CasillaMeta":      c = new modelo.tablero.CasillaMeta(pos); break;
				case "CasillaNormal":    c = new modelo.tablero.CasillaNormal(pos); break;
			}
			if (c != null) tablero.añadirCasilla(c);
		}

		Partida partida = new Partida(tablero, jugadores);
		partida.setIdPartida(id); // Guardamos el ID para poder sobreescribir al guardar
		LinkedHashMap<String, String> filaP = filas.get(0);
		partida.setTurnos(Integer.parseInt(filaP.getOrDefault("TURNOS", "0")));
		
		// Reconstruir el índice del jugador actual buscando por nombre
		String nombreActual = filaP.get("JUGADOR_ACTUAL");
		int indiceActual = 0;
		boolean encontrado = false;
		int i = 0;
		while (i < jugadores.size() && !encontrado) {
			if (jugadores.get(i).getNombre().equalsIgnoreCase(nombreActual)) {
				indiceActual = i;
				encontrado = true;
			}
			i++;
		}
		partida.setJugadorActual(indiceActual);
		partida.setFinalizada("S".equalsIgnoreCase(filaP.getOrDefault("FINALIZADA", "N")));

		System.out.println("Partida " + id + " cargada correctamente.");
		return partida;
	}

	// ══════════════════════════════════════════════════
	//  LLAMADAS A PL/SQL (FUNCIONES Y PROCEDIMIENTOS)
	// ══════════════════════════════════════════════════

	/**
	 * Obtiene el récord máximo de victorias usando la función PL/SQL FN_MAX_VICTORIAS.
	 */
	public int obtenerMaxVictorias(Connection con) {
		String sql = "{ ? = call FN_MAX_VICTORIAS() }";
		try (CallableStatement cstmt = con.prepareCall(sql)) {
			cstmt.registerOutParameter(1, Types.NUMERIC);
			cstmt.execute();
			return cstmt.getInt(1);
		} catch (SQLException e) {
			System.err.println("Error al llamar a FN_MAX_VICTORIAS: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Obtiene la media de victorias usando la función PL/SQL FN_MEDIA_VICTORIAS.
	 */
	public double obtenerMediaVictorias(Connection con) {
		String sql = "{ ? = call FN_MEDIA_VICTORIAS() }";
		try (CallableStatement cstmt = con.prepareCall(sql)) {
			cstmt.registerOutParameter(1, Types.NUMERIC);
			cstmt.execute();
			return cstmt.getDouble(1);
		} catch (SQLException e) {
			System.err.println("Error al llamar a FN_MEDIA_VICTORIAS: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Obtiene el porcentaje de jugadores con menos victorias que las indicadas.
	 */
	public double obtenerPorcentajeSuperado(Connection con, int victorias) {
		String sql = "{ ? = call FN_PORCENTAJE_MENOR(?) }";
		try (CallableStatement cstmt = con.prepareCall(sql)) {
			cstmt.registerOutParameter(1, Types.NUMERIC);
			cstmt.setInt(2, victorias);
			cstmt.execute();
			return cstmt.getDouble(1);
		} catch (SQLException e) {
			System.err.println("Error al llamar a FN_PORCENTAJE_MENOR: " + e.getMessage());
			return 0.0;
		}
	}

	/**
	 * Ejecuta el procedimiento PR_QUIEN_TIENE_RECORD y muestra la salida de DBMS_OUTPUT.
	 */
	public void ejecutarJugadoresRecord(Connection con) {
		habilitarDbmsOutput(con);
		String sql = "{ call PR_QUIEN_TIENE_RECORD() }";
		try (CallableStatement cstmt = con.prepareCall(sql)) {
			cstmt.execute();
			imprimirDbmsOutput(con);
		} catch (SQLException e) {
			System.err.println("Error al ejecutar PR_QUIEN_TIENE_RECORD: " + e.getMessage());
		}
	}

	/**
	 * Ejecuta el procedimiento PR_SOBRE_LA_MEDIA y muestra la salida de DBMS_OUTPUT.
	 */
	public void ejecutarJugadoresSuperiorMedia(Connection con) {
		habilitarDbmsOutput(con);
		String sql = "{ call PR_SOBRE_LA_MEDIA() }";
		try (CallableStatement cstmt = con.prepareCall(sql)) {
			cstmt.execute();
			imprimirDbmsOutput(con);
		} catch (SQLException e) {
			System.err.println("Error al ejecutar PR_SOBRE_LA_MEDIA: " + e.getMessage());
		}
	}

	/**
	 * Ejecuta el procedimiento PR_RANKING_JUGADAS y muestra la salida de DBMS_OUTPUT.
	 */
	public void ejecutarRankingJugadas(Connection con) {
		habilitarDbmsOutput(con);
		String sql = "{ call PR_RANKING_JUGADAS() }";
		try (CallableStatement cstmt = con.prepareCall(sql)) {
			cstmt.execute();
			imprimirDbmsOutput(con);
		} catch (SQLException e) {
			System.err.println("Error al ejecutar PR_RANKING_JUGADAS: " + e.getMessage());
		}
	}

	// ══════════════════════════════════════════════════
	//  HELPERS PARA DBMS_OUTPUT
	// ══════════════════════════════════════════════════

	private void habilitarDbmsOutput(Connection con) {
		try (CallableStatement cstmt = con.prepareCall("{ call dbms_output.enable(20000) }")) {
			cstmt.execute();
		} catch (SQLException e) {
			System.err.println("Error al habilitar DBMS_OUTPUT: " + e.getMessage());
		}
	}

	private void imprimirDbmsOutput(Connection con) {
		String sql = "{ call dbms_output.get_line(?, ?) }";
		try (CallableStatement cstmt = con.prepareCall(sql)) {
			cstmt.registerOutParameter(1, Types.VARCHAR);
			cstmt.registerOutParameter(2, Types.NUMERIC);

			int status = 0;
			while (status == 0) {
				cstmt.execute();
				String line = cstmt.getString(1);
				status = cstmt.getInt(2);
				if (status == 0 && line != null) {
					System.out.println("[PL/SQL] " + line);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al leer DBMS_OUTPUT: " + e.getMessage());
		}
	}




}