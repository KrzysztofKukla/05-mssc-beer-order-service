package pl.kukla.krzys.msscbeerorderservice.statemachine.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;
import pl.kukla.krzys.msscbeerorderservice.service.BeerOrderManagerImpl;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */

//interceptor which allows to change logic between state machine changes
@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state, Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine) {
        Optional.ofNullable(message)
            .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, " ")))
            .ifPresent(orderId -> {
                log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());
                //whenever state in state machine is changing then we are setting orderState for orderBeer ( getting by orderId )
                // and persist/save to database
                BeerOrder beerOrder = beerOrderRepository.getOne(UUID.fromString(orderId));
                beerOrder.setOrderStatus(state.getId());
                beerOrderRepository.saveAndFlush(beerOrder);
            });
    }

}
