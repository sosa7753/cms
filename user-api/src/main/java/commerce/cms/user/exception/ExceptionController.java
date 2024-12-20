package commerce.cms.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // 컨트롤러에서 발생한 예외 처리
@Slf4j
public class ExceptionController {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ExceptionResponse> customRequestException(final CustomException e) {
    log.warn("api Exception : {}", e.getErrorCode());
    return ResponseEntity.badRequest().body(new ExceptionResponse(e.getMessage(), e.getErrorCode()));
  }

  @Getter
  @AllArgsConstructor
  public static class ExceptionResponse {
    private String message;
    private ErrorCode errorCode;
  }

}
