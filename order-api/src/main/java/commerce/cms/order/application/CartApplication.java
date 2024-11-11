package commerce.cms.order.application;

import static commerce.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;

import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.model.ProductItem;
import commerce.cms.order.domain.product.AddProductCartForm;
import commerce.cms.order.domain.redis.Cart;
import commerce.cms.order.exception.CustomException;
import commerce.cms.order.service.CartService;
import commerce.cms.order.service.ProductSearchService;
import java.util.ArrayList;
import java.util.List;
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
    Cart cart = cartService.getCart(customerId);
    if(cart != null && !addAble(cart, product, form)) {
      throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
    }

    return cartService.addCart(customerId, form);
  }

  public Cart updateCart(Long customerId, Cart cart) {
    // 실질적으로 변하는 데이터
    // 상품의 삭제, 수량 변경
    cartService.putCart(customerId, cart);
    return getCart(customerId);
  }

  /**
   * 1. 장바구니에 상품을 추가했다.
   * 2. 상품의 가격이나 수량이 변동된다.
   *
   */
  public Cart getCart(Long customerId) {
    Cart cart = refreshCart(cartService.getCart(customerId));
    Cart returnCart = new Cart(); // 메세지가 있는 cart
    returnCart.setCustomerId(customerId);
    returnCart.setProducts(cart.getProducts());
    returnCart.setMessages(cart.getMessages());
    cart.setMessages(new ArrayList<>());
    cartService.putCart(customerId, cart);
    return returnCart; // 메시지가 있는걸 리턴하고 없는건 redis에 저장.

    // 2. 메시지를 보고 난 다음에는, 이미 본 메시지는 스팸이 되기 때문에 제거한다.
  }

  public void clearCart(Long customerId) {
    cartService.putCart(customerId, null);
  }

  private Cart refreshCart(Cart cart) {
    // 1. 상품이나 상품의 아이템의 정보, 가격, 수령이 변경 되었는지 체크하고
    // 그에 맞는 알람 제공.
    // 상품의 수량, 가격을 우리가 임의로 변경한다.
    Map<Long, Product> productMap = productSearchService.getListByProductIds(
        cart.getProducts().stream().map(Cart.Product::getId).toList())
            .stream().collect(Collectors.toMap(Product::getId, product->product));

    for(int i=0; i<cart.getProducts().size(); i++) {
      Cart.Product cartProduct = cart.getProducts().get(i);
      Product p = productMap.get(cartProduct.getId()); // 카트 상품에 맞는 DB 상품 조회.
      if (p == null) {
        cart.getProducts().remove(cartProduct);
        i--;
        cart.addMessage(cartProduct.getName() + " 상품이 삭제되었습니다.");
        continue;
      }
      Map<Long, ProductItem> productItemMap = p.getProductItems().stream() // DB ProductItem
          .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

      // 변경 및 삭제에 대한 메시지는 한번에 받자.
      List<String> tmpMessages = new ArrayList<>();
      for (int j = 0; j < cartProduct.getItems().size(); j++) { // 가격과 수량 비교
        Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
        ProductItem pi = productItemMap.get(cartProductItem.getId());
        if (pi == null) {
          cartProduct.getItems().remove(cartProductItem);
          j--;
          tmpMessages.add(cartProductItem.getName() + " 옵션이 삭제되었습니다.");
          continue;
        }

        boolean isPriceChanged = false, isCountNotEnough = false;

        if (!cartProductItem.getPrice().equals(pi.getPrice())) {
          isPriceChanged = true;
          cartProductItem.setPrice(pi.getPrice());
        }

        if (cartProductItem.getCount() > pi.getCount()) {
          isCountNotEnough = true;
          cartProductItem.setCount(pi.getCount());
        }

        if (isPriceChanged && isCountNotEnough) {
          tmpMessages.add(cartProductItem.getName() + " 가격변동, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");

        } else if (isPriceChanged) {
          tmpMessages.add(cartProductItem.getName() + " 가격이 변동되었습니다.");

        } else if (isCountNotEnough) {
          tmpMessages.add(cartProductItem.getName() + " 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
        }
      }
      // 상품 아이템이 모두 없다면, 상품도 삭제해주어야함.
      if (cartProduct.getItems().isEmpty()) {
        cart.getProducts().remove(cartProduct);
        cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
        i--;
      } else if (!tmpMessages.isEmpty()) {
        StringBuilder sb = new StringBuilder();
        sb.append(cartProduct.getName()).append(" 상품의 변동사항 : ");
        for(String message : tmpMessages) {
          sb.append(message);
          sb.append(", ");
        }
        cart.addMessage(sb.toString());
      }
    }
    return cartService.putCart(cart.getCustomerId(), cart);
  }

  private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
    Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
        .findFirst().orElse(null);
    if(cartProduct == null) {
      return true;
    }

    Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
        .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
    Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
        .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

    return form.getItems().stream().noneMatch( // 하나라도 조건에 부합하면 false
        formItem -> {
          Integer cartCount = cartItemCountMap.get(formItem.getId());
          if(cartCount == null) {
            cartCount = 0;
          }
          Integer currentCount = currentItemCountMap.get(formItem.getId());
          return formItem.getCount()+cartCount > currentCount;
        });
  }
}
