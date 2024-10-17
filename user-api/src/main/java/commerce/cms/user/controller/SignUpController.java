package commerce.cms.user.controller;

import commerce.cms.user.application.SignUpApplication;
import commerce.cms.user.domain.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

  private final SignUpApplication signUpApplication;

  /**
   * 회원 가입
   */
  @PostMapping
  public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm form) {
    return ResponseEntity.ok(signUpApplication.customerSignUp(form));
  }

  /**
   * 회원 인증
   */
  @PutMapping("customer/verify")
  public ResponseEntity<String> verifyCustomer(String email, String code) {
    signUpApplication.customerVerify(email, code);
    return ResponseEntity.ok("인증이 완료되었습니다.");
  }
}
