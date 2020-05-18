package pl.kukla.krzys.msscbeerorderservice.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kukla.krzys.brewery.model.CustomerPagedList;
import pl.kukla.krzys.msscbeerorderservice.service.CustomerService;

/**
 * @author Krzysztof Kukla
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CustomerService customerService;

    public CustomerPagedList listCustomer(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        pageNumber = pageNumber == null || pageNumber < 0 ?
            DEFAULT_PAGE_NUMBER : pageNumber;

        pageSize = pageSize == null || pageSize < 0 ?
            DEFAULT_PAGE_SIZE : pageSize;

        return customerService.listCustomers(PageRequest.of(pageNumber, pageSize));
    }

}
