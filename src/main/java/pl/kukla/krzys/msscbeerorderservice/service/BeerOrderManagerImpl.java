package pl.kukla.krzys.msscbeerorderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kukla.krzys.brewery.model.BeerOrderDto;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.exception.BeerOrderNotFoundException;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Krzysztof Kukla
 */
//it allows to manage different actions that are coming through the system
@Service
@RequiredArgsConstructor
@Slf4j
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

    @Transactional
    @Override
    public void processValidationResult(UUID beerOrderId, Boolean isValid) {
        if (isValid) {
            getBeerOrderAndSendEvent(beerOrderId, BeerOrderEventEnum.VALIDATION_PASSED);

            //wait for status change
            awaitForStatus(beerOrderId, BeerOrderStatusEnum.VALIDATED);

            //when we invoke above 'getBeerOrderAndSendEvent' method above then interceptor is gonna saves it
            //so if we want to have a fresh object we need to call to database again and get it
            //this in NOT very expensive, because Hibernate is caching things, so not always hit to database
            BeerOrder validatedOrder = beerOrderRepository.findById(beerOrderId)
                .orElseThrow(() -> new BeerOrderNotFoundException(beerOrderId.toString()));
            sendBeerOrderEvent(validatedOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
        } else {
            getBeerOrderAndSendEvent(beerOrderId, BeerOrderEventEnum.VALIDATION_FAILED);
        }
    }

    @Override
    public void beerOrderAllocationPassed(BeerOrderDto beerOrderDto) {
        getBeerOrderAndSendEvent(beerOrderDto.getId(), BeerOrderEventEnum.ALLOCATION_SUCCESS);
        awaitForStatus(beerOrderDto.getId(), BeerOrderStatusEnum.ALLOCATED);
        updateAllocatedQty(beerOrderDto);
    }

    @Override
    public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        getBeerOrderAndSendEvent(beerOrderDto.getId(), BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
        updateAllocatedQty(beerOrderDto);
    }

    @Override
    public void beerOrderAllocationFailed(UUID id) {
        getBeerOrderAndSendEvent(id, BeerOrderEventEnum.ALLOCATION_FAILED);
    }

    @Override
    public void beerOrderPickedUp(UUID id) {
        getBeerOrderAndSendEvent(id, BeerOrderEventEnum.BEER_ORDER_PICKED_UP);
    }

    @Override
    public void cancelOrder(UUID id) {
        getBeerOrderAndSendEvent(id, BeerOrderEventEnum.CANCEL_ORDER);
    }

    private void getBeerOrderAndSendEvent(UUID id, BeerOrderEventEnum beerOrderEventEnum) {
        BeerOrder beerOrder = beerOrderRepository.findById(id)
            .orElseThrow(() -> new BeerOrderNotFoundException(id.toString()));
        sendBeerOrderEvent(beerOrder, beerOrderEventEnum);
    }

    private void updateAllocatedQty(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> allocatedOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                    if (beerOrderLine.getId().equals(beerOrderLineDto.getId())) {
                        beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            beerOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + beerOrderDto.getId()));
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

    private void awaitForStatus(UUID beerOrderId, BeerOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            beerOrderRepository.findById(beerOrderId).ifPresentOrElse(beerOrder -> {
                if (beerOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + beerOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

}
