package pl.kukla.krzys.msscbeerorderservice.service;

/**
 * @author Krzysztof Kukla
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.Customer;
import pl.kukla.krzys.msscbeerorderservice.domain.OrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.exception.NotFoundException;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;
import pl.kukla.krzys.msscbeerorderservice.repository.CustomerRepository;
import pl.kukla.krzys.msscbeerorderservice.web.mapper.BeerOrderMapper;
import pl.kukla.krzys.msscbeerorderservice.web.model.BeerOrderDto;
import pl.kukla.krzys.msscbeerorderservice.web.model.BeerOrderPagedList;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final ApplicationEventPublisher publisher;

    public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository,
                                CustomerRepository customerRepository,
                                BeerOrderMapper beerOrderMapper, ApplicationEventPublisher publisher) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerOrderMapper = beerOrderMapper;
        this.publisher = publisher;
    }

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Cannot find customer with id->" + customerId.toString()));

        Page<BeerOrder> beerOrderPage =
            beerOrderRepository.findAllByCustomer(customer, pageable);

        return new BeerOrderPagedList(beerOrderPage
            .stream()
            .map(beerOrderMapper::beerOrderToDto)
            .collect(Collectors.toList()), PageRequest.of(
            beerOrderPage.getPageable().getPageNumber(),
            beerOrderPage.getPageable().getPageSize()),
            beerOrderPage.getTotalElements());
    }

    @Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Cannot find customer with id->" + customerId.toString()));

        BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
        beerOrder.setId(null); //should not be set by outside client
        beerOrder.setCustomer(customer);
        beerOrder.setOrderStatus(OrderStatusEnum.NEW);
        beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));
        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

        log.debug("Saved Beer Order: " + beerOrder.getId());

        //todo impl
        //  publisher.publishEvent(new NewBeerOrderEvent(savedBeerOrder));

        return beerOrderMapper.beerOrderToDto(savedBeerOrder);
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);

        beerOrderRepository.save(beerOrder);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);

            if (beerOrderOptional.isPresent()) {
                BeerOrder beerOrder = beerOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if (beerOrder.getCustomer().getId().equals(customerId)) {
                    return beerOrder;
                }
            }
            throw new RuntimeException("Beer Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }

}
