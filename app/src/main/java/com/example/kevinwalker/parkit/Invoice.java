package com.example.kevinwalker.parkit;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by kevinwalker on 3/19/18.
 */

public class Invoice {

    private Float rate;
    private TimeUnit period;
    private PaymentType[] acceptedPaymentTypes;
    private Date billed;
    private Date received;
    private PaymentType paymentTypeReceived;
    private UUID invoiceUUID;
}
