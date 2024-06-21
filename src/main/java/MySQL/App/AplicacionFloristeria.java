package MySQL.App;

import MySQL.Entrada.Input;
import MySQL.Entrada.Material;
import MySQL.Excepciones.CantidadExcedida;
import MySQL.Excepciones.ProductoNoExiste;
import MySQL.Model.*;

import java.time.LocalDate;
import java.util.HashMap;

public class AplicacionFloristeria {

    private static Floristeria floristeria;
    public static void start (){
        //floristeria = Floristeria.getInstancia();
        Menu.ejecutarMenu();
    }

    /*public static void agregarCantidadProducto (){
        int idProducto = Input.inputInt("Id del producto:");
        int cantidad = Input.inputInt("Cantidad a añadir:");
        Producto producto = floristeria.consultarProducto(idProducto);

        if(producto == null){
            System.out.println("El producto no existe.");
        } else {
            floristeria.agregarCantidadProducto(idProducto, producto.getProductoCantidad() + cantidad);
        }
    }*/

   /*public static Arbol crearArbol() {
        String nombre = Input.inputString("Dime el nombre del árbol:");
        float precio = Input.inputFloat("Dime el precio:");
        float altura = Input.inputFloat("Dime la altura:");
        int cantidad = Input.inputInt("Dime la cantidad:");
        System.out.println("Arbol " + nombre + "  creado correctamente.");
        return new Arbol(floristeria.consultarSiguienteProductoID(), nombre, precio, altura, cantidad);
    }

    public static Flor crearFlor() {
        String nombre = Input.inputString("Dime el nombre de la flor:");
        float precio = Input.inputFloat("Dime el precio:");
        String color = Input.inputString("Dime el color:");
        int cantidad = Input.inputInt("Dime la cantidad:");
        System.out.println("Flor " + nombre + "  creada correctamente.");
        return new Flor(floristeria.consultarSiguienteProductoID(), nombre, precio, color, cantidad);
    }

    public static Decoracion crearDecoracion() {
        String nombre = Input.inputString("Dime el tipo de decoración:");
        float precio = Input.inputFloat("Dime el precio:");
        Material material = Input.inputEnum("Dime el material (madera o plastico)");
        int cantidad = Input.inputInt("Dime la cantidad:");
        System.out.println("Decoración " + nombre + " creada correctamente.");
        return new Decoracion(floristeria.consultarSiguienteProductoID(), nombre, precio, material, cantidad);
    }*/

    /*public static void eliminarProducto() {
        int id = Input.inputInt("ID de producto: ");
        int cantidad = Input.inputInt("Cantidad a retirar: ");
        try {
            String resultado = floristeria.eliminarProducto(id, cantidad);
            System.out.println(resultado);
        } catch (CantidadExcedida | ProductoNoExiste e) {
            System.out.println(e.getMessage());
        }
    }*/



    /*public static void consultarProductos(){
        System.out.println("\nStock por tipo de producto:");
        consultarArbol(floristeria.consultarListaProductosPorTipo("arbol"));
        consultarFlor(floristeria.consultarListaProductosPorTipo("flor"));
        consultarDecoracion(floristeria.consultarListaProductosPorTipo("decoracion"));
    }*/
    /*private static void consultarArbol (HashMap<Integer, Producto> stockArbol){
        System.out.println("***ARBOL***:\n");
        stockArbol.values().forEach(producto -> {
            Arbol productoArbol = (Arbol) producto;
            System.out.println("ID: " + productoArbol.getProductoID()
                    + " | Nombre: " + productoArbol.getProductoNombre()
                    + " | Precio: " + productoArbol.getProductoPrecio()
                    + " | Cantidad: " + productoArbol.getProductoCantidad()
                    + " | Altura: " + productoArbol.getArbolAltura()
            );

        });
    }
    private static void consultarFlor (HashMap<Integer, Producto> stockFlor){
        System.out.println("\n***FLOR***:\n");
        stockFlor.values().forEach(producto -> {
            Flor productoFlor = (Flor) producto;
            System.out.println("ID: " + productoFlor.getProductoID()
                    + " | Nombre: " + productoFlor.getProductoNombre()
                    + " | Precio: " + productoFlor.getProductoPrecio()
                    + " | Cantidad: " + productoFlor.getProductoCantidad()
                    + " | Color: " + productoFlor.getFlorColor()
            );
        });
    }
    private static void consultarDecoracion (HashMap<Integer,Producto> stockDecoracion){
        System.out.println("\n***DECORACION***:\n");
        stockDecoracion.values().forEach(producto -> {
            Decoracion productoDecoracion = (Decoracion) producto;
            System.out.println("ID: " + productoDecoracion.getProductoID()
                    + " | Nombre: " + productoDecoracion.getProductoNombre()
                    + " | Precio: " + productoDecoracion.getProductoPrecio()
                    + " | Cantidad: " + productoDecoracion.getProductoCantidad()
                    + " | Material: " + productoDecoracion.getDecoracionMaterial()
            );
        });
    }*/
/*    public static void consultarValorTotalStock() {
        float valorTotal = floristeria.consultarValorTotalInventario();
        String formattedValue = String.format("%.2f", valorTotal);
        System.out.println("El valor total del stock es de " + formattedValue + " Euros.");
    }*/


/*    public static void crearTicket() {
        Ticket ticket = new Ticket();
        agregarProductosTicket(ticket);
        floristeria.agregarTicket(ticket);
    }*/

/*    private static void agregarProductosTicket(Ticket ticket) {
        int productoID;
        int cantidadProductoEnTicket;
        boolean si;
        do {
            productoID = Input.inputInt("Id Producto para agregar: ");
            cantidadProductoEnTicket = Input.inputInt("Cantidad: ");
            try {
                if (floristeria.existeProducto(productoID, cantidadProductoEnTicket)) {
                    Producto productoAAgregar = floristeria.consultarProducto(productoID).clonar();
                    productoAAgregar.setProductoCantidad(cantidadProductoEnTicket);
                    ticket.agregarProductoAlTicket(productoAAgregar);
                    try {
                        floristeria.eliminarProducto(productoID, cantidadProductoEnTicket);
                    } catch (CantidadExcedida e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.err.println("No existe el producto, o no hay suficiente en stock.");
                }
            } catch (ProductoNoExiste e) {
                System.out.println(e.getMessage());
            }
            si = Input.inputSiNo("Deseas agregar otro producto/ o cambiar cantidad? s/n");
        } while (si);
    }*/

/*    public static void consultarHistorialTickets() {
        floristeria.consultarListaTickets().entrySet().forEach(System.out::println);
    }

    public static void imprimirValorTotalDeVentas() {
        System.out.println("El valor total del ventas es de " + floristeria.consultarValorTotalVentas());
    }*/

}