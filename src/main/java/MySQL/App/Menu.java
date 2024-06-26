package MySQL.App;

import MySQL.Entrada.Input;
import MySQL.Entrada.Material;
import MySQL.Excepciones.CantidadExcedida;
import MySQL.Excepciones.ProductoNoExiste;
import MySQL.Model.*;
import java.util.HashMap;

public class    Menu {
    private static Floristeria floristeria = Floristeria.getInstancia();
    public static void ejecutarMenu(){
        boolean salir = false;
        do{
            switch(menu()){
                case 1: agregarProducto();
                    break;
                case 2: agregarProductoExistente();
                    break;
                case 3: eliminarProducto();
                    break;
                case 4: consultarProductos();
                    break;
                case 5: consultarValorTotalStock();
                    break;
                case 6: crearTicket();
                    break;
                case 7: consultarHistorialTickets();
                    break;
                case 8: consultarUnTicket();
                    break;
                case 9: imprimirValorTotalDeVentas();
                    break;
                case 0: System.out.println("Gracias por utilizar nuestra floristería.");
                    salir = true;
                    break;
            }
        }while(!salir);
    }

    public static byte menu(){
        byte opcion;
        final byte MINIMO = 0;
        final byte MAXIMO = 9;

        do{
            opcion = Input.inputByte("\nBienvenido a " + floristeria.getNombre().toUpperCase() + "\n"
                    + "\n1. Agregar producto nuevo."
                    + "\n2. Añadir stock a producto existente."
                    + "\n3. Eliminar producto."
                    + "\n4. Listar productos."
                    + "\n5. Consultar valor stock total."
                    + "\n6. Crear Ticket."
                    + "\n7. Lista historial Tickets."
                    + "\n8. Consultar un Ticket por id."
                    + "\n9. Totalizar ventas."
                    + "\n0. Salir de la aplicación.\n");

            if(opcion < MINIMO || opcion > MAXIMO){
                System.err.println("Escoge una opción válida.");
            }
        }while(opcion < MINIMO || opcion > MAXIMO);

        return opcion;
    }
    public static void agregarProductoExistente(){
        int idProducto = Input.inputInt("Id del producto:");
        int cantidad = Input.inputInt("Cantidad a añadir:");
        Producto producto = floristeria.consultarProducto(idProducto);

        if(producto == null){
            System.out.println("El producto no existe.");
        } else {
            floristeria.agregarCantidadProducto(idProducto, producto.getProductoCantidad() + cantidad);
        }

    }
    public static void agregarProducto (){
        int opcion2 = Input.inputInt("Dime que producto deseas crear: \n1.Arbol.\n2.Flor.\n3.Decoración.");

        switch (opcion2){
            case 1:
                floristeria.agregarProducto(crearArbol());
                break;
            case 2:
                floristeria.agregarProducto(crearFlor());
                break;
            case 3:
                floristeria.agregarProducto(crearDecoracion());
                break;
        }
    }
    public static Arbol crearArbol() {
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
    }
    public static void eliminarProducto() {
        int id = Input.inputInt("ID de producto: ");
        int cantidad = Input.inputInt("Cantidad a retirar: ");
        try {
            String resultado = floristeria.eliminarProducto(id, cantidad);
            System.out.println(resultado);
        } catch (CantidadExcedida | ProductoNoExiste e) {
            System.out.println(e.getMessage());
        }
    }
    public static void consultarProductos(){
        System.out.println("\nStock por tipo de producto:");
        consultarArbol(floristeria.consultarListaProductosPorTipo("arbol"));
        consultarFlor(floristeria.consultarListaProductosPorTipo("flor"));
        consultarDecoracion(floristeria.consultarListaProductosPorTipo("decoracion"));
    }
    private static void consultarArbol (HashMap<Integer, Producto> stockArbol){
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
    }
    public static void consultarValorTotalStock() {
        float valorTotal = floristeria.consultarValorTotalInventario();
        String formattedValue = String.format("%.2f", valorTotal);
        System.out.println("El valor total del stock es de " + formattedValue + " Euros.");
    }
    public static void crearTicket() {
        Ticket ticket = new Ticket();
        agregarProductosTicket(ticket);
        floristeria.agregarTicket(ticket);
        System.out.println("******   Ticket guardado correctamente   ******");
    }
    private static void agregarProductosTicket(Ticket ticket) {
        int productoID;
        int cantidadProductoEnTicket;
        boolean si;
        do {
            productoID = Input.inputInt("Id Producto para agregar: ");
            cantidadProductoEnTicket = Input.inputInt("Cantidad: ");
            try {
                if (floristeria.existeProducto(productoID, cantidadProductoEnTicket)) {
                    Producto productoAAgregar = floristeria.consultarProducto(productoID);
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
            si = Input.inputSiNo("\nDeseas agregar otro producto o sumar cantidad? s/n "+
                    "\nEm caso de querer decremento, indiquelo con el operador '-'");
    } while (si);
    }
    public static void consultarHistorialTickets() {
        floristeria.consultarListaTickets();
    }
    
    public static void consultarUnTicket(){
        floristeria.consultarUnTicket(Input.inputInt("Indica el id del ticket"));
    }
    public static void imprimirValorTotalDeVentas() {
        System.out.println("El valor total del ventas es de " + floristeria.consultarValorTotalVentas());
    }
}