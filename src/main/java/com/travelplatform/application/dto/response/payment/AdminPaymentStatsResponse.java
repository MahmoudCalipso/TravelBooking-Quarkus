package com.travelplatform.application.dto.response.payment;

import java.math.BigDecimal;
import java.util.Map;

public class AdminPaymentStatsResponse {
    public long totalTransactions;
    public long successfulTransactions;
    public long completedTransactions;
    public long failedTransactions;
    public long refundedTransactions;
    public Map<String, BigDecimal> totalRevenueByCurrency;
    public Map<String, BigDecimal> averageAmountByCurrency;
    public Map<String, BigDecimal> refundedAmountByCurrency;
}
