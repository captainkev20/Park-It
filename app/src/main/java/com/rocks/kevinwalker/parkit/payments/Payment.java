package com.rocks.kevinwalker.parkit.payments;

import com.stripe.android.model.Customer;

public class Payment {

    private String token = "";
    private Customer customer;
    private String cardLastFourDigits;
    private String cardBrand;
    private String paymentUserUUID;

    public Payment() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getCardLastFourDigits() { return cardLastFourDigits; }

    public void setCardLastFourDigits(String cardLastFourDigits) { this.cardLastFourDigits = cardLastFourDigits; }

    public String getCardBrand() { return cardBrand; }

    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }

    public String getPaymentUserUUID() { return paymentUserUUID; }

    public void setPaymentUserUUID(String paymentUserUUID) { this.paymentUserUUID = paymentUserUUID; }

}
