package commerce.cms.order.controller;

import commerce.cms.domain.config.JwtAuthenticationProvider;
import commerce.cms.order.domain.model.Product;
import commerce.cms.order.domain.product.ProductDto;
import commerce.cms.order.service.ProductSearchService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search/product")
@RequiredArgsConstructor
public class SearchController {

  private final ProductSearchService productSearchService;

  @GetMapping
  public ResponseEntity<List<ProductDto>> searchByNames(
      @RequestParam String name) {

    return ResponseEntity.ok(productSearchService.searchByName(name).stream()
        .map(ProductDto::withoutItemsFrom).collect(Collectors.toList()));
  }

  @GetMapping("/detail")
  public ResponseEntity<ProductDto> getDetail(
      @RequestParam Long productId) {

    return ResponseEntity.ok(
        ProductDto.from(productSearchService.getByProductId(productId)));
  }
}
