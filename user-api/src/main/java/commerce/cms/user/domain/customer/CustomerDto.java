package commerce.cms.user.domain.customer;

import commerce.cms.user.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

  private Long id;
  private String email;
  private Integer balance;

  public static CustomerDto from(Customer customer) {
    return CustomerDto.builder()
        .id(customer.getId())
        .email(customer.getEmail())
        .balance(customer.getBalance()==null?0:customer.getBalance())
        .build();
  }
}
