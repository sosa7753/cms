package commerce.cms.order.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;
  private final int status;
  private static final ObjectMapper mapper = new ObjectMapper();

  public CustomException(ErrorCode errorcode) {
    super(errorcode.getMessage());
    this.errorCode = errorcode;
    this.status = errorcode.getHttpStatus().value();
  }



}
