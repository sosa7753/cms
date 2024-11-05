package commerce.cms.order.service;

import static commerce.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static commerce.cms.order.exception.ErrorCode.SAME_ITEM_NAME;

import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.model.ProductItem;
import commerce.cms.order.domain.product.AddProductItemForm;
import commerce.cms.order.domain.repository.ProductItemRepository;
import commerce.cms.order.domain.repository.ProductRepository;
import commerce.cms.order.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductItemService {

  private final ProductRepository productRepository;
  private final ProductItemRepository productItemRepository;

  @Transactional
  public Product addProductItem(Long sellerId, AddProductItemForm form) {
    Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
        .orElseThrow(()-> new CustomException(NOT_FOUND_PRODUCT));
    if(product.getProductItems().stream()
        .anyMatch(item -> item.getName().equals(form.getName()))) {
      throw new CustomException(SAME_ITEM_NAME);
    }

    ProductItem productItem = ProductItem.of(sellerId, form);
    product.getProductItems().add(productItem);
    return product;
  }
}
