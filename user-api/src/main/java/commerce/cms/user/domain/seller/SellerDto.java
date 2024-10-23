package commerce.cms.user.domain.seller;

import commerce.cms.user.domain.model.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerDto {

  private Long id;
  private String email;

  public static SellerDto from(Seller seller) {
    return SellerDto.builder()
        .id(seller.getId())
        .email(seller.getEmail())
        .build();
  }
}
