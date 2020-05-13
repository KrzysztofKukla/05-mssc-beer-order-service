package pl.kukla.krzys.msscbeerorderservice.exception;

import lombok.RequiredArgsConstructor;

/**
 * @author Krzysztof Kukla
 */
@RequiredArgsConstructor
public class NotFoundBeerOrderException extends RuntimeException {
    private final String message;

}
