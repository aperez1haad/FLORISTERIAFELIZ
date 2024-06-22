package MySQL.Excepciones;

public class ProductoNoExiste extends Exception {

    public ProductoNoExiste() {

    }
    public ProductoNoExiste(String errorMessage) {
        super(errorMessage);

    }

}