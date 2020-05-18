package pl.kukla.krzys.msscbeerorderservice.statemachine.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.service.impl.BeerOrderManagerImpl;

import java.util.Objects;

/**
 * @author Krzysztof Kukla
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = Objects.requireNonNull(stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER)).toString();
        log.error("Compensating transaction - Validation failed {}", beerOrderId);
    }

}
