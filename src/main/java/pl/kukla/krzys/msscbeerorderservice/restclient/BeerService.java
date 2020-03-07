package pl.kukla.krzys.msscbeerorderservice.restclient;

import pl.kukla.krzys.brewery.model.BeerDto;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
public interface BeerService {

    BeerDto getBeerById(UUID id);

    BeerDto getBeerByUpc(String upc);

}
