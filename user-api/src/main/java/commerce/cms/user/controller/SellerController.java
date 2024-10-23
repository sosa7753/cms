package commerce.cms.user.controller;

import commerce.cms.domain.config.JwtAuthenticationProvider;
import commerce.cms.domain.domain.common.UserVo;
import commerce.cms.user.domain.model.Seller;
import commerce.cms.user.domain.seller.SellerDto;
import commerce.cms.user.exception.CustomException;
import commerce.cms.user.exception.ErrorCode;
import commerce.cms.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {

  private final JwtAuthenticationProvider provider;
  private final SellerService sellerService;

  @GetMapping("/getInfo")
  public ResponseEntity<SellerDto> getInfo(@RequestHeader(name= "X-AUTH-TOKEN") String token){
    UserVo vo = provider.getUserVo(token);
    Seller s =sellerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_FOUND_USER));
    return ResponseEntity.ok(SellerDto.from(s));
  }
}
