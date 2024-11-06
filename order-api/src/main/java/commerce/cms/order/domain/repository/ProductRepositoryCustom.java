package commerce.cms.order.domain.repository;

import commerce.cms.order.domain.model.Product;
import java.util.List;

public interface ProductRepositoryCustom {
  List<Product> searchByName(String name);

}
