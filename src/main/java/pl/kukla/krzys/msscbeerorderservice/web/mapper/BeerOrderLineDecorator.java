package pl.kukla.krzys.msscbeerorderservice.web.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import pl.kukla.krzys.brewery.model.BeerDto;
import pl.kukla.krzys.brewery.model.BeerOrderLineDto;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderLine;
import pl.kukla.krzys.msscbeerorderservice.restclient.BeerService;

/**
 * @author Krzysztof Kukla
 */
public abstract class BeerOrderLineDecorator implements BeerOrderLineMapper {

    private BeerService beerService;
    private BeerOrderLineMapper beerOrderLineMapper;

    @Autowired
    public void setBeerService(BeerService beerService) {
        this.beerService = beerService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setBeerOrderLineMapper(BeerOrderLineMapper beerOrderLineMapper) {
        this.beerOrderLineMapper = beerOrderLineMapper;
    }

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        BeerOrderLineDto beerOrderLineDto = beerOrderLineMapper.beerOrderLineToDto(line);
        BeerDto beerDto = beerService.getBeerByUpc(line.getUpc());
        beerOrderLineDto.setBeerId(beerDto.getId());
        beerOrderLineDto.setBeerName(beerDto.getBeerName());
        beerOrderLineDto.setBeerStyleEnum(beerDto.getBeerStyle());
        beerOrderLineDto.setUpc(beerDto.getUpc());
        beerOrderLineDto.setPrice(beerDto.getPrice());
        return beerOrderLineDto;
    }

}
