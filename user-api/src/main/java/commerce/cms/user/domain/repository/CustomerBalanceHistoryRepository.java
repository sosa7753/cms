package commerce.cms.user.domain.repository;

import commerce.cms.user.domain.model.CustomerBalanceHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface CustomerBalanceHistoryRepository extends JpaRepository<CustomerBalanceHistory, Long> {

  Optional<CustomerBalanceHistory>
  findFirstByCustomer_IdOrderByIdDesc(@RequestParam("customerId") Long customerId);

}
