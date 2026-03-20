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
		
		if (con != null) {
			// Aseguramos que la tabla de usuarios del juego exista
			String sql = "CREATE TABLE USUARIOS_JUEGO (" +
						 "USERNAME VARCHAR2(50) PRIMARY KEY, " +
						 "PASSWORD VARCHAR2(50) NOT NULL)";
			try (Statement st = con.createStatement()) {
				st.execute(sql);
				System.out.println("Tabla USUARIOS_JUEGO creada.");
			} catch (SQLException e) {
				// Si ya existe, ignoramos el error
			}

			// Actualizar tabla PARTIDA para incluir el dueño de la partida
			String sqlAlt = "ALTER TABLE PARTIDA ADD USERNAME VARCHAR2(50) REFERENCES USUARIOS_JUEGO(USERNAME)";
			try (Statement st = con.createStatement()) {
				st.execute(sqlAlt);
				System.out.println("Añadido dueño (USERNAME) a la tabla PARTIDA.");
			} catch (SQLException e) {
				// Ignoramos si la columna ya existe
			}
		}
		return con;
	}

	public boolean registrarUsuario(Connection con, String user, String pass) {
		if (con == null) return false;
		String sql = "INSERT INTO USUARIOS_JUEGO (USERNAME, PASSWORD) VALUES ('" + user + "', '" + pass + "')";
		return insert(con, sql) > 0;
	}

	public boolean loginUsuario(Connection con, String user, String pass) {
		if (con == null) return false;
		String sql = "SELECT * FROM USUARIOS_JUEGO WHERE USERNAME = '" + user + "' AND PASSWORD = '" + pass + "'";
		ArrayList<LinkedHashMap<String, String>> res = select(con, sql);
		return res != null && !res.isEmpty();
	}

	public boolean existeUsuario(Connection con, String user) {
		if (con == null) return false;
		String sql = "SELECT * FROM USUARIOS_JUEGO WHERE USERNAME = '" + user + "'";
		ArrayList<LinkedHashMap<String, String>> res = select(con, sql);
		return res != null && !res.isEmpty();
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
		String sql = "DELETE FROM PARTIDA WHERE ID_PARTIDA = " + idPartida;
		return executeInsUpDel(con, sql, "Delete") > 0;
	}

	/**
	 * Devuelve una lista con TODAS las partidas de UN usuario específico.
	 */
	public ArrayList<LinkedHashMap<String, String>> listarPartidas(Connection con, String username) {
		String sql = "SELECT ID_PARTIDA, TURNOS, FINALIZADA FROM PARTIDA WHERE USERNAME = '" + username + "' ORDER BY ID_PARTIDA DESC";
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

	    if (idPartida > 0) {
	        // 1) UPDATE EN PARTIDA (Sobreescribimos)
	        String sqlUpdate = "UPDATE PARTIDA SET TURNOS = " + p.getTurnos() + 
	                           ", JUGADOR_ACTUAL = " + p.getJugadorActualIndice() + 
	                           ", FINALIZADA = '" + (p.isFinalizada() ? "S" : "N") + "'" +
	                           " WHERE ID_PARTIDA = " + idPartida;
	        update(con, sqlUpdate);

	        // Limpiamos estados y casillas para re-insertar actualizados
	        delete(con, "DELETE FROM JUGADOR_ESTADO WHERE ID_PARTIDA = " + idPartida);
	        delete(con, "DELETE FROM CASILLAS_ESPECIALES WHERE ID_PARTIDA = " + idPartida);
	    } else {
	        // 1) INSERT EN PARTIDA (Nueva partida)
	        String sqlPartida = "INSERT INTO PARTIDA (TURNOS, JUGADOR_ACTUAL, FINALIZADA, USERNAME) VALUES ("
	                + p.getTurnos() + ", " + p.getJugadorActualIndice() + ", '" + (p.isFinalizada() ? "S" : "N") + "', '" + username + "')";
	        insert(con, sqlPartida);

	        String sqlIdP = "SELECT MAX(ID_PARTIDA) AS ID FROM PARTIDA";
	        idPartida = Integer.parseInt(select(con, sqlIdP).get(0).get("ID"));
	        p.setIdPartida(idPartida);
	    }

	    // 2) INSERT EN JUGADOR_ESTADO Y INVENTARIO_ITEMS
	    for (modelo.jugador.Jugador j : p.getJugadores()) {
	        String tipo = (j instanceof modelo.jugador.Pinguino) ? "PINGUINO" : "FOCA";
	        int stop = 0, bloqueo = 0;
	        String sobornada = "N";

	        if (j instanceof modelo.jugador.Pinguino) {
	            stop = ((modelo.jugador.Pinguino) j).getTurnosCongelado();
	        } else if (j instanceof modelo.jugador.Foca) {
	            sobornada = ((modelo.jugador.Foca) j).isSoborno() ? "S" : "N";
	            bloqueo = ((modelo.jugador.Foca) j).getTurnosBloqueada();
	        }

	        // Insertar en JUGADOR_ESTADO
	        String sqlJ = "INSERT INTO JUGADOR_ESTADO (ID_PARTIDA, NOMBRE_JUGADOR, TIPO_JUGADOR, COLOR, POSICION, TURNOS_STOP, SOBORNADA, TURNOS_BLOQUEO) VALUES ("
	                + idPartida + ", '" + j.getNombre() + "', '" + tipo + "', '" + j.getColor() + "', "
	                + j.getPosicion() + ", " + stop + ", '" + sobornada + "', " + bloqueo + ")";
	        insert(con, sqlJ);

	        // Si es Pinguino, guardamos sus items en INVENTARIO_ITEMS
	        if (j instanceof modelo.jugador.Pinguino) {
	            String sqlIdJ = "SELECT MAX(ID_ESTADO) AS ID FROM JUGADOR_ESTADO";
	            int idEstado = Integer.parseInt(select(con, sqlIdJ).get(0).get("ID"));
	            
	            modelo.items.Inventario inv = ((modelo.jugador.Pinguino) j).getInventario();
	            
	            // Guardamos los ítems agrupados por su nombre exacto para no perder el tipo de dado
	            java.util.Map<String, Integer> conteo = new java.util.HashMap<>();
	            for (modelo.items.Item it : inv.getLista()) {
	                conteo.put(it.getNombre(), conteo.getOrDefault(it.getNombre(), 0) + 1);
	            }
	            
	            for (java.util.Map.Entry<String, Integer> entry : conteo.entrySet()) {
	                insert(con, "INSERT INTO INVENTARIO_ITEMS (ID_ESTADO, TIPO_ITEM, CANTIDAD) VALUES (" 
	                        + idEstado + ", '" + entry.getKey() + "', " + entry.getValue() + ")");
	            }
	        }
	    }

	    // 3) INSERT EN CASILLAS_ESPECIALES (Para reconstruir el tablero)
	    for (modelo.tablero.Casilla c : p.getTablero().getCasillas()) {
	        int datoExtra = 0;
	        if (c instanceof modelo.tablero.Trineo)  datoExtra = ((modelo.tablero.Trineo) c).getPosicionSiguienteTrineo();
	        if (c instanceof modelo.tablero.Agujero) datoExtra = ((modelo.tablero.Agujero) c).getPosicionAgujeroAnterior();
	        // SueloQuebradizo no necesita dato extra (lógica basada en inventario)

	        String sqlC = "INSERT INTO CASILLAS_ESPECIALES (ID_PARTIDA, POSICION, TIPO_CASILLA, DATO_EXTRA) VALUES ("
	                + idPartida + ", " + c.getPosicion() + ", '" + c.getClass().getSimpleName() + "', " + datoExtra + ")";
	        insert(con, sqlC);
	    }

	    System.out.println("Partida guardada correctamente en el esquema JuegoPinguBALA.");
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

		ArrayList<modelo.jugador.Jugador> jugadores = new ArrayList<>();
		for (LinkedHashMap<String, String> fila : filasJ) {
			String nombre  = fila.get("NOMBRE_JUGADOR");
			String color   = fila.get("COLOR");
			String tipo    = fila.get("TIPO_JUGADOR");
			int posicion   = Integer.parseInt(fila.getOrDefault("POSICION", "0"));

			if ("FOCA".equalsIgnoreCase(tipo)) {
				modelo.jugador.Foca foca = new modelo.jugador.Foca(nombre, color);
				foca.setPosicion(posicion);
				foca.setSoborno("S".equals(fila.getOrDefault("SOBORNADA", "N")));
				foca.setTurnosBloqueada(Integer.parseInt(fila.getOrDefault("TURNOS_BLOQUEO", "0")));
				jugadores.add(foca);
			} else {
				// Por defecto: Pinguino
				modelo.jugador.Pinguino ping = new modelo.jugador.Pinguino(nombre, color);
				ping.setPosicion(posicion);
				ping.setTurnosCongelado(Integer.parseInt(fila.getOrDefault("TURNOS_STOP", "0")));

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
		modelo.tablero.Trineo ultimoTrineo = null;
		for (LinkedHashMap<String, String> fc : filasC) {
			int pos       = Integer.parseInt(fc.getOrDefault("POSICION", "0"));
			int datoExtra = Integer.parseInt(fc.getOrDefault("DATO_EXTRA", "0"));
			String tipo   = fc.get("TIPO_CASILLA");
			modelo.tablero.Casilla c = null;
			switch (tipo) {
				case "Oso":             c = new modelo.tablero.Oso(pos); break;
				case "Agujero":         c = new modelo.tablero.Agujero(pos, datoExtra); break;
				case "Trineo":
					c = new modelo.tablero.Trineo(pos);
					if (ultimoTrineo != null) ultimoTrineo.setPosicionSiguienteTrineo(pos);
					ultimoTrineo = (modelo.tablero.Trineo) c;
					break;
				case "Evento":          c = new modelo.tablero.Evento(pos); break;
				case "SueloQuebradizo": c = new modelo.tablero.SueloQuebradizo(pos); break;
				case "MotoNieve":       c = new modelo.tablero.MotoNieve(pos); break;
			}
			if (c != null) tablero.añadirCasilla(c);
		}

		Partida partida = new Partida(tablero, jugadores);
		partida.setIdPartida(id); // Guardamos el ID para poder sobreescribir al guardar
		LinkedHashMap<String, String> filaP = filas.get(0);
		partida.setTurnos(Integer.parseInt(filaP.getOrDefault("TURNOS", "0")));
		partida.setJugadorActual(Integer.parseInt(filaP.getOrDefault("JUGADOR_ACTUAL", "0")));
		partida.setFinalizada("S".equals(filaP.getOrDefault("FINALIZADA", "N")));

		System.out.println("Partida " + id + " cargada correctamente.");
		return partida;
	}




}