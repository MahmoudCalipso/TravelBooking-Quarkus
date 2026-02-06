import sys
import os

def replace_in_file(file_path, start_marker, end_marker, replacement):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    start_index = content.find(start_marker)
    if start_index == -1:
        print(f"Start marker not found in {file_path}")
        return False
    
    end_index = content.find(end_marker, start_index + len(start_marker))
    if end_index == -1:
        print(f"End marker not found in {file_path}")
        return False
    
    # We want to replace from start of start_marker to end of end_marker
    new_content = content[:start_index] + replacement + content[end_index + len(end_marker):]
    
    with open(file_path, 'w', encoding='utf-8', newline='\n') as f:
        f.write(new_content)
    print(f"Successfully updated {file_path}")
    return True

stripe_gateway_path = r'd:\Traveling-Project\backend-Travel-booking\backend\src\main\java\com\travelplatform\infrastructure\payment\StripePaymentGateway.java'

# Replace createPaymentIntent block to include createPaymentIntentWithTransfer right after
create_pi_marker_end = '            throw new PaymentException(PaymentException.PAYMENT_FAILED, "Failed to create payment intent: " + e.getMessage(), e);\n        }\n    }'
create_pi_transfer = """        } catch (StripeException e) {
            throw new PaymentException(PaymentException.PAYMENT_FAILED, "Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentIntent createPaymentIntentWithTransfer(UUID bookingId, BigDecimal amount, String currency,
                                                        String paymentMethod, String description,
                                                        String destinationAccountId, BigDecimal appFeeAmount) throws PaymentException {
        initializeStripe();

        try {
            long amountInCents = amount.multiply(new BigDecimal("100")).longValue();
            long feeInCents = appFeeAmount.multiply(new BigDecimal("100")).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency.toLowerCase())
                    .setDescription(description)
                    .putAllMetadata(Map.of(
                            "booking_id", bookingId.toString(),
                            "payment_method", paymentMethod,
                            "destination_account", destinationAccountId
                    ))
                    .setTransferData(PaymentIntentCreateParams.TransferData.builder()
                            .setDestination(destinationAccountId)
                            .build())
                    .setApplicationFeeAmount(feeInCents)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            com.stripe.model.PaymentIntent stripeIntent = com.stripe.model.PaymentIntent.create(params);

            PaymentIntent intent = new PaymentIntent();
            intent.setId(stripeIntent.getId());
            intent.setClientSecret(stripeIntent.getClientSecret());
            intent.setStatus(stripeIntent.getStatus());
            intent.setAmount(new BigDecimal(stripeIntent.getAmount()).divide(new BigDecimal("100")));
            intent.setCurrency(stripeIntent.getCurrency().toUpperCase());
            intent.setPaymentMethod(stripeIntent.getPaymentMethod());
            intent.setDescription(stripeIntent.getDescription());
            intent.setCreatedAt(Instant.ofEpochSecond(stripeIntent.getCreated()));

            return intent;

        } catch (StripeException e) {
            throw new PaymentException(PAYMENT_FAILED, "Failed to create transfer payment intent: " + e.getMessage(), e);
        }
    }"""

replace_in_file(stripe_gateway_path, create_pi_marker_end, create_pi_marker_end, create_pi_transfer)

# Add Connect methods at the end of class before the final closing brace
connect_methods = """    @Override
    public String createConnectAccount(String email, String accountType) throws PaymentException {
        initializeStripe();

        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.valueOf(accountType.toUpperCase()))
                    .setEmail(email)
                    .setCapabilities(AccountCreateParams.Capabilities.builder()
                            .setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder()
                                    .setRequested(true)
                                    .build())
                            .setTransfers(AccountCreateParams.Capabilities.Transfers.builder()
                                    .setRequested(true)
                                    .build())
                            .build())
                    .build();

            Account account = Account.create(params);
            return account.getId();

        } catch (StripeException e) {
            throw new PaymentException(INVALID_REQUEST, "Failed to create Connect account: " + e.getMessage(), e);
        }
    }

    @Override
    public String createAccountLink(String accountId, String refreshUrl, String returnUrl) throws PaymentException {
        initializeStripe();

        try {
            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(accountId)
                    .setRefreshUrl(refreshUrl)
                    .setReturnUrl(returnUrl)
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink accountLink = AccountLink.create(params);
            return accountLink.getUrl();

        } catch (StripeException e) {
            throw new PaymentException(INVALID_REQUEST, "Failed to create account link: " + e.getMessage(), e);
        }
    }
}"""

# Final closing brace replacement
replace_in_file(stripe_gateway_path, "\n}", "\n}", connect_methods)
