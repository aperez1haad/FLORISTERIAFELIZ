package MySQL.Model;

import MySQL.ConexionMySQL.InterfaceBaseDeDatos;
import MySQL.ConexionMySQL.MySQLDB;
import MySQL.Entrada.Input;
import MySQL.Excepciones.CantidadExcedida;
import MySQL.Excepciones.ProductoNoExiste;
import java.util.HashMap;

public class Floristeria {

    private static Floristeria instancia = null;
    private String nombre;
    private InterfaceBaseDeDatos baseDeDatos;

    private Floristeria(String nombreFloristeria) {
        this.nombre =nombreFloristeria;
        this.baseDeDatos = MySQLDB.instanciar(nombreFloristeria);
    }
    public static Floristeria getInstancia() {
        String nombre = Input.inputString("Dime el nombre de la floristeria");
        if (instancia == null) {
            instancia = new Floristeria(nombre);
        }
        return instancia;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void agregarProducto(Producto producto) {
        baseDeDatos.agregarProducto(producto);
    }
    public void agregarCantidadProducto(int id, int nuevaCantidad) {
        baseDeDatos.actualizarCantidadProducto(id, nuevaCantidad);
    }
    public void agregarTicket(Ticket ticket) {
        baseDeDatos.agregarTicket(ticket);
    }
    public Producto consultarProducto(int productoId) {
        return baseDeDatos.consultarProducto(productoId);
    }
    public int consultarSiguienteProductoID() {
        return baseDeDatos.obtenerSiguienteProductoId();
    }
    public int consultarSiguienteTicketID() {
        return baseDeDatos.obtenerSiguienteTicketId();
    }

    public String eliminarProducto(int productoID, int cantidad) throws CantidadExcedida, ProductoNoExiste {
        Producto producto = baseDeDatos.consultarProducto(productoID);
        if (producto == null) {
            throw new ProductoNoExiste("El producto con ID " + productoID + " no existe.");
        }
        if (producto.getProductoCantidad() < cantidad) {
            throw new CantidadExcedida("No hay suficiente cantidad en el inventario para eliminar.");
        }
        baseDeDatos.eliminarProducto(productoID, cantidad);

        if (producto.getProductoCantidad() - cantidad == 0) {
            return "El producto con ID " + productoID + " ha sido completamente eliminado por que se a vencido todo su stock.";
        } else {
            return "Se eliminaron " + cantidad + " unidades del producto con ID " + productoID;
        }
    }
    public HashMap<Integer, Producto> consultarListaProductosPorTipo (String tipo){
        return baseDeDatos.consultarProductosFiltrando(tipo);
    }
    public void consultarListaTickets () {
        baseDeDatos.consultarTickets();
    }
    public void consultarUnTicket (int idTicket) {
        baseDeDatos.consultarUnTicket(idTicket);
    }
    public float consultarValorTotalInventario () {
        return baseDeDatos.consultarValorTotalStock();
    }
    public float consultarValorTotalVentas () {
        return baseDeDatos.consultarValorTotalTickets();
    }
    /*public boolean existeProducto(int productoID) throws ProductoNoExiste {
        boolean returnValue;
        Producto producto = baseDeDatos.consultarProducto(productoID);
        if (producto != null) {
            returnValue = producto.getProductoCantidad() > 0;
        } else {
            throw new ProductoNoExiste("El id de producto inexistente. Escoja en productos existentes");
        }
        return returnValue;
    }*/
    public boolean existeProducto(int productoID,int cantidadMinima) throws ProductoNoExiste {
        boolean returnValue;
        Producto producto = baseDeDatos.consultarProducto(productoID);
        if (producto != null) {
            returnValue = producto.getProductoCantidad() >= cantidadMinima;
        } else {
            throw new ProductoNoExiste("El id de producto inexistente. Escoja en productos existentes");
        }
        return returnValue;
    }
}