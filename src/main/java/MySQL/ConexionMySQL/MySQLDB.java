package MySQL.ConexionMySQL;

import MySQL.Entrada.Input;
import MySQL.Entrada.Material;
import MySQL.Excepciones.CantidadExcedida;
import MySQL.Excepciones.ProductoNoExiste;
import MySQL.Model.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Locale;

public class MySQLDB implements InterfaceBaseDeDatos {
    private static MySQLDB instancia;
    private String dbName; // Nombre de la base de datos introducido por el usuario
    private int nextProductoId;
    private int nextTicketId;
    private static Connection conn;

    private MySQLDB(String dbName) {
        this.dbName = dbName;
        if (obtenerConexion(dbName)) {
            nextProductoId = generarSiguienteId("producto");
            nextTicketId = generarSiguienteId("ticket");
        } else {
            System.err.println("Error al establecer la conexión.");
        }
    }

    public static MySQLDB instanciar(String dbName) {
        if (instancia == null) {
            instancia = new MySQLDB(dbName);
        }
        return instancia;
    }


    public boolean obtenerConexion(String dbName) {
        boolean conexionEstablecida = false;
        while (!conexionEstablecida) {

            String usuario = Input.inputString("Dime tu usuario MySQL:");
            String password = Input.inputString("Dime tu password MySQL:");

            try {
                // Intenta conectar a la base de datos
                String url = "jdbc:mysql://localhost:3306/?user=" + usuario + "&password=" + password;
                conn = DriverManager.getConnection(url);
                System.out.println("Conexión a MySQL establecida.");

                // Verifica si la base de datos existe
                ResultSet resultSet = conn.getMetaData().getCatalogs();
                boolean dbExists = false;
                while (resultSet.next()) {
                    if (resultSet.getString(1).equalsIgnoreCase(this.dbName)) { // Usa el nombre proporcionado
                        dbExists = true;
                        break;
                    }
                }

                // Si no existe, crea la base de datos
                if (!dbExists) {
                    System.out.println("Base de datos no encontrada, creando base de datos...");
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE " + this.dbName);
                        System.out.println("Base de datos creada exitosamente.");
                    }
                }


                // Conecta a la base de datos específica
                String dbUrl = "jdbc:mysql://localhost:3306/" + dbName + "?user=" + usuario + "&password=" + password;
                conn = DriverManager.getConnection(dbUrl);


                System.out.println("Conectado a la base de datos " + dbName);  //Eugenia



                // Crear tablas si no existen
                crearTablas(conn);

                conexionEstablecida = true;

            } catch (SQLException e) {
                System.err.println("Error al conectar a la base de datos: " + e.getMessage());
                System.out.println("Por favor, intenta nuevamente con las credenciales correctas.");
            }
        }
        return true;
    }


    private void crearTablas(Connection conn) {
        // Crear las tablas si no existen
        String createProductoTable = "CREATE TABLE IF NOT EXISTS producto (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +  // Añadido AUTO_INCREMENT
                "nombre VARCHAR(50), " +
                "precio FLOAT, " +
                "tipo VARCHAR(50), " +
                "cantidad INT)";
        String createArbolTable = "CREATE TABLE IF NOT EXISTS arbol (" +
                "id INT PRIMARY KEY, " +
                "altura FLOAT)";
        String createFlorTable = "CREATE TABLE IF NOT EXISTS flor (" +
                "id INT PRIMARY KEY, " +
                "color VARCHAR(50))";
        String createDecoracionTable = "CREATE TABLE IF NOT EXISTS decoracion (" +
                "id INT PRIMARY KEY, " +
                "material VARCHAR(50))";
        String createTicketTable = "CREATE TABLE IF NOT EXISTS ticket (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +  // Añadido AUTO_INCREMENT
                "fecha DATE)";
        String createProductoTicketTable = "CREATE TABLE IF NOT EXISTS producto_ticket (" +
                "ticketId INT, " +
                "productoId INT, " +
                "cantidad INT, " +
                "PRIMARY KEY(ticketId, productoId))";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createProductoTable);
            stmt.executeUpdate(createArbolTable);
            stmt.executeUpdate(createFlorTable);
            stmt.executeUpdate(createDecoracionTable);
            stmt.executeUpdate(createTicketTable);
            stmt.executeUpdate(createProductoTicketTable);
            System.out.println("Tablas creadas o verificadas exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }


    @Override
    public void agregarProducto(Producto producto) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(
                    "SELECT * FROM producto WHERE id = %d", producto.getProductoID()));

            if (rs.next()) {
                int nuevaCantidad = producto.getProductoCantidad() + rs.getInt("cantidad");
                actualizarCantidadProducto(producto.getProductoID(), nuevaCantidad);
            } else {
                String insertarProducto = String.format(Locale.US,
                        "INSERT INTO producto VALUES (%d, '%s', %f, '%s', %d)",
                        producto.getProductoID(), producto.getProductoNombre(),
                        producto.getProductoPrecio(), producto.getProductoTipo(),
                        producto.getProductoCantidad());
                stmt.executeUpdate(insertarProducto);

                String tipo = producto.getProductoTipo().toLowerCase();

                switch (tipo) {
                    case "arbol" -> {
                        String insertarArbol = String.format(Locale.US,
                                "INSERT INTO arbol (id, altura) VALUES (%d, %f)",
                                producto.getProductoID(), ((Arbol) producto).getArbolAltura());
                        stmt.executeUpdate(insertarArbol);
                    }
                    case "flor" -> {
                        String insertarFlor = String.format(Locale.US,
                                "INSERT INTO flor (id, color) VALUES (%d, '%s')",
                                producto.getProductoID(), ((Flor) producto).getFlorColor());
                        stmt.executeUpdate(insertarFlor);
                    }
                    case "decoracion" -> {
                        String insertarDecoracion = String.format("INSERT INTO decoracion (id, material) VALUES (%d,'%s')",
                                producto.getProductoID(), ((Decoracion) producto).getDecoracionMaterial());
                        stmt.executeUpdate(insertarDecoracion);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Ticket agregarTicket(Ticket ticket) {
        PreparedStatement insertTicketOnTicketDB;

        try {
            insertTicketOnTicketDB = conn.prepareStatement(QueriesSQL.AGREGAR_TICKET);
            insertTicketOnTicketDB.setInt(1, ticket.getTicketID());
            insertTicketOnTicketDB.setDate(2, Date.valueOf(ticket.getTicketDate()));
            insertTicketOnTicketDB.execute();

            for (Producto producto : ticket.getProductosVendidos().values()) {
                agregarProductoAlTicket(producto, ticket.getTicketID());
            }

        } catch (SQLException e) {
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }

        return ticket;
    }

    @Override
    public void actualizarCantidadProducto(int id, int nuevaCantidad) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE producto SET cantidad = " + nuevaCantidad + " WHERE id = " + id);
        } catch (SQLException e) {
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }
    }

    private void agregarProductoAlTicket(Producto producto, int ticketID) {
        PreparedStatement insertProductOnProductTicketDB;
        try {
            insertProductOnProductTicketDB = conn.prepareStatement(QueriesSQL.AGREGAR_PRODUCTO_TICKET);
            insertProductOnProductTicketDB.setInt(1, ticketID);
            insertProductOnProductTicketDB.setInt(2, producto.getProductoID());
            insertProductOnProductTicketDB.setInt(3, producto.getProductoCantidad());
            insertProductOnProductTicketDB.execute();
        } catch (SQLException e) {
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    public HashMap<Integer, Producto> consultarProductos() {
        HashMap<Integer, Producto> productos = new HashMap<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QueriesSQL.GET_PRODUCTOS);
            productos = generaMapaProducto(rs);
        } catch (SQLException e) {
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }
        return productos;
    }

    @Override
    public HashMap<Integer, Ticket> consultarTickets() {
        HashMap<Integer, Ticket> tickets = new HashMap<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QueriesSQL.LISTAR_TICKETS);
            while (rs.next()) {
                Ticket ticket = new Ticket(rs.getInt("id"), rs.getDate("fecha").toLocalDate());
                tickets.put(ticket.getTicketID(), ticket);
            }
        } catch (SQLException e) {
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }
        return tickets;
    }

    @Override
    public Producto consultarProducto(int id) {
        // Implementar consulta de producto.
        return null;
    }

    @Override
    public Ticket consultarTicket(int id) {
        // Implementar consulta de ticket.
        return null;
    }

    @Override
    public HashMap<Integer, Producto> consultarProductosFiltrando(String tipo) {
        HashMap<Integer, Producto> productos = new HashMap<>();
        try {
            String query = String.format(
                    "SELECT p.id, p.nombre, p.precio, p.cantidad, " +
                            "IFNULL(a.altura, 0) AS altura, f.color, d.material " +
                            "FROM producto p " +
                            "LEFT JOIN arbol a ON p.id = a.id " +
                            "LEFT JOIN flor f ON p.id = f.id " +
                            "LEFT JOIN decoracion d ON p.id = d.id " +
                            "WHERE p.tipo = '%s'", tipo);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Producto producto = null;
                switch (tipo.toLowerCase()) {
                    case "arbol":
                        producto = new Arbol(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getFloat("precio"),
                                rs.getFloat("altura"),
                                rs.getInt("cantidad")
                        );
                        break;
                    case "flor":
                        producto = new Flor(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getFloat("precio"),
                                rs.getString("color"),
                                rs.getInt("cantidad")
                        );
                        break;
                    case "decoracion":
                        producto = new Decoracion(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getFloat("precio"),
                                Material.valueOf(rs.getString("material")),
                                rs.getInt("cantidad")
                        );
                        break;
                }
                if (producto != null) {
                    productos.put(rs.getInt("id"), producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar productos por tipo: " + e.getMessage());
        }
        return productos;
    }


    @Override
    public float consultarValorTotalStock() {
        // Implementar consulta del valor total del inventario.
        return 0;
    }

    @Override
    public float consultarValorTotalTickets() {
        // Implementar consulta del valor total de los tickets.
        return 0;
    }

    @Override
    public Producto eliminarProducto(int id, int cantidad) throws CantidadExcedida, ProductoNoExiste {
        // Obtener el producto actual por su ID
        Producto producto = consultarProducto(id);
        if (producto == null) {
            throw new ProductoNoExiste("El producto con ID " + id + " no existe.");
        }

        int cantidadActual = producto.getProductoCantidad();
        if (cantidadActual < cantidad) {
            throw new CantidadExcedida("Cantidad excede el stock actual.");
        }

        int nuevaCantidad = cantidadActual - cantidad;
        try (Statement stmt = conn.createStatement()) {
            if (nuevaCantidad > 0) {
                // Actualizar la cantidad del producto en la base de datos
                stmt.executeUpdate("UPDATE producto SET cantidad = " + nuevaCantidad + " WHERE id = " + id);
            } else {
                // Eliminar el producto si la cantidad es cero
                stmt.executeUpdate("DELETE FROM producto WHERE id = " + id);
                eliminarDetallesProducto(id, producto.getProductoTipo());
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
        }
        return producto;
    }

    private void eliminarDetallesProducto(int id, String tipo) throws SQLException {
        String tablaDetalles;
        switch (tipo.toLowerCase()) {
            case "arbol":
                tablaDetalles = "arbol";
                break;
            case "flor":
                tablaDetalles = "flor";
                break;
            case "decoracion":
                tablaDetalles = "decoracion";
                break;
            default:
                throw new IllegalArgumentException("Tipo de producto desconocido: " + tipo);
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM " + tablaDetalles + " WHERE id = " + id);
        }
    }


    @Override
    public int obtenerSiguienteProductoId() {
        return generarSiguienteId("producto");
    }

    @Override
    public int obtenerSiguienteTicketId() {
        return generarSiguienteId("ticket");
    }

    private HashMap<Integer, Producto> generaMapaProducto(ResultSet rs) throws SQLException {
        HashMap<Integer, Producto> productos = new HashMap<>();
        while (rs.next()) {
            String tipo = rs.getString("tipo").toLowerCase();
            int id = rs.getInt("id");
            Producto producto = switch (tipo) {
                case "arbol" -> new Arbol(id, rs.getString("nombre"), rs.getFloat("precio"),
                        (float) rs.getInt("cantidad"), (int) rs.getFloat("altura"));
                case "flor" -> new Flor(id, rs.getString("nombre"), rs.getFloat("precio"),
                        rs.getString("color"), rs.getInt("cantidad"));
                case "decoracion" -> new Decoracion(id, rs.getString("nombre"), rs.getFloat("precio"),
                        Material.valueOf(rs.getString("material")), rs.getInt("cantidad"));
                default -> throw new IllegalStateException("Unexpected value: " + tipo);
            };
            productos.put(id, producto);
        }
        return productos;
    }

    private int generarSiguienteId(String nombreTabla) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT MAX(id) FROM %s", nombreTabla));
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error al generar el siguiente ID para " + nombreTabla + ": " + e.getMessage());
        }
        return 1;
    }
}