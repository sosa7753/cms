package commerce.cms.user.service;

import commerce.cms.user.domain.SignUpForm;
import commerce.cms.user.domain.model.Customer;
import commerce.cms.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

  private final CustomerRepository customerRepository;

  public Customer signUp(SignUpForm form) {
    return customerRepository.save(Customer.from(form));
  }

}
