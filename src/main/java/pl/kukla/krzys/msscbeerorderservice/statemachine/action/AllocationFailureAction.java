package pl.kukla.krzys.msscbeerorderservice.statemachine.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.event.AllocationFailureEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.service.impl.BeerOrderManagerImpl;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = Objects.requireNonNull(stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER)).toString();

        AllocationFailureEvent allocationFailureEvent = AllocationFailureEvent.builder()
            .orderId(UUID.fromString(beerOrderId))
            .build();

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_FAILURE_QUEUE, allocationFailureEvent);

        log.debug("Allocation failure message sent with orderId->{}", beerOrderId);
    }

}
