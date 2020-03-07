package pl.kukla.krzys.msscbeerorderservice.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.event.ValidateOrderResultEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;
import pl.kukla.krzys.msscbeerorderservice.service.BeerOrderManager;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listenValidationResult(ValidateOrderResultEvent validateOrderResultEvent) {
        UUID beerOrderId = validateOrderResultEvent.getId();
        log.debug("Validation result for order id {}", beerOrderId);

        beerOrderManager.processValidationResult(beerOrderId, validateOrderResultEvent.getIsValid());

    }

}
