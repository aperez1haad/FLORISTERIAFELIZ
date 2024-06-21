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
    private String dbName;
    private int nextProductoId;
    private int nextTicketId;
    private static Connection conn;

    private MySQLDB(String nombredb) {
        this.dbName = nombredb;
        if (obtenerConexion(dbName)) {
            nextProductoId = generarSiguienteId("producto");
            nextTicketId = generarSiguienteId("ticket");
        } else {
            System.err.println("Error al establecer la conexión.");
        }
    }
    public static MySQLDB instanciar(String nombredb) {
        if (instancia == null) {
            instancia = new MySQLDB(nombredb);
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
    public void actualizarCantidadProducto(int id, int nuevaCantidad) {
        String query = "UPDATE producto SET cantidad = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, id);
            int filasActualizadas = stmt.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Cantidad de producto actualizada en la base de datos.");
            } else {
                System.err.println("No se encontró el producto con el ID especificado.");
            }
        } catch (SQLException e) {
            System.err.println("Hubo un error al actualizar la cantidad del producto en la base de datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }
    }
    @Override
    public Ticket agregarTicket(Ticket ticket) {
        PreparedStatement insertTicketOnTicketDB = null;
        ResultSet generatedKeys = null;
        try {
            conn.setAutoCommit(false);
            insertTicketOnTicketDB = conn.prepareStatement(QueriesSQL.AGREGAR_TICKET, Statement.RETURN_GENERATED_KEYS);
            insertTicketOnTicketDB.setDate(1, Date.valueOf(ticket.getTicketDate()));
            insertTicketOnTicketDB.executeUpdate();

            generatedKeys = insertTicketOnTicketDB.getGeneratedKeys();
            if (generatedKeys.next()) {
                int ticketID = generatedKeys.getInt(1);
                ticket.setTicketID(ticketID);

                for (Producto producto : ticket.getProductosVendidos().values()) {
                    agregarProductoAlTicket(producto, ticketID);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        } finally {
            try {
                if (insertTicketOnTicketDB != null) {
                    insertTicketOnTicketDB.close();
                }
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ticket;
    }
    private void agregarProductoAlTicket(Producto producto, int ticketID) {
        PreparedStatement insertProductOnProductTicketDB = null;
        try {
            insertProductOnProductTicketDB = conn.prepareStatement(QueriesSQL.AGREGAR_PRODUCTO_TICKET);
            insertProductOnProductTicketDB.setInt(1, ticketID);
            insertProductOnProductTicketDB.setInt(2, producto.getProductoID());
            insertProductOnProductTicketDB.setInt(3, producto.getProductoCantidad());
            insertProductOnProductTicketDB.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Hubo un error al acceder a los datos. Intenta nuevamente.");
            System.err.println(e.getMessage());
        }
    }
    @Override
    public void consultarTickets() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QueriesSQL.LISTAR_TICKETS);

            System.out.println("=================================");
            System.out.println("Detalles de Tickets");
            System.out.println("=================================");
            System.out.printf("%-20s %-20s %20s\n", "ID Ticket", "Fecha Ticket", "Total Ticket");
            System.out.println("------------------------------------------------------");

            while (rs.next()) {
                int idTicket = rs.getInt("id_ticket");
                Date fechaTicket = rs.getDate("fecha_ticket");
                float totalTicket = rs.getFloat("total_ticket");

                // Formatted output for better readability
                System.out.printf("%-20d %-20s %20.2f\n", idTicket, fechaTicket, totalTicket);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar los detalles de los tickets: " + e.getMessage());
        }
    }
    @Override
    public void consultarUnTicket(int idTicket) {
        try {
            PreparedStatement stmt = conn.prepareStatement(QueriesSQL.CONSULTAR_TICKET_POR_ID);
            stmt.setInt(1, idTicket); // Set the parameter with the provided ID

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("=============================================");
                System.out.println("Detalles del Ticket #" + idTicket);
                System.out.println("=============================================");
                System.out.printf("%-20s %-20s %-20s %-20s %-20s %-20s %20s\n",
                        "ID Ticket", "Fecha Ticket", "ID Producto", "Nombre Producto", "Cantidad", "Precio", "Importe Producto");
                System.out.println("------------------------------------------------------------------");

                do {
                    int productoId = rs.getInt("producto_id");
                    String nombreProducto = rs.getString("nombre_producto");
                    int cantidadProducto = rs.getInt("cantidad_producto");
                    float precioProducto = rs.getFloat("precio_producto");
                    float importeProducto = rs.getFloat("importe_producto");

                    System.out.printf("%-20d %-20d %-20d %-20s %-20d %-20.2f %20.2f\n",
                            idTicket, rs.getDate("fecha_ticket"), productoId, nombreProducto, cantidadProducto, precioProducto, importeProducto);
                } while (rs.next()); // Iterate through all products in the ticket

                float totalTicket = rs.getFloat("total_ticket");
                System.out.println("\nTotal Ticket: " + totalTicket);

            } else {
                System.out.println("No se encontró ningún ticket con el ID: " + idTicket);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar el ticket: " + idTicket + " - " + e.getMessage());
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
    private HashMap<Integer, Producto> generaMapaProducto(ResultSet rs) throws SQLException {
        HashMap<Integer, Producto> productos = new HashMap<>();
        while (rs.next()) {
            String tipo = rs.getString("tipo").toLowerCase();
            int id = rs.getInt("id");
            Producto producto = switch (tipo) {
                case "arbol" -> new Arbol(
                        id,
                        rs.getString("nombre"),
                        rs.getFloat("precio"),
                        rs.getFloat("altura"),
                        rs.getInt("cantidad")
                );
                case "flor" -> new Flor(
                        id,
                        rs.getString("nombre"),
                        rs.getFloat("precio"),
                        rs.getString("color"),
                        rs.getInt("cantidad")
                );
                case "decoracion" -> new Decoracion(
                        id,
                        rs.getString("nombre"),
                        rs.getFloat("precio"),
                        Material.valueOf(rs.getString("material")),
                        rs.getInt("cantidad")
                );
                default -> throw new IllegalStateException("Unexpected value: " + tipo);
            };
            productos.put(id, producto);
        }
        return productos;
    }
    @Override
    public Producto consultarProducto(int id) {
        Producto producto = null;
        try {
            String query = "SELECT p.id, p.nombre, p.precio, p.cantidad, p.tipo, " +
                    "IFNULL(a.altura, 0) AS altura, f.color, d.material " +
                    "FROM producto p " +
                    "LEFT JOIN arbol a ON p.id = a.id " +
                    "LEFT JOIN flor f ON p.id = f.id " +
                    "LEFT JOIN decoracion d ON p.id = d.id " +
                    "WHERE p.id = " + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                String tipo = rs.getString("tipo").toLowerCase();
                switch (tipo) {
                    case "arbol":
                        producto = new Arbol(id, rs.getString("nombre"), rs.getFloat("precio"),
                                rs.getFloat("altura"), rs.getInt("cantidad"));
                        break;
                    case "flor":
                        producto = new Flor(id, rs.getString("nombre"), rs.getFloat("precio"),
                                rs.getString("color"), rs.getInt("cantidad"));
                        break;
                    case "decoracion":
                        producto = new Decoracion(id, rs.getString("nombre"), rs.getFloat("precio"),
                                Material.valueOf(rs.getString("material")), rs.getInt("cantidad"));
                        break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar el producto: " + e.getMessage());
        }
        return producto;
    }
/*    @Override
    public Ticket consultarTicket(int id) {
        // Implementar consulta de ticket.
        return null;
    }*/
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
        float valorTotal = 0;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SUM(precio * cantidad) AS valor_total FROM producto");
            if (rs.next()) {
                valorTotal = rs.getFloat("valor_total");
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar el valor total del stock: " + e.getMessage());
        }
        return valorTotal;
    }
    @Override
    public float consultarValorTotalTickets() {
        float valorTotal = 0;
        float valorPorTicket=0;
        int idticket;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT t.id AS id_ticket, SUM(p.precio * pt.cantidad) AS valor_total\n" +
                    "FROM ticket t\n" +
                    "INNER JOIN producto_ticket pt ON t.id = pt.ticketId\n" +
                    "INNER JOIN producto p ON pt.productoId = p.id\n" +
                    "GROUP BY t.id;");
            while (rs.next()) {
                idticket = rs.getInt("id_ticket");
                valorPorTicket = rs.getFloat("valor_total");
                valorTotal += valorPorTicket;
                System.out.println("id ticket: "+idticket+": "+valorPorTicket);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar el valor total del stock: " + e.getMessage());
        }
        return valorTotal;
    }
    @Override
    public Producto eliminarProducto(int id, int cantidad) throws CantidadExcedida, ProductoNoExiste {
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
                stmt.executeUpdate("UPDATE producto SET cantidad = " + nuevaCantidad + " WHERE id = " + id);
            } else {
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