package commerce.cms.user.service.customer;

import commerce.cms.user.domain.customer.ChangeBalanceForm;
import commerce.cms.user.domain.model.CustomerBalanceHistory;
import commerce.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import commerce.cms.user.domain.repository.CustomerRepository;
import commerce.cms.user.exception.CustomException;
import commerce.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {

  private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
  private final CustomerRepository customerRepository;

  @Transactional(noRollbackFor = {CustomException.class}) // 해당 exception 발생시 수행된 부분까지 커밋
  public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form)
      throws CustomException {

    CustomerBalanceHistory customerBalanceHistory = customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(
        customerId).orElse(CustomerBalanceHistory.builder()
        .changeMoney(0)
        .currentMoney(0)
        .customer(customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)))
        .build());

    if(customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0) { // - 잔액
      throw new CustomException(ErrorCode.NO_ENOUGH_BALANCE);
    }

    customerBalanceHistory = CustomerBalanceHistory.builder()
        .changeMoney(form.getMoney())
        .currentMoney(customerBalanceHistory.getCurrentMoney() + form.getMoney())
        .description(form.getMessage())
        .fromMessage(form.getFrom())
        .customer(customerBalanceHistory.getCustomer())
        .build();

    customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());
    return customerBalanceHistoryRepository.save(customerBalanceHistory);
  }
}
