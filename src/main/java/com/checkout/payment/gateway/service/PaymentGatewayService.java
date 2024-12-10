package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankRequest;
import com.checkout.payment.gateway.model.BankResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.checkout.payment.gateway.utils.PaymentValidator;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final PaymentValidator validator;
  private final BankSimulationService bankService;

  public PaymentGatewayService(PaymentsRepository paymentsRepository,PaymentValidator validator,BankSimulationService bankService) {
    this.paymentsRepository = paymentsRepository;
    this.validator  = validator;
    this.bankService = bankService;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest)
  {
    // 1. Validate
    //2 . Call the bank simulator
    //3. if authorized then send a sucess payment back
    try{
      LOG.debug("Processing payment");

      validator.validatePaymentRequest(paymentRequest);
      BankRequest bankRequest = bankService.createBankRequest(paymentRequest);
      BankResponse response = bankService.processPayment(bankRequest);
      // Set common fields
      PostPaymentResponse paymentResponse = mapToPaymentResponse(paymentRequest);

      // Set status based on bank response
      if (response.getAuthorized()) {
        paymentResponse.setStatus(PaymentStatus.AUTHORIZED);
      } else {
        paymentResponse.setStatus(PaymentStatus.DECLINED);
      }
      paymentsRepository.add(paymentResponse);
      return paymentResponse;

    }
    catch (ValidationException e){
      LOG.error("Validation failed: {}", e.getMessage());
      PostPaymentResponse paymentResponse =  new PostPaymentResponse();
      paymentResponse.setStatus(PaymentStatus.REJECTED);
      return paymentResponse;
    }
    catch (EventProcessingException e){
      LOG.error("Unexpected error: {}", e.getMessage());
      throw new EventProcessingException("Something went wrong");
    }

  }

  private PostPaymentResponse mapToPaymentResponse(PostPaymentRequest paymentRequest) {
    PostPaymentResponse paymentResponse = new PostPaymentResponse();
    paymentResponse.setId(UUID.randomUUID());
    paymentResponse.setCardNumberLastFour(extractLastFourDigits(paymentRequest.getCardNumber()));
    paymentResponse.setExpiryMonth(paymentRequest.getExpiryMonth());
    paymentResponse.setExpiryYear(paymentRequest.getExpiryYear());
    paymentResponse.setCurrency(paymentRequest.getCurrency());
    paymentResponse.setAmount(paymentRequest.getAmount());
    return paymentResponse;
  }

  private String extractLastFourDigits(String cardNumber) {
    String lastFour = cardNumber.substring(cardNumber.length() - 4);
    return lastFour;
  }


}
