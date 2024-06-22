package MySQL.App;

import MySQL.Entrada.Input;
import MySQL.Excepciones.CantidadExcedida;
import MySQL.Excepciones.ProductoNoExiste;
import MySQL.Model.*;
import java.util.HashMap;

public class Menu {
    private static final Floristeria floristeria = Floristeria.getInstancia();

    public static void ejecutarMenu() {
        boolean salir = false;
        while (!salir) {
            switch (menu()) {
                case 1 -> agregarProducto();
                case 2 -> agregarProductoExistente();
                case 3 -> eliminarProducto();
                case 4 -> consultarProductos();
                case 5 -> consultarValorTotalStock();
                case 6 -> crearTicket();
                case 7 -> consultarHistorialTickets();
                case 8 -> consultarUnTicket();
                case 9 -> imprimirValorTotalDeVentas();
                case 0 -> {
                    System.out.println("Gracias por utilizar nuestra floristería.");
                    salir = true;
                }
                default -> System.err.println("Escoge una opción válida.");
            }
        }
    }

    private static byte menu() {
        byte opcion;
        final byte MINIMO = 0;
        final byte MAXIMO = 9;

        do {
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

            if (opcion < MINIMO || opcion > MAXIMO) {
                System.err.println("Escoge una opción válida.");
            }
        } while (opcion < MINIMO || opcion > MAXIMO);
        return opcion;
    }

    private static void agregarProductoExistente() {
        int idProducto = Input.inputInt("Id del producto:");
        boolean productoExiste = floristeria.consultarProducto(idProducto) != null;

        if (!productoExiste) {
            System.err.println("El producto con el id " + idProducto + " no existe.");
        } else {
            int cantidad = Input.inputInt("Cantidad a añadir:");
            Producto producto = floristeria.consultarProducto(idProducto);
            floristeria.agregarCantidadProducto(idProducto, producto.getProductoCantidad() + cantidad);
            System.out.println("******   Cantidad anadida correctamente   ******");
        }
    }

    private static void agregarProducto() {
        switch (Input.inputInt("Dime qué producto deseas crear: \n1. Arbol.\n2. Flor.\n3. Decoración.")) {
            case 1 -> crearProductoMostrar("arbol");
            case 2 -> crearProductoMostrar("flor");
            case 3 -> crearProductoMostrar("decoracion");
            default -> System.err.println("Opción no válida.");
        }
    }

    private static void crearProductoMostrar(String tipo) {
        String nombre = Input.inputString("Dime el nombre del " + tipo + ":");
        float precio = Input.inputFloat("Dime el precio:");
        int cantidad = Input.inputInt("Dime la cantidad:");

        Producto producto = switch (tipo) {
            case "arbol" ->
                    new Arbol(floristeria.consultarSiguienteProductoID(), nombre, precio, Input.inputFloat("Dime la altura:"), cantidad);
            case "flor" ->
                    new Flor(floristeria.consultarSiguienteProductoID(), nombre, precio, Input.inputString("Dime el color:"), cantidad);
            case "decoracion" ->
                    new Decoracion(floristeria.consultarSiguienteProductoID(), nombre, precio, Input.inputEnum("Dime el material (madera o plastico)"), cantidad);
            default -> throw new IllegalStateException("Tipo de producto no reconocido: " + tipo);
        };

        floristeria.agregarProducto(producto);
        System.out.println("******   Producto creado correctamente   ******");
    }

    private static void eliminarProducto() {
        int idProducto = Input.inputInt("ID de producto: ");
        boolean productoExiste = floristeria.consultarProducto(idProducto) != null;

        if (!productoExiste) {
            System.err.println("El producto con el id " + idProducto + " no existe.");
        } else {
            int cantidad = Input.inputInt("Cantidad a retirar: ");
            try {
                System.out.println(floristeria.eliminarProducto(idProducto, cantidad));
            } catch (CantidadExcedida | ProductoNoExiste e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void consultarProductos() {
        System.out.println("\nStock por tipo de producto:");
        consultarProducto("arbol");
        consultarProducto("flor");
        consultarProducto("decoracion");
    }

    private static void consultarProducto(String tipo) {
        System.out.println("\n***" + tipo.toUpperCase() + "***:\n");
        try {
            HashMap<Integer, Producto> productos = floristeria.consultarListaProductosPorTipo(tipo);
            if (productos.isEmpty()) {
                System.out.println("No existen productos de tipo " + tipo + ".");
            } else {
                for (Producto producto : productos.values()) {
                    System.out.println(producto);
                }
            }
        } catch (Exception e) {
            System.err.println("Ocurrió un error al consultar productos " + e.getMessage());
        }
    }

    private static void consultarValorTotalStock() {
        float valorTotal = floristeria.consultarValorTotalInventario();
        if (valorTotal == 0) {
            System.err.println("No existen productos en stock. Por favor crea un producto.");
        } else {
            System.out.printf("El valor total del stock es de %.2f Euros.%n", valorTotal);
        }
    }

    private static void crearTicket() {
        Ticket ticket = new Ticket();
        agregarProductosTicket(ticket);
        floristeria.agregarTicket(ticket);
    }

    private static void agregarProductosTicket(Ticket ticket) {
        boolean continuar = true;
        do {
            int productoID = Input.inputInt("Id Producto para agregar: ");
            try {
                Producto producto = floristeria.consultarProducto(productoID);
                if (producto == null) {
                    throw new ProductoNoExiste("El producto con ID " + productoID + " no existe.");
                }
                int cantidad = Input.inputInt("Cantidad: ");

                if (floristeria.existeProducto(productoID, cantidad)) {
                    producto.setProductoCantidad(cantidad);
                    ticket.agregarProductoAlTicket(producto);
                    floristeria.eliminarProducto(productoID, cantidad);
                    System.out.println("******   Ticket guardado correctamente   ******");
                } else {
                    System.err.println("No existe el producto, o no hay suficiente en stock.");
                }
            } catch (ProductoNoExiste e) {
                System.err.println("No se puede crear el ticket porque el producto con ID " + productoID + " no existe.");
                continuar = false;
            } catch (CantidadExcedida e) {
                System.err.println("No se puede agregar al ticket: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (continuar && Input.inputSiNo("\n¿Deseas agregar otro producto o sumar cantidad? s/n "));
    }

    private static void consultarHistorialTickets() { floristeria.consultarListaTickets(); }

    private static void consultarUnTicket() { floristeria.consultarUnTicket(Input.inputInt("Indica el id del ticket")); }

    private static void imprimirValorTotalDeVentas() {
        try {
            float valorTotalVentas = floristeria.consultarValorTotalVentas();
            if (valorTotalVentas == 0) {
                System.err.println("No existen tickets creados. Por favor crea un ticket.");
            } else {
                System.out.println("El valor total de ventas es de " + valorTotalVentas);
            }
        } catch (Exception e) {
            System.err.println("Ocurrió un error al consultar el valor total de ventas: " + e.getMessage());
        }
    }

}