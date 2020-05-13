package pl.kukla.krzys.msscbeerorderservice.service.testcomponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.event.ValidateOrderRequestEvent;
import pl.kukla.krzys.brewery.model.event.ValidateOrderResultEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(Message message) {

        //Spring is using the Jackson to parse payload into ValidateOrderResultEvent
        ValidateOrderRequestEvent requestEvent = (ValidateOrderRequestEvent) message.getPayload();

        ValidateOrderResultEvent resultEvent = ValidateOrderResultEvent.builder()
            .isValid(true)
            .orderId(requestEvent.getBeerOrderDto().getId())
            .build();

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, resultEvent);
    }

}
