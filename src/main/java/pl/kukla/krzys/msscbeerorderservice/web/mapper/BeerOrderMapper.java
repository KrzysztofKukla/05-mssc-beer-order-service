package pl.kukla.krzys.msscbeerorderservice.web.mapper;

/**
 * @author Krzysztof Kukla
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kukla.krzys.brewery.model.BeerOrderDto;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

    //mapping customerId -> customer.id entity
    @Mapping(target = "customerId", source = "customer.id")
    BeerOrderDto beerOrderToDto(BeerOrder beerOrder);

    BeerOrder dtoToBeerOrder(BeerOrderDto dto);

}
