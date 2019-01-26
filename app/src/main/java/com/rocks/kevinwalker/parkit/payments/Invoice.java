package com.rocks.kevinwalker.parkit.payments;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;



public class Invoice {

    private Float rate;
    private TimeUnit period;
    private PaymentType[] acceptedPaymentTypes;
    private Date billed;
    private Date received;
    private PaymentType paymentTypeReceived;
    private UUID invoiceUUID;

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public TimeUnit getPeriod() {
        return period;
    }

    public void setPeriod(TimeUnit period) {
        this.period = period;
    }

    public PaymentType[] getAcceptedPaymentTypes() {
        return acceptedPaymentTypes;
    }

    public void setAcceptedPaymentTypes(PaymentType[] acceptedPaymentTypes) {
        this.acceptedPaymentTypes = acceptedPaymentTypes;
    }

    public Date getBilled() {
        return billed;
    }

    public void setBilled(Date billed) {
        this.billed = billed;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public PaymentType getPaymentTypeReceived() {
        return paymentTypeReceived;
    }

    public void setPaymentTypeReceived(PaymentType paymentTypeReceived) {
        this.paymentTypeReceived = paymentTypeReceived;
    }

    public UUID getInvoiceUUID() {
        return invoiceUUID;
    }

    public void setInvoiceUUID(UUID invoiceUUID) {
        this.invoiceUUID = invoiceUUID;
    }
}


