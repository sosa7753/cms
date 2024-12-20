package commerce.cms.user.controller;

import commerce.cms.domain.config.JwtAuthenticationProvider;
import commerce.cms.domain.domain.common.UserVo;
import commerce.cms.user.domain.customer.ChangeBalanceForm;
import commerce.cms.user.domain.customer.CustomerDto;
import commerce.cms.user.domain.model.Customer;
import commerce.cms.user.exception.CustomException;
import commerce.cms.user.exception.ErrorCode;
import commerce.cms.user.service.customer.CustomerBalanceService;
import commerce.cms.user.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

  private final JwtAuthenticationProvider provider;
  private final CustomerService customerService;
  private final CustomerBalanceService customerBalanceService;

  @GetMapping("/getInfo")
  public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
    UserVo vo = provider.getUserVo(token);
    Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_FOUND_USER));
    return ResponseEntity.ok(CustomerDto.from(c));
  }

  @PostMapping("/balance")
  public ResponseEntity<Integer> changeBalance(
      @RequestHeader(name = "X-AUTH-TOKEN") String token,
      @RequestBody
      ChangeBalanceForm form) {

    UserVo vo = provider.getUserVo(token);
    return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
  }
}
