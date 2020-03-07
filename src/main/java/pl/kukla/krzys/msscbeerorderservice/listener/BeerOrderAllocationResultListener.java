package pl.kukla.krzys.msscbeerorderservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.BeerOrderDto;
import pl.kukla.krzys.brewery.model.event.AllocateOrderResultEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;
import pl.kukla.krzys.msscbeerorderservice.service.BeerOrderManager;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderAllocationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResultEvent allocateOrderResultEvent) {
        BeerOrderDto beerOrderDto = allocateOrderResultEvent.getBeerOrderDto();
        if (!allocateOrderResultEvent.getAllocationError() && !allocateOrderResultEvent.getPendingInventory()) {
            //allocated normally
            beerOrderManager.beerOrderAllocationPassed(beerOrderDto);
        } else if (!allocateOrderResultEvent.getAllocationError() && allocateOrderResultEvent.getPendingInventory()) {
            //pending inventory
            beerOrderManager.beerOrderAllocationPendingInventory(beerOrderDto);
        } else if (allocateOrderResultEvent.getAllocationError()) {
            //allocate errors
            beerOrderManager.beerOrderAllocationFailed(beerOrderDto);
        }
    }

}
