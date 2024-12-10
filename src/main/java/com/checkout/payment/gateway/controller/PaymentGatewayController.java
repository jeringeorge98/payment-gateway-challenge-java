package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

@RestController("api")
@Tag(name ="Payment Gateway",description="APIs for processing and managing payments")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  @Operation(
      summary = "Retrieve payment details",
      description = "Get the details of a previously processed payment"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Payment found"),
      @ApiResponse(responseCode = "404", description = "Payment not found")
  })
  @GetMapping("/payment/{id}")
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }

  @PostMapping("/payment")
  public ResponseEntity<PostPaymentResponse> postPaymentEvent(@Valid @RequestBody
  PostPaymentRequest payload) {
    try {
      PostPaymentResponse response = paymentGatewayService.processPayment(payload);
      return switch (response.getStatus()) {
        case AUTHORIZED -> ResponseEntity.status(HttpStatus.CREATED).body(response);
        case DECLINED -> ResponseEntity.status(HttpStatus.OK).body(response);
        case REJECTED -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

      };

//    return new ResponseEntity<>(paymentGatewayService.processPayment(payload),HttpStatus.OK);
    } catch (RestClientException e) {
//      LOG.error("Bank service error: {}", e.getMessage());
      throw new EventProcessingException("Bank service unavailable");
    } catch (ValidationException e) {
      throw e;
    } catch (Exception e) {
      throw new EventProcessingException("Internal Servor error");
    }

  }

}
