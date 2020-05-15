package pl.kukla.krzys.msscbeerorderservice.service.testcomponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.event.AllocateOrderRequestEvent;
import pl.kukla.krzys.brewery.model.event.AllocateOrderResultEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message<AllocateOrderRequestEvent> message) {
        AllocateOrderRequestEvent allocateOrderRequest = message.getPayload();

        boolean pendingInventory = allocateOrderRequest.getBeerOrderDto().getCustomerRef() != null
            && allocateOrderRequest.getBeerOrderDto().getCustomerRef().equals("partial-allocation");

        boolean allocationError = allocateOrderRequest.getBeerOrderDto().getCustomerRef() == null
            && allocateOrderRequest.getBeerOrderDto().getCustomerRef().equals("fail-allocation");

        allocateOrderRequest.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
            int orderQuantity = pendingInventory ?
                beerOrderLineDto.getOrderQuantity() - 1 : beerOrderLineDto.getOrderQuantity();
            beerOrderLineDto.setQuantityAllocated(orderQuantity);
        });

        AllocateOrderResultEvent allocateOrderResultEvent = AllocateOrderResultEvent.builder()
            .beerOrderDto(allocateOrderRequest.getBeerOrderDto())
            .pendingInventory(pendingInventory)
            .allocationError(allocationError)
            .build();

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, allocateOrderResultEvent);
    }

}
