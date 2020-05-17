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
    public void listen(Message<ValidateOrderRequestEvent> message) {

        //Spring is using the Jackson to parse payload into ValidateOrderResultEvent
        ValidateOrderRequestEvent requestEvent = message.getPayload();

        //condition to fail validation
        boolean valid = requestEvent.getBeerOrderDto().getCustomerRef() == null
            || !requestEvent.getBeerOrderDto().getCustomerRef().equals("fail-validation");

        boolean sendResponse = requestEvent.getBeerOrderDto().getCustomerRef() == null
            || !requestEvent.getBeerOrderDto().getCustomerRef().equals("dont-validate");

        if (sendResponse) {
            ValidateOrderResultEvent resultEvent = ValidateOrderResultEvent.builder()
                .isValid(valid)
                .orderId(requestEvent.getBeerOrderDto().getId())
                .build();

            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, resultEvent);
        }
    }

}
