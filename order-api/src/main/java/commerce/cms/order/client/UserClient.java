package commerce.cms.order.client;

import commerce.cms.order.client.user.ChangeBalanceForm;
import commerce.cms.order.client.user.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-api", url = "${feign.client.url.user-api}")
public interface UserClient {

  @GetMapping("/getInfo")
  ResponseEntity<CustomerDto> getCustomerInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token);

  @PostMapping("/balance")
  ResponseEntity<Integer> changeBalance(
      @RequestHeader(name = "X-AUTH-TOKEN") String token,
      @RequestBody ChangeBalanceForm form);
}
