package MySQL.ConexionMySQL;

public class QueriesSQL {
    public static final String AGREGAR_TICKET = "INSERT INTO ticket (fecha) VALUES(?)";
    public static final String AGREGAR_PRODUCTO_TICKET =
            "INSERT INTO producto_ticket (ticketId, productoId, cantidad) VALUES(?,?,?)";

 /*   public static final String CONSULTAR_PRODUCTOS_TICKET =
            "SELECT * FROM producto_ticket " +
                    "INNER JOIN producto ON producto_ticket.productoId = producto.id " +
                    "LEFT JOIN arbol ON producto.id = arbol.id " +
                    "LEFT JOIN flor ON producto.id = flor.id " +
                    "LEFT JOIN decoracion ON producto.id = decoracion.id " +
                    "WHERE producto_ticket.ticketId = ";*/

/*    public static final String CONSULTAR_VALOR_TOTAL_TICKETS =
            "SELECT SUM(precio * producto_ticket.cantidad) AS sumaTotal FROM producto_ticket " +
                    "INNER JOIN producto ON producto_ticket.productoId = producto.id " +
                    "LEFT JOIN arbol ON producto.id = arbol.id " +
                    "LEFT JOIN flor ON producto.id = flor.id " +
                    "LEFT JOIN decoracion ON producto.id = decoracion.id ";*/
    public static final String LISTAR_TICKETS = "SELECT t.id AS id_ticket, t.fecha AS fecha_ticket, SUM(pt.cantidad * p.precio) AS total_ticket\n" +
        "FROM ticket t\n" +
        "INNER JOIN producto_ticket pt ON t.id = pt.ticketId\n" +
        "INNER JOIN producto p ON pt.productoId = p.id\n" +
        "GROUP BY t.id, t.fecha";

    public static final String CONSULTAR_TICKET_POR_ID="SELECT t.id AS id_ticket,\n" +
            "       t.fecha AS fecha_ticket,\n" +
            "       pt.productoId AS producto_id,\n" +
            "       p.nombre AS nombre_producto,\n" +
            "       pt.cantidad AS cantidad_producto,\n" +
            "       p.precio AS precio_producto,\n" +
            "       (pt.cantidad * p.precio) AS importe_producto,\n" +
            "       SUM(pt.cantidad * p.precio) AS total_ticket\n" +
            "FROM ticket t\n" +
            "INNER JOIN producto_ticket pt ON t.id = pt.ticketId\n" +
            "INNER JOIN producto p ON pt.productoId = p.id\n" +
            "WHERE t.id = ? -- Parameter for the ticket ID\n" +
            "GROUP BY t.id, pt.productoId";

    //public static final String DELETE_PRODUCTO = "DELETE FROM producto WHERE id = (?)";
    public static final String GET_PRODUCTOS = "SELECT * FROM producto " +
            "LEFT JOIN arbol ON producto.id = arbol.id " +
            "LEFT JOIN flor ON producto.id = flor.id " +
            "LEFT JOIN decoracion ON producto.id = decoracion.id ";




}
