package MySQL.Excepciones;

public class ProductoNoExiste extends Exception {

    private static final long serialVersionUID = 1L;

    public ProductoNoExiste() {

    }

    public ProductoNoExiste(String errorMessage) {
        super(errorMessage);

    }

}