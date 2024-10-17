package commerce.cms.user.service;

import static commerce.cms.user.exception.ErrorCode.ALREADY_VERIFY;
import static commerce.cms.user.exception.ErrorCode.EXPIRE_CODE;
import static commerce.cms.user.exception.ErrorCode.NOT_FOUND_USER;
import static commerce.cms.user.exception.ErrorCode.WRONG_VERIFICATION;

import commerce.cms.user.domain.SignUpForm;
import commerce.cms.user.domain.model.Customer;
import commerce.cms.user.exception.CustomException;
import commerce.cms.user.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

  private final CustomerRepository customerRepository;

  public Customer signUp(SignUpForm form) {
    return customerRepository.save(Customer.from(form));
  }

  public boolean isEmailExist(String email) {
    return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
        .isPresent();
  }

  @Transactional
  public void verifyEmail(String email, String code) {
    Customer customer = customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
        .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

    if(customer.isVerify()) {
      throw new CustomException(ALREADY_VERIFY);
    }

    if(!customer.getVerificationCode().equals(code)) {
      throw new CustomException(WRONG_VERIFICATION);
    }

    if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
      throw new CustomException(EXPIRE_CODE);
    }
    customer.setVerify(true);
  }

  @Transactional
  public LocalDateTime changeCustomerValidateEmail(Long customerId, String verificationCode) {
    Optional<Customer> customerOptional = customerRepository.findById(customerId);

    if(customerOptional.isPresent()) {
      Customer customer = customerOptional.get();
      customer.setVerificationCode(verificationCode);
      customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
      return customer.getVerifyExpiredAt();
    }
    throw new CustomException(NOT_FOUND_USER);
  }
}
