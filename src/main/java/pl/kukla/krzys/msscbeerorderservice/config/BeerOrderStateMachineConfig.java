package pl.kukla.krzys.msscbeerorderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderEventEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;

import java.util.EnumSet;

/**
 * @author Krzysztof Kukla
 */
@Configuration
@EnableStateMachineFactory // it scans Spring Components to generate ( create ) State Machine
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    //here we defined all possible states
    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states) throws Exception {
        states.withStates()
            //initial status
            .initial(BeerOrderStatusEnum.NEW)
            .states(EnumSet.allOf(BeerOrderStatusEnum.class))
            //possible end statuses
            .end(BeerOrderStatusEnum.PICKED_UP)
            .end(BeerOrderStatusEnum.DELIVERED)
            .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
            .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
            .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions) throws Exception {
        transitions
            .withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.NEW).event(BeerOrderEventEnum.VALIDATE_ORDER)
            .and().withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATED).event(BeerOrderEventEnum.VALIDATION_PASSED)
            .and().withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION).event(BeerOrderEventEnum.VALIDATION_FAILED);
    }

}
