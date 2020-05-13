package pl.kukla.krzys.msscbeerorderservice.restclient;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kukla.krzys.brewery.model.BeerDto;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
@Service
@Slf4j
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
public class BeerServiceRestTemplate implements BeerService {

    public static final String BEER_PATH_V1 = "/api/v1/beer";
    public static final String BEER_UPC_PATH_V1 = "/api/v1/beerUpc";

    private final RestTemplate restTemplate;

    public BeerServiceRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Setter
    private String beerServiceHost;

    @Override
    public BeerDto getBeerById(UUID beerId) {
        log.debug("Calling " + beerServiceHost + BEER_PATH_V1);
        return restTemplate.getForObject(beerServiceHost + BEER_PATH_V1 + "/" + beerId, BeerDto.class);
    }

    @Override
    public BeerDto getBeerByUpc(String upc) {
        log.debug("Calling " + beerServiceHost + BEER_UPC_PATH_V1);
        return restTemplate.getForObject(beerServiceHost + BEER_UPC_PATH_V1 + "/" + upc, BeerDto.class);
    }

}
