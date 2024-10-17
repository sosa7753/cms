package commerce.cms.user.application;

import commerce.cms.user.client.MailgunClient;
import commerce.cms.user.client.mailgun.SendMailForm;
import commerce.cms.user.domain.SignUpForm;
import commerce.cms.user.domain.model.Customer;
import commerce.cms.user.exception.CustomException;
import commerce.cms.user.exception.ErrorCode;
import commerce.cms.user.service.SignUpCustomerService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpApplication {

  private final MailgunClient mailgunClient;
  private final SignUpCustomerService signUpCustomerService;

  public void customerVerify(String email, String code) {
    signUpCustomerService.verifyEmail(email, code);
  }

  public String customerSignUp(SignUpForm form) {
    if (signUpCustomerService.isEmailExist(form.getEmail())) {
      throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
    } else {
      Customer c = signUpCustomerService.signUp(form);
      LocalDateTime now = LocalDateTime.now();
      String code = getRandomCode();

      SendMailForm sendMailForm = SendMailForm.builder()
          .from("tester@test.com")
          .to(c.getEmail())
          .subject("Verification Email!!")
          .text(getVerificationEmailBody(c.getEmail(), c.getName(), code))
          .build();
      mailgunClient.sendMail(sendMailForm);
      signUpCustomerService.changeCustomerValidateEmail(c.getId(), code);
      return "회원 가입에 성공했습니다.";
    }
  }
  private String getRandomCode() {
    return RandomStringUtils.random(10, true, true);
  }

  private String getVerificationEmailBody(String email, String name, String code) {
    StringBuilder builder = new StringBuilder();
    return builder.append("Hello ").append(name).append("! Please Click Link for verification. \n\n")
        .append("http://localhost:8081/singup/customer/verify?email=")
        .append(email)
        .append("&code=")
        .append(code).toString();
  }
}
