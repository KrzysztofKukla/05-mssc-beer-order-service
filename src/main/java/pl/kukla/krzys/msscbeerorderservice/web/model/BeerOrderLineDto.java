package pl.kukla.krzys.msscbeerorderservice.web.model;

/**
 * @author Krzysztof Kukla
 */

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BeerOrderLineDto extends BaseItem {

    private String upc;
    private String beerName;
    private UUID beerId;
    private Integer orderQuantity = 0;
    private BeerStyleEnum beerStyleEnum;
    private BigDecimal price;

    @Builder
    public BeerOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                            String upc, String beerName, UUID beerId, Integer orderQuantity, BeerStyleEnum beerStyleEnum, BigDecimal price) {
        super(id, version, createdDate, lastModifiedDate);
        this.upc = upc;
        this.beerName = beerName;
        this.beerStyleEnum = beerStyleEnum;
        this.beerId = beerId;
        this.orderQuantity = orderQuantity;
        this.price = price;
    }


}
