package commerce.cms.order.exception;

import commerce.cms.order.exception.CustomException.CustomExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionAdvice {
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<CustomException.CustomExceptionResponse> exceptionHandler(CustomException e) {
    return ResponseEntity
        .status(e.getStatus())
        .body(CustomExceptionResponse.builder()
            .message(e.getMessage())
            .code(e.getErrorCode().name())
            .status(e.getStatus())
            .build());
  }
}
