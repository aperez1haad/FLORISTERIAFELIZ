package MySQL.ConexionMySQL;

import MySQL.Excepciones.CantidadExcedida;
import MySQL.Excepciones.ProductoNoExiste;
import MySQL.Model.Producto;
import MySQL.Model.Ticket;

import java.util.HashMap;

public interface InterfaceBaseDeDatos {


    void agregarProducto(Producto producto);
    Ticket agregarTicket(Ticket ticket);
    void actualizarCantidadProducto(int id, int nuevaCantidad);
    HashMap<Integer, Producto> consultarProductos();
    void consultarTickets();
    Producto consultarProducto(int id);
    //Ticket consultarTicket(int id);
    HashMap<Integer, Producto> consultarProductosFiltrando(String tipo);
    float consultarValorTotalStock();
    float consultarValorTotalTickets();
    Producto eliminarProducto(int id, int cantidad) throws CantidadExcedida, ProductoNoExiste;
    int obtenerSiguienteProductoId();
    int obtenerSiguienteTicketId();

}