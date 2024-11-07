package commerce.cms.order.application;

import static commerce.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static commerce.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.model.ProductItem;
import commerce.cms.order.domain.product.AddProductCartForm;
import commerce.cms.order.domain.redis.Cart;
import commerce.cms.order.exception.CustomException;
import commerce.cms.order.service.CartService;
import commerce.cms.order.service.ProductSearchService;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartApplication {
  private final CartService cartService;
  private final ProductSearchService productSearchService;

  public Cart addCart(Long customerId, AddProductCartForm form) {
    Product product = productSearchService.getByProductId(form.getId());
    if(product ==null) {
      throw new CustomException(NOT_FOUND_PRODUCT);
    }
    Cart cart = cartService.getCart(customerId);
    if(cart != null && !addAble(cart, product, form)) {
      throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
    }

    return cartService.addCart(customerId, form);
  }

  private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
    Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
        .findFirst().orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

    Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
        .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
    Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
        .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

    return form.getItems().stream().noneMatch( // 하나라도 조건에 부합하면 false
        formItem -> {
          Integer cartCount = cartItemCountMap.get(formItem.getId());
          Integer currentCount = currentItemCountMap.get(formItem.getId());
          return formItem.getCount()+cartCount > currentCount;
        });
  }
}
