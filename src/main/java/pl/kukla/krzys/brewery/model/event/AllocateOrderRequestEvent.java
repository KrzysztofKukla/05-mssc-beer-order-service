package pl.kukla.krzys.brewery.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kukla.krzys.brewery.model.BeerOrderDto;

/**
 * @author Krzysztof Kukla
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateOrderRequestEvent {

    private BeerOrderDto beerOrderDto;

}
