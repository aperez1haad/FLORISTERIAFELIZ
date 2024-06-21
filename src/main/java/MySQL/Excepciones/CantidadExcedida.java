package MySQL.Excepciones;

public class CantidadExcedida extends Exception {

    public CantidadExcedida() {
    }
    public CantidadExcedida(String errorMessage) {
        super(errorMessage);

    }
}
