package pl.kukla.krzys.msscbeerorderservice.statemachine.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.brewery.model.BeerOrderDto;
import pl.kukla.krzys.brewery.model.event.DeallocateOrderRequestEvent;
import pl.kukla.krzys.msscbeerorderservice.config.JmsConfig;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.exception.BeerOrderNotFoundException;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;
import pl.kukla.krzys.msscbeerorderservice.service.BeerOrderManagerImpl;
import pl.kukla.krzys.msscbeerorderservice.web.mapper.BeerOrderMapper;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeallocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {

        String beerOrderId = Objects.requireNonNull(stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER)).toString();
        BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(beerOrderId))
            .orElseThrow(() -> new BeerOrderNotFoundException(beerOrderId));

        BeerOrderDto beerOrderDto = beerOrderMapper.beerOrderToDto(beerOrder);

        DeallocateOrderRequestEvent deallocateOrderRequestEvent = DeallocateOrderRequestEvent.builder()
            .beerOrderDto(beerOrderDto)
            .build();

        jmsTemplate.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE, deallocateOrderRequestEvent);

        log.debug("Deallocate order request sent for beerOrderId {}", beerOrderDto);
    }

}
