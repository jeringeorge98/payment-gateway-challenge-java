package com.checkout.payment.gateway.utils;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

@Component
public class PaymentValidator {
  // Supported currency codes (limiting to 3 as per requirement)
  private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "GBP", "EUR");

  public void validatePaymentRequest(PostPaymentRequest request) {
    validateCardNumber(request.getCardNumber());
    validateExpiryDate(request.getExpiryMonth(), request.getExpiryYear());
    validateCurrency(request.getCurrency());
    validateAmount(request.getAmount());
    validateCVV(request.getCvv());
  }

  private void validateCardNumber(String cardNumber) {

    if (cardNumber == null || cardNumber.isEmpty()) {
      throw new ValidationException("Card number is required");
    }
    if (!cardNumber.matches("^\\d{14,19}$")) {
      throw new ValidationException("Card number must be between 14-19 digits and contain only numbers");
    }

  }

  private void validateExpiryDate(int month, int year) {
    // Validate month
    if (month < 1 || month > 12) {
      throw new ValidationException("Expiry month must be between 1 and 12");
    }

    // Validate year and future date
    YearMonth expiryDate = YearMonth.of(year, month);
    YearMonth currentDate = YearMonth.from(LocalDate.now());

    if (expiryDate.isBefore(currentDate)) {
      throw new ValidationException("Card has expired or expiry date is invalid");
    }
  }

  private void validateCurrency(String currency) {
    if (currency == null || currency.isEmpty()) {
      throw new ValidationException("Currency is required");
    }
    if (currency.length() != 3) {
      throw new ValidationException("Currency code must be 3 characters");
    }
    if (!SUPPORTED_CURRENCIES.contains(currency.toUpperCase())) {
      throw new ValidationException("Currency not supported. Supported currencies are: " +
          String.join(", ", SUPPORTED_CURRENCIES));
    }
  }

  private void validateAmount(long amount) {
    if (amount <= 0) {
      throw new ValidationException("Amount must be greater than 0");
    }
    // Additional amount validations could be added here
    // e.g., maximum amount limits
  }

  private void validateCVV(String cvv) {
    if (cvv == null || cvv.isEmpty()) {
      throw new ValidationException("CVV is required");
    }
    if (!cvv.matches("^\\d{3,4}$")) {
      throw new ValidationException("CVV must be 3-4 digits and contain only numbers");
    }
  }
}