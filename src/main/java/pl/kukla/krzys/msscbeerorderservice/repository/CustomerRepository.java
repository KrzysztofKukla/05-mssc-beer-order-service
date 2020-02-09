package pl.kukla.krzys.msscbeerorderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kukla.krzys.msscbeerorderservice.domain.Customer;

import java.util.List;
import java.util.UUID;

/**
 * @author Krzysztof Kukla
 */
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findAllByCustomerNameLike(String customerName);

}
