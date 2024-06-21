package MySQL.Model;

import MySQL.Entrada.Material;

public class Decoracion extends Producto {

    private Enum<Material> decoracionMaterial;

    public Decoracion(int productoID, String productoNombre, float productoPrecio, Enum<Material> decoracionMaterial, int productoCantidad) {
        super(productoID, productoNombre, productoPrecio, productoCantidad);
        this.decoracionMaterial = decoracionMaterial;
        super.setProductoTipo("Decoracion");
    }

    public Enum<Material> getDecoracionMaterial() {
        return decoracionMaterial;
    }

    public void setDecoracionMaterial(Enum<Material> decoracionMaterial) {
        this.decoracionMaterial = decoracionMaterial;
    }

    @Override
    public String toString() {
        return super.toString() + ", Material=" + decoracionMaterial + "]";
    }

}