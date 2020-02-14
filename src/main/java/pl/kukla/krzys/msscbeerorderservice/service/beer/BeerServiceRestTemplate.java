package pl.kukla.krzys.msscbeerorderservice.service.beer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kukla.krzys.msscbeerorderservice.web.model.BeerDto;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@Service
@Slf4j
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
public class BeerServiceRestTemplate implements BeerService {

    private static final String BEER_PATH_V1 = "/api/v1/beer/{beerId}";
    private static final String BEER_PATH_UPC_V1 = "/api/v1/beerUpc/{upc}";

    private final RestTemplate restTemplate;

    public BeerServiceRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Setter
    private String beerServiceHost;

    @Override
    public BeerDto getBeerById(UUID beerId) {
        log.debug("Calling " + beerServiceHost + BEER_PATH_V1);
        return restTemplate.getForObject(beerServiceHost + BEER_PATH_V1, BeerDto.class, beerId);
    }

    @Override
    public BeerDto getBeerByUpc(String upc) {
        log.debug("Calling " + beerServiceHost + BEER_PATH_UPC_V1);
        return restTemplate.getForObject(beerServiceHost + BEER_PATH_UPC_V1, BeerDto.class, upc);
    }

}
