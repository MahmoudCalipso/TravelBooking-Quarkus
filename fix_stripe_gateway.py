import sys

path = r'd:\Traveling-Project\backend-Travel-booking\backend\src\main\java\com\travelplatform\infrastructure\payment\StripePaymentGateway.java'

with open(path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

transfer_method = """
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
    }
"""

connect_methods = """
    @Override
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
"""

# Insert transfer method after line 86 (index 86 since 1-indexed)
lines.insert(86, transfer_method)

# Remove last line (closing brace) and append connect methods + closing brace
if lines[-1].strip() == "}":
    lines[-1] = connect_methods + "}\n"
else:
    # If there are blank lines at the end
    idx = len(lines) - 1
    while idx >= 0 and lines[idx].strip() != "}":
        idx -= 1
    if idx >= 0:
        lines[idx] = connect_methods + "}\n"

with open(path, 'w', encoding='utf-8', newline='\n') as f:
    f.writelines(lines)

print("Successfully updated StripePaymentGateway.java")
