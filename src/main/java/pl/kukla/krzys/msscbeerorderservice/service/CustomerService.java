package pl.kukla.krzys.msscbeerorderservice.service;

import org.springframework.data.domain.PageRequest;
import pl.kukla.krzys.brewery.model.CustomerPagedList;

/**
 * @author Krzysztof Kukla
 */
public interface CustomerService {
    CustomerPagedList listCustomers(PageRequest pageRequest);

}
