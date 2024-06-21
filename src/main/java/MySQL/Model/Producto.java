package MySQL.Model;

public abstract class Producto {

    private int productoID;
    private String productoNombre;
    private float productoPrecio;
    private String productoTipo;
    private int productoCantidad;
    public Producto(int productoID, String productoNombre, float productoPrecio, int cantidad) {
        this.productoID = productoID;
        this.productoNombre = productoNombre;
        this.productoPrecio = productoPrecio;
        this.productoCantidad = cantidad;
    }

    public int getProductoID() {
        return productoID;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public float getProductoPrecio() {
        return productoPrecio;
    }

    public void setProductoPrecio(float productoPrecio) {
        this.productoPrecio = productoPrecio;
    }

    public String getProductoTipo() {
        return productoTipo;
    }

    public void setProductoTipo(String productoTipo) {
        this.productoTipo = productoTipo;
    }

    public int getProductoCantidad() {
        return productoCantidad;
    }

    public void setProductoCantidad(int productoCantidad) {
        this.productoCantidad = productoCantidad;
    }

    public void reducirProductoCantidad(int cantidad) {
        productoCantidad = productoCantidad - cantidad;
    }

    public void reducirProductoCantidadUnidad() {
        productoCantidad--;
    }

    public void incrementarProductoCantidadUnidad() {
        productoCantidad++;
    }

    public void resetProductoCantidad() {
        productoCantidad = 0;
    }
    @Override
    public String toString() {
        return "Producto [ID= " + productoID + ", Nombre=" + productoNombre + ", Precio="
                + productoPrecio + ", Tipo=" + productoTipo + ", Cantidad=" + productoCantidad + " ";
    }


}
