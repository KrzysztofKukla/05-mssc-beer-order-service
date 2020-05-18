package pl.kukla.krzys.msscbeerorderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kukla.krzys.brewery.model.CustomerDto;
import pl.kukla.krzys.brewery.model.CustomerPagedList;
import pl.kukla.krzys.msscbeerorderservice.domain.Customer;
import pl.kukla.krzys.msscbeerorderservice.repository.CustomerRepository;
import pl.kukla.krzys.msscbeerorderservice.service.CustomerService;
import pl.kukla.krzys.msscbeerorderservice.web.mapper.CustomerMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Kukla
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomers(PageRequest givenPageRequest) {
        Page<Customer> customerPage = customerRepository.findAll(givenPageRequest);

        List<CustomerDto> customerDtoList = customerPage.stream()
            .map(customerMapper::customerToDto)
            .collect(Collectors.toList());
        Pageable pageable = customerPage.getPageable();
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return new CustomerPagedList(customerDtoList, pageRequest, customerPage.getTotalElements());
    }

}
