package commerce.cms.user.application;

import commerce.cms.domain.config.JwtAuthenticationProvider;
import commerce.cms.domain.domain.common.UserType;
import commerce.cms.user.domain.SignInForm;
import commerce.cms.user.domain.model.Customer;
import commerce.cms.user.domain.model.Seller;
import commerce.cms.user.exception.CustomException;
import commerce.cms.user.exception.ErrorCode;
import commerce.cms.user.service.customer.CustomerService;
import commerce.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

  private final CustomerService customerService;
  private final SellerService sellerService;
  private final JwtAuthenticationProvider provider;

  public String customerLoginToken(SignInForm form) {
    // 1. 로그인 가능 여부
    Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
        .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

    // 2. 토큰을 발행하고 response 한다.
    return provider.createToken(c.getEmail(), c.getId(), UserType.CUSTOMER);
  }

  public String sellerLoginToken(SignInForm form) {
    // 1. 로그인 가능 여부
    Seller s = sellerService.findValidSeller(form.getEmail(), form.getPassword())
        .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

    // 2. 토큰을 발행하고 response 한다.
    return provider.createToken(s.getEmail(), s.getId(), UserType.SELLER);
  }
}
