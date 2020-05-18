package pl.kukla.krzys.msscbeerorderservice.web.mapper;

import org.mapstruct.Mapper;
import pl.kukla.krzys.brewery.model.CustomerDto;
import pl.kukla.krzys.msscbeerorderservice.domain.Customer;

/**
 * @author Krzysztof Kukla
 */
@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    Customer dtoToCustomer(CustomerDto customerDto);
    CustomerDto customerToDto(Customer customer);
}
