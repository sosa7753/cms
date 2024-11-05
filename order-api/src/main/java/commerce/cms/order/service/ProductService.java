package commerce.cms.order.service;

import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.product.AddProductForm;
import commerce.cms.order.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  @Transactional
  public Product addProduct(Long sellerId, AddProductForm form) {
    Product product = Product.of(sellerId, form);
    product.addProductMapping(product.getProductItems());
    return productRepository.save(product);
  }
}
