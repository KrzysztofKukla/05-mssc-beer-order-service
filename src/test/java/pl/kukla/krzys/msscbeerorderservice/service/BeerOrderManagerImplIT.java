package pl.kukla.krzys.msscbeerorderservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderLine;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.Customer;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;
import pl.kukla.krzys.msscbeerorderservice.repository.CustomerRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@SpringBootTest
class BeerOrderManagerImplIT {

    @Autowired
    private BeerOrderManager beerOrderManager;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    private UUID beerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        Customer customer = Customer.builder().customerName("Test Customer").build();
        testCustomer = customerRepository.save(customer);
    }

    @Test
    void newToAllocate() throws Exception {
        BeerOrder beerOrder = createBeerOrder();
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Assertions.assertNotNull(savedBeerOrder);
        Assertions.assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder.getOrderStatus());

    }

    private BeerOrder createBeerOrder() {

        BeerOrder beerOrder = BeerOrder.builder()
            .customer(testCustomer)
            .build();

        BeerOrderLine beerOrderLine = BeerOrderLine.builder()
            .beerId(beerId)
            .orderQuantity(1)
            .beerOrder(beerOrder)
            .build();
        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(beerOrderLine);

        beerOrder.setBeerOrderLines(lines);
        return beerOrder;
    }

}