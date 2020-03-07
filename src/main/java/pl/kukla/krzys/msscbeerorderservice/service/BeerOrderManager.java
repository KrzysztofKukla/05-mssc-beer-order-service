package pl.kukla.krzys.msscbeerorderservice.service;

import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
//it allows to manage different actions that are coming through the system
public interface BeerOrderManager {

    //this method will be call when we create new BeerOrder
    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID beerOrderId, Boolean isValid);

}
