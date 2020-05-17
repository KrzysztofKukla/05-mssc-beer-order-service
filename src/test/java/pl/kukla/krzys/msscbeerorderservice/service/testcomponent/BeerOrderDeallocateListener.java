package pl.kukla.krzys.msscbeerorderservice.service.testcomponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.event.DeallocateOrderRequestEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderDeallocateListener {

    @JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
    public void listen(Message<DeallocateOrderRequestEvent> message) {
        DeallocateOrderRequestEvent deallocateOrderRequestEvent = message.getPayload();

    }

}
