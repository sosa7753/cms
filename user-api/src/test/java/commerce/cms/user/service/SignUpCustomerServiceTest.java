package commerce.cms.user.service;

import commerce.cms.user.domain.SignUpForm;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpCustomerServiceTest {

  @Autowired
  private SignUpCustomerService service;

  @Test
  void signUp() {
    SignUpForm form = SignUpForm.builder()
        .name("name2")
        .birth(LocalDate.now())
        .email("abcd@gmail.com")
        .password("2")
        .phone("01000000001")
        .build();

    Assertions.assertThat(service.signUp(form).getId()).isNotNull();
  }
}