package commerce.cms.user.domain.repository;

import commerce.cms.user.domain.model.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

  Optional<Seller> findByIdAndEmail(Long id, String email);

  Optional<Seller> findByEmailAndPasswordAndVerifyIsTrue(String email, String password);

  boolean existsByEmail(String email);

  Optional<Seller> findByEmail(String email);
}

