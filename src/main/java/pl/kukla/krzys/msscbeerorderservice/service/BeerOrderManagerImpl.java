package pl.kukla.krzys.msscbeerorderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
//it allows to manage different actions that are coming through the system
@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;
    private final StateMachineInterceptor<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineInterceptor;

    //this method will be call when we create new BeerOrder
    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

        sendBeerOrderEvent(savedBeerOrder, BeerOrderEventEnum.VALIDATE_ORDER);
        return savedBeerOrder;
    }

    @Override
    public void processValidationResult(UUID beerOrderId, Boolean isValid) {
        BeerOrder beerOrder = beerOrderRepository.findOneById(beerOrderId);
        BeerOrderEventEnum beerOrderEvenEnum = isValid ?
            BeerOrderEventEnum.VALIDATION_PASSED : BeerOrderEventEnum.ALLOCATION_FAILED;
        sendBeerOrderEvent(beerOrder, beerOrderEvenEnum);
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum beerOrderEvent) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine = buildStateMachine(beerOrder);
        Message<BeerOrderEventEnum> beerOrderEventMessage = MessageBuilder.withPayload(beerOrderEvent)
            //here we are enriching (wzbogacamy) this message with orderIdHeader, because it will be needed for us
            .setHeader(ORDER_ID_HEADER, beerOrder.getId().toString())
            .build();
        stateMachine.sendEvent(beerOrderEventMessage);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> buildStateMachine(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine = stateMachineFactory.getStateMachine(beerOrder.getId());

        //to be able to change configuration/add additional properties to state machine, first we need to stop stateMachine
        stateMachine.stop();
        //and then we are setting stateMachine to specific state
        stateMachine.getStateMachineAccessor()
            .doWithAllRegions(stateMachineAccessor -> {
                stateMachineAccessor.addStateMachineInterceptor(stateMachineInterceptor);
                stateMachineAccessor.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null,
                    null));
            });
        stateMachine.start();
        return stateMachine;
    }

}
