package com.rocks.kevinwalker.parkit.payments;

import com.stripe.android.model.Customer;

public class StripeCustomer {

    private String token = "";
    private Customer customer;
    private String cardLastFourDigits;
    private String cardBrand;

    public StripeCustomer() {}

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

}
