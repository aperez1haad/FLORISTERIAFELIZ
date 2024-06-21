package MySQL.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;

public class Ticket{

    private int ticketID=0;
    private LocalDate ticketDate;
    private HashMap<Integer, Producto> productosVendidos;
    private float ticketTotal = 0.0F;

    public Ticket() {
        this.ticketID += ticketID;
        ticketDate = LocalDate.now();
        productosVendidos = new HashMap<>();
        ticketTotal = calcularValorTotalDelTicket();
    }

    public LocalDate getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(LocalDate ticketDate) {
        this.ticketDate = ticketDate;
    }

    public HashMap<Integer, Producto> getProductosVendidos() {
        return productosVendidos;
    }

    public double getTicketTotal() {
        return ticketTotal;
    }

    public void setTicketTotal(float ticketTotal) {
        this.ticketTotal = ticketTotal;
    }

    public int getTicketID() {
        return ticketID;
    }

    public HashMap<Integer, Producto> agregarProductoAlTicket(Producto producto) {
        productosVendidos.compute(producto.getProductoID(), (id, existingProducto) -> {
            if (existingProducto != null) {
                producto.setProductoCantidad(producto.getProductoCantidad() + existingProducto.getProductoCantidad());
            }
            return producto;
        });
        //productosVendidos.put(productoID, producto);
        return productosVendidos;
    }

    public HashMap<Integer, Producto> removerProductoDelTicket(int productoID, Producto producto) {
        productosVendidos.remove(productoID, producto);
        return productosVendidos;
    }

    public float calcularValorTotalDelTicket() {
        return (float) productosVendidos.values().stream().mapToDouble(producto -> producto.getProductoPrecio() * producto.getProductoCantidad()).sum();
    }
    public int cantidadDeProductosEnTicket(){
        int cantidadTotal = 0;
        for (Producto producto : productosVendidos.values()) {
            cantidadTotal += producto.getProductoCantidad();
        }
        return cantidadTotal;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    @Override
    public String toString() {
        return "Ticket [ID= " + ticketID + ", Date= " + ticketDate +  ", productos= " + productosVendidos + ", Total= "
                + calcularValorTotalDelTicket() + "]";
    }

}