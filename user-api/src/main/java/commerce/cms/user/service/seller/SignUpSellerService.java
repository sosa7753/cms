package commerce.cms.user.service.seller;

import static commerce.cms.user.exception.ErrorCode.ALREADY_VERIFY;
import static commerce.cms.user.exception.ErrorCode.EXPIRE_CODE;
import static commerce.cms.user.exception.ErrorCode.NOT_FOUND_USER;
import static commerce.cms.user.exception.ErrorCode.WRONG_VERIFICATION;

import commerce.cms.user.domain.SignUpForm;
import commerce.cms.user.domain.model.Seller;
import commerce.cms.user.domain.repository.SellerRepository;
import commerce.cms.user.exception.CustomException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpSellerService {

  private final SellerRepository sellerRepository;

  /**
   * seller 회원가입
   */
  public Seller signUp(SignUpForm form) {
    return sellerRepository.save(Seller.from(form));
  }

  /**
   * 이메일 존재 유무
   */
  public boolean isEmailExist(String email) {
    return sellerRepository.existsByEmail(email);
  }

  /**
   * 이메일 검증
   */
  @Transactional
  public void verifyEmail(String email, String code) {
    Seller seller = sellerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

    if(seller.isVerify()) {
      throw new CustomException(ALREADY_VERIFY);
    }

    if(!seller.getVerificationCode().equals(code)) {
      throw new CustomException(WRONG_VERIFICATION);
    }

    if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
      throw new CustomException(EXPIRE_CODE);
    }
    seller.setVerify(true);
  }

  @Transactional
  public LocalDateTime changeSellerValidateEmail(Long sellerId, String verificationCode) {
    Optional<Seller> sellerOptional = sellerRepository.findById(sellerId);

    if(sellerOptional.isPresent()) {
      Seller seller = sellerOptional.get();
      seller.setVerificationCode(verificationCode);
      seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
      return seller.getVerifyExpiredAt();
    }
    throw new CustomException(NOT_FOUND_USER);
  }
}
