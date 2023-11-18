package com.service;

import com.paypal.api.payments.*;
import com.paypal.api.payments.Currency;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String clientMode;

    @Autowired
    private APIContext apiContext;

    public static final String SUCCESS_URL = "https://developer.paypal.com/tools/sandbox/";
    public static final String CANCEL_URL = "https://developer.paypal.com/tools/sandbox/";
    private static final String INTENT = "sale";
    private static final String METHOD = "paypal";

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", clientMode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }

    public Payment createPayment(
            Double total,
            String description) throws PayPalRESTException {
        Amount amount = new Amount();
        total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(METHOD);

        Payment payment = new Payment();
        payment.setIntent(INTENT);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(CANCEL_URL);
        redirectUrls.setReturnUrl(SUCCESS_URL);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    public PayoutBatch payout(double total, String currency, String receiverEmail) throws PayPalRESTException {
        Date currentDate = new Date(System.currentTimeMillis());
        PayoutSenderBatchHeader payoutSenderBatchHeader = new PayoutSenderBatchHeader();
        List<PayoutItem> payoutItems = new ArrayList<>();

        payoutItems.add(new PayoutItem(new Currency(currency, String.format("%.2f", total)), receiverEmail));
        Payout payout = new Payout();

        payout.setSenderBatchHeader(payoutSenderBatchHeader);
        payout.setItems(payoutItems);

        return payout.create(apiContext, null);
    }
}