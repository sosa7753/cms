package commerce.cms.order.service;

import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.model.ProductItem;
import commerce.cms.order.domain.product.AddProductForm;
import commerce.cms.order.domain.product.UpdateProductForm;
import commerce.cms.order.domain.product.UpdateProductItemForm;
import commerce.cms.order.domain.repository.ProductRepository;
import commerce.cms.order.exception.CustomException;
import commerce.cms.order.exception.ErrorCode;
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

  @Transactional
  public Product updateProduct(Long sellerId, UpdateProductForm form) {
    Product product = productRepository.findBySellerIdAndId(sellerId, form.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

    product.setName(form.getName());
    product.setDescription(form.getDescription());

    for(UpdateProductItemForm itemForm : form.getItems()) {
      ProductItem item = product.getProductItems().stream()
          .filter(pi -> pi.getId().equals(itemForm.getId())) // 변경하고 하자는 id가 같아야함.
          .findFirst().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));

      item.setName(itemForm.getName());
      item.setPrice(itemForm.getPrice());
      item.setCount(itemForm.getCount());
    }
    return product;
  }
}
