package commerce.cms.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.product.AddProductCartForm;
import commerce.cms.order.domain.product.AddProductForm;
import commerce.cms.order.domain.product.AddProductItemForm;
import commerce.cms.order.domain.redis.Cart;
import commerce.cms.order.domain.repository.ProductRepository;
import commerce.cms.order.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CartApplicationTest {
  @Autowired
  private CartApplication cartApplication;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductRepository productRepository;

  @Test
  void ADD_TEST_MODIFY() {
    Long customerId = 100L;

    cartApplication.clearCart(customerId);

    Product p = add_product();
    Product result = productRepository.findWithProductItemsById(p.getId()).get();

    assertNotNull(result);

    assertEquals(result.getProductItems().size(), 1);
    assertEquals(result.getProductItems().get(0).getName(), "뉴발란스0");
    assertEquals(result.getProductItems().get(0).getPrice(), 10000);
    assertEquals(result.getProductItems().get(0).getCount(), 10);

    // refresh test
    Cart cart = cartApplication.addCart(customerId, makeAddCartForm(result));

    // 데이터가 잘 들어갔는지
    assertNotNull(cart);
    assertEquals(cart.getMessages().size(), 0);

    cart = cartApplication.getCart(customerId); // 가격 변경 메시지
    assertEquals(cart.getMessages().size(), 1);
  }

  private AddProductCartForm makeAddCartForm(Product p) {
    AddProductCartForm.ProductItem productItem =
        AddProductCartForm.ProductItem.builder()
        .id(p.getProductItems().get(0).getId())
        .name(p.getProductItems().get(0).getName())
        .count(5)
        .price(20000)
        .build();

    return AddProductCartForm.builder()
            .id(p.getId())
            .sellerId(p.getSellerId())
            .name(p.getName())
            .description(p.getDescription())
            .items(List.of(productItem))
            .build();
  }

  Product add_product() {
    Long sellerId = 1L;
    AddProductForm form = makeProductForm("뉴발란스", "신발", 1);
    return productService.addProduct(sellerId, form);
  }

  private static AddProductForm makeProductForm(String name, String description, int itemCount) {
    List<AddProductItemForm> itemForms = new ArrayList<>();
    for(int i=0; i<itemCount; i++) {
      itemForms.add(makeProductItemForm(null,name+i));
    }
    return AddProductForm.builder()
        .name(name)
        .description(description)
        .items(itemForms)
        .build();
  }

  private static AddProductItemForm makeProductItemForm(Long productId, String name) {
    return AddProductItemForm.builder()
        .productId(productId)
        .name(name)
        .price(10000)
        .count(10)
        .build();
  }
}