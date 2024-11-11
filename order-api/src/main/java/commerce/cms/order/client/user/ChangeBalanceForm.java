package commerce.cms.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBalanceForm {

  private String from;
  private String message;
  private Integer money; // 변경 금액

}
