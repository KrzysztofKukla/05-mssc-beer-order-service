package pl.kukla.krzys.msscbeerorderservice.service.beer;

import pl.kukla.krzys.msscbeerorderservice.web.model.BeerDto;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
public interface BeerService {

    BeerDto getBeerById(UUID id);

    BeerDto getBeerByUpc(String upc);

}
