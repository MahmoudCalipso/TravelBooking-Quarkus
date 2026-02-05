package com.travelplatform.application.dto.request.trust;

import java.time.LocalDate;

public class FraudCheckRequest {
    private LocalDate accountCreatedAt;
    private boolean emailVerified;
    private boolean phoneVerified;
    private int bookingsLast24h;
    private double bookingAmount;
    private double averageUserAmount;
    private boolean vpnDetected;

    public LocalDate getAccountCreatedAt() {
        return accountCreatedAt;
    }

    public void setAccountCreatedAt(LocalDate accountCreatedAt) {
        this.accountCreatedAt = accountCreatedAt;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public int getBookingsLast24h() {
        return bookingsLast24h;
    }

    public void setBookingsLast24h(int bookingsLast24h) {
        this.bookingsLast24h = bookingsLast24h;
    }

    public double getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(double bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    public double getAverageUserAmount() {
        return averageUserAmount;
    }

    public void setAverageUserAmount(double averageUserAmount) {
        this.averageUserAmount = averageUserAmount;
    }

    public boolean isVpnDetected() {
        return vpnDetected;
    }

    public void setVpnDetected(boolean vpnDetected) {
        this.vpnDetected = vpnDetected;
    }
}
