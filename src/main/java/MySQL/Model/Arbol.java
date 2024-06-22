package MySQL.Model;

public class Arbol extends Producto {

    private float arbolAltura;

    public Arbol(int productoID, String productoNombre, float productoPrecio, float arbolAltura, int productoCantidad) {
        super(productoID, productoNombre, productoPrecio, productoCantidad);
        this.arbolAltura = arbolAltura;
        super.setProductoTipo("Arbol");
    }
    public float getArbolAltura() {
        return arbolAltura;
    }
    public void setArbolAltura(float arbolAltura) {
        this.arbolAltura = arbolAltura;
    }
    @Override
    public String toString() {
        return super.toString() + "| Altura = " + arbolAltura + " ]";
    }

}
