package pl.kukla.krzys.msscbeerorderservice.web.mapper;

/**
 * @author Krzysztof Kukla
 */

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import pl.kukla.krzys.brewery.model.BeerOrderLineDto;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderLine;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(value = BeerOrderLineDecorator.class)
public interface BeerOrderLineMapper {
    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);

}
