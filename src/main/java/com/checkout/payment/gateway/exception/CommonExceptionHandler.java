package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(ex.getMessage()),
        HttpStatus.BAD_REQUEST  // 400
    );
  }

  @ExceptionHandler(RestClientException.class)
  public ResponseEntity<ErrorResponse> handleRestClientException(RestClientException ex) {
    return new ResponseEntity<>(
        new ErrorResponse("Payment service temporarily unavailable"),
        HttpStatus.SERVICE_UNAVAILABLE  // 503
    );
  }

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleEventProcessingException(EventProcessingException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR  // 500
    );
  }
}
