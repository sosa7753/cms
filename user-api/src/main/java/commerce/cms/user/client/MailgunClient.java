package commerce.cms.user.client;

import commerce.cms.user.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
@Qualifier("mailgun")
public interface MailgunClient {

  @PostMapping("sandboxe5d491ee06334b60b0606d28b259b162.mailgun.org/messages")
  ResponseEntity<String> sendMail(@SpringQueryMap SendMailForm form);
}
