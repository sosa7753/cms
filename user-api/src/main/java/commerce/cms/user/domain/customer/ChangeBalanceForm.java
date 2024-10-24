package commerce.cms.user.domain.customer;

import lombok.Getter;

@Getter
public class ChangeBalanceForm {

  private String from;
  private String message;
  private Integer money; // 변경 금액

}
