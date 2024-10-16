package commerce.cms.user.service;

import commerce.cms.user.client.MailgunClient;
import commerce.cms.user.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {

  private final MailgunClient mailgunClient;

  public String sendEmail() {
    SendMailForm form = SendMailForm.builder()
        .from("sosa7753@test.com")
        .to("sosa7753@naver.com")
        .subject("Test email from zero base")
        .text("my text")
        .build();
    return mailgunClient.sendMail(form).getBody();
  }
}
