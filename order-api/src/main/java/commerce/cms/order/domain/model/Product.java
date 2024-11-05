package commerce.cms.order.domain.model;

import commerce.cms.order.domain.product.AddProductForm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditOverride(forClass = BaseEntity.class)
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long sellerId;

  private String name;

  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
  private List<ProductItem> productItems = new ArrayList<>();

  public static Product of(Long sellerId, AddProductForm form) {
    return Product.builder()
        .sellerId(sellerId)
        .name(form.getName())
        .description(form.getDescription())
        .productItems(form.getItems().stream()
            .map(piFrom -> ProductItem.of(sellerId, piFrom)).collect(Collectors.toList()))
        .build();
  }

  public void addProductMapping(List<ProductItem> productItems) {
    for (ProductItem productItem : productItems) {
      productItem.setProduct(this);
    }
  }
}
