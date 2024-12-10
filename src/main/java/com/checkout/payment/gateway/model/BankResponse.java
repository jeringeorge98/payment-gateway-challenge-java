package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankResponse {

  @JsonProperty("authorization_code")
  private String mAuthorizationCode;
  @JsonProperty("authorized")
  private Boolean mAuthorized;

  public String getAuthorizationCode() {
    return mAuthorizationCode;
  }

  public Boolean getAuthorized() {
    return mAuthorized;
  }

  public void setmAuthorizationCode(String mAuthorizationCode) {this.mAuthorizationCode = mAuthorizationCode;}

  public  void setmAuthorized(Boolean mAuthorized) {this.mAuthorized = mAuthorized;}

}
