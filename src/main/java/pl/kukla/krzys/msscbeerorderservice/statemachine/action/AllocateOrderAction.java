package pl.kukla.krzys.msscbeerorderservice.statemachine.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.event.AllocateOrderRequestEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;
import pl.kukla.krzys.msscbeerorderservice.service.impl.BeerOrderManagerImpl;
import pl.kukla.krzys.msscbeerorderservice.web.mapper.BeerOrderMapper;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = Objects.requireNonNull(stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER)).toString();
//        String beerOrderId = Objects.requireNonNull(stateContext.getMessage().getHeaders().getId()).toString();
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString(beerOrderId));

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
                AllocateOrderRequestEvent allocateOrderRequestEvent = AllocateOrderRequestEvent.builder()
                    .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                    .build();
                log.debug("Sent allocation request for orderId {}", beerOrderId);
                jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE, allocateOrderRequestEvent);
            },
            () -> log.error("BeerOrder Not found!")
        );

        log.debug("Sent allocation request for beer order id {}", beerOrderId);
    }

}
