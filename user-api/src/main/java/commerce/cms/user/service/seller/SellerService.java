package commerce.cms.user.service.seller;

import commerce.cms.user.domain.model.Seller;
import commerce.cms.user.domain.repository.SellerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

  private final SellerRepository sellerRepository;

  public Optional<Seller> findByIdAndEmail(Long id, String email) {
    return sellerRepository.findByIdAndEmail(id, email);
  }

  public Optional<Seller> findValidSeller(String email, String password) {
    return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);
  }

}
