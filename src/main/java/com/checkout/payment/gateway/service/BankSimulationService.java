package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.BankRequest;
import com.checkout.payment.gateway.model.BankResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class BankSimulationService {

  private static final Logger LOG = LoggerFactory.getLogger(BankSimulationService.class);
  private final RestTemplate restTemplate;
  private final String bankSimulatorUrl = "http://localhost:8080";


  public BankSimulationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public BankResponse processPayment(BankRequest request) {
    try {
      LOG.info("Sending payment request to bank simulator");

      LOG.debug("Request URL: {}", bankSimulatorUrl + "/payments");
      LOG.debug("Request Body: {}", new ObjectMapper().writeValueAsString(request));
      ResponseEntity<BankResponse> response = restTemplate.postForEntity(
          bankSimulatorUrl + "/payments",
          request,
          BankResponse.class
      );

      LOG.info("Received response from bank simulator: {}", response.getBody());
      return response.getBody();

    } catch (RestClientException e) {
      LOG.error("Error calling bank simulator", e);
      throw e;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public BankRequest createBankRequest(PostPaymentRequest paymentRequest) {
    BankRequest bankRequest = new BankRequest();
    bankRequest.setAmount(paymentRequest.getAmount());
    bankRequest.setCardNumber(paymentRequest.getCardNumber());
    bankRequest.setCurrency(paymentRequest.getCurrency());
    bankRequest.setCvv(paymentRequest.getCvv());
    bankRequest.setExpiryDate(paymentRequest.getExpiryDate());
    return bankRequest;
  }
}
