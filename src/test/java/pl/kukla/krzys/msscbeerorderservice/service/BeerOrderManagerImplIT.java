package pl.kukla.krzys.msscbeerorderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.ManagedWireMockServer;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.kukla.krzys.brewery.model.BeerDto;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrder;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderLine;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderStatusEnum;
import pl.kukla.krzys.msscbeerorderservice.domain.Customer;
import pl.kukla.krzys.msscbeerorderservice.repository.BeerOrderRepository;
import pl.kukla.krzys.msscbeerorderservice.repository.CustomerRepository;
import pl.kukla.krzys.msscbeerorderservice.restclient.BeerServiceRestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
//Wiremock allows to test a client
@ExtendWith(WireMockExtension.class)
@SpringBootTest
class BeerOrderManagerImplIT {

    private static final String UPC = "1234";

    @Autowired
    private BeerOrderManager beerOrderManager;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer testCustomer;

    private UUID beerId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        //'stop' method will be call on destroy Bean
        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            //it will be managed by WireMockExtension, so it will sets up everything needed for WireMock
            WireMockServer wireMockServer = ManagedWireMockServer.with(WireMockConfiguration.wireMockConfig().port(8083));
            wireMockServer.start();
            return wireMockServer;
        }

    }

    @BeforeEach
    void setUp() {
        Customer customer = Customer.builder().customerName("Test Customer").build();
        testCustomer = customerRepository.save(customer);
    }

    @Test
    void newToAllocate() throws Exception {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(UPC).build();
//        BeerPagedList beerOrderPagedList = new BeerPagedList(Collections.singletonList(beerDto));

        wireMockServer.stubFor(
            WireMock.get(BeerServiceRestTemplate.BEER_UPC_PATH_V1 + "/" + UPC)
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto)))
        );

        BeerOrder beerOrder = createBeerOrder();
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundBeerOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            Assertions.assertEquals(BeerOrderStatusEnum.ALLOCATED, foundBeerOrder.getOrderStatus());
        });

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
            //we want to make sure 'quantityAllocated' has been updated
            Assertions.assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });

    }

    private BeerOrder createBeerOrder() {

        BeerOrder beerOrder = BeerOrder.builder()
            .customer(testCustomer)
            .build();

        BeerOrderLine beerOrderLine = BeerOrderLine.builder()
            .beerId(beerId)
            .orderQuantity(1)
            .upc(UPC)
            .beerOrder(beerOrder)
            .build();
        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(beerOrderLine);

        beerOrder.setBeerOrderLines(lines);
        return beerOrder;
    }

}