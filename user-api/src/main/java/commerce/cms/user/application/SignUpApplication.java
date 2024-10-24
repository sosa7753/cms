package commerce.cms.user.application;

import commerce.cms.user.client.MailgunClient;
import commerce.cms.user.client.mailgun.SendMailForm;
import commerce.cms.user.domain.SignUpForm;
import commerce.cms.user.domain.model.Customer;
import commerce.cms.user.domain.model.Seller;
import commerce.cms.user.exception.CustomException;
import commerce.cms.user.exception.ErrorCode;
import commerce.cms.user.service.customer.SignUpCustomerService;
import commerce.cms.user.service.seller.SignUpSellerService;
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
  private final SignUpSellerService signUpSellerService;

  public void customerVerify(String email, String code) {
    signUpCustomerService.verifyEmail(email, code);
  }

  public String customerSignUp(SignUpForm form) {
    if (signUpCustomerService.isEmailExist(form.getEmail())) {
      throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
    } else {
      Customer c = signUpCustomerService.signUp(form);
      String code = getRandomCode();

      SendMailForm sendMailForm = SendMailForm.builder()
          .from("tester@test.com")
          .to(c.getEmail())
          .subject("Verification Email!!")
          .text(getVerificationEmailBody(c.getEmail(), c.getName(), "customer", code))
          .build();
      log.info("send Email result : " + mailgunClient.sendMail(sendMailForm));
      signUpCustomerService.changeCustomerValidateEmail(c.getId(), code);
      return "회원 가입에 성공했습니다.";
    }
  }

  public void sellerVerify(String email, String code) {
    signUpSellerService.verifyEmail(email, code);
  }

  public String sellerSignUp(SignUpForm form) {
    if (signUpSellerService.isEmailExist(form.getEmail())) {
      throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
    } else {
      Seller s = signUpSellerService.signUp(form);
      String code = getRandomCode();

      SendMailForm sendMailForm = SendMailForm.builder()
          .from("tester@test.com")
          .to(s.getEmail())
          .subject("Verification Email!!")
          .text(getVerificationEmailBody(s.getEmail(), s.getName(), "seller", code))
          .build();
      log.info("send Email result : " + mailgunClient.sendMail(sendMailForm));
      signUpSellerService.changeSellerValidateEmail(s.getId(), code);
      return "회원 가입에 성공했습니다.";
    }
  }
  private String getRandomCode() {
    return RandomStringUtils.random(10, true, true);
  }

  private String getVerificationEmailBody(String email, String name, String type, String code) {
    StringBuilder builder = new StringBuilder();
    return builder.append("Hello ").append(name).append("! Please Click Link for verification. \n\n")
        .append("http://localhost:8081/signup/"+type+"/verify?email=")
        .append(email)
        .append("&code=")
        .append(code).toString();
  }
}
