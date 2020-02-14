package pl.kukla.krzys.msscbeerorderservice.exception;

/**
 * @author Krzysztof Kukla
 */
public class NotFoundException extends RuntimeException {

    private String message;

    public NotFoundException(String message) {
        this.message = message;
    }

}
