package pl.kukla.krzys.msscbeerorderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.ManagedWireMockServer;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
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
        String upc = "1234";
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(upc).build();
//        BeerPagedList beerOrderPagedList = new BeerPagedList(Collections.singletonList(beerDto));

        wireMockServer.stubFor(
            WireMock.get(BeerServiceRestTemplate.BEER_PATH_UPC_V1 + "/" + upc)
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto)))
        );

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