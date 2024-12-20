package commerce.cms.order.service;

import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.repository.ProductRepository;
import commerce.cms.order.exception.CustomException;
import commerce.cms.order.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

  private final ProductRepository productRepository;

  public List<Product> searchByName(String name) {
    return productRepository.searchByName(name);
  }

  // code로 상품 조회
  public Product getByProductId(Long productId) {
    return productRepository.findWithProductItemsById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
  }

  public List<Product> getListByProductIds(List<Long> productIds) {
    return productRepository.findAllByIdIn(productIds);
  }
}
