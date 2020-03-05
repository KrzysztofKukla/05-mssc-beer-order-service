package pl.kukla.krzys.msscbeerorderservice.domain;

/**
 * @author Krzysztof Kukla
 */
public enum BeerOrderStatusEnum {
    NEW, VALIDATED, VALIDATION_PENDING, VALIDATION_EXCEPTION,
    ALLOCATION_PENDING, ALLOCATED, ALLOCATION_EXCEPTION, PENDING_INVENTORY, PICKED_UP, DELIVERED,
    DELIVERY_EXCEPTION
}
