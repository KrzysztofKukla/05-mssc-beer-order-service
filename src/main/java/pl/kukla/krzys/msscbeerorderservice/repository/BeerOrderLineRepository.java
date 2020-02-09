package pl.kukla.krzys.msscbeerorderservice.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.kukla.krzys.msscbeerorderservice.domain.BeerOrderLine;

import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
public interface BeerOrderLineRepository extends PagingAndSortingRepository<BeerOrderLine, UUID> {
}
