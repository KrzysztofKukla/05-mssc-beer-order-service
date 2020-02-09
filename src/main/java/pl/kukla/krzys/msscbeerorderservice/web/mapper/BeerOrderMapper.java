package pl.kukla.krzys.msscbeerorderservice.web.mapper;

/**
 * @author Krzysztof Kukla
 */

import org.mapstruct.Mapper;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.web.model.BeerOrderDto;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

    BeerOrderDto beerOrderToDto(BeerOrder beerOrder);

    BeerOrder dtoToBeerOrder(BeerOrderDto dto);

}
