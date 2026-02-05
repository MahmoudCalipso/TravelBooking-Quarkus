package com.travelplatform.interfaces.rest.controller;

import com.travelplatform.application.dto.request.trust.DisputeCreateRequest;
import com.travelplatform.application.dto.request.trust.DisputeResolveRequest;
import com.travelplatform.application.dto.request.trust.FraudCheckRequest;
import com.travelplatform.application.dto.request.trust.IdentityVerificationRequest;
import com.travelplatform.application.service.trust.DisputeService;
import com.travelplatform.application.service.trust.FraudDetectionService;
import com.travelplatform.application.service.trust.IdentityVerificationService;
import com.travelplatform.application.service.trust.IdentityVerificationService.VerificationDocuments;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/trust")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
public class TrustController {

    @Inject
    IdentityVerificationService identityVerificationService;

    @Inject
    FraudDetectionService fraudDetectionService;

    @Inject
    DisputeService disputeService;

    @POST
    @Path("/verify-identity")
    public Response verifyIdentity(IdentityVerificationRequest request) {
        var result = identityVerificationService.verifyIdentity(
                request.getUserId(),
                new VerificationDocuments(request.getDocumentType(), request.getDocumentUrl()));
        return Response.ok(result).build();
    }

    @POST
    @Path("/fraud-check")
    public Response fraudCheck(FraudCheckRequest request) {
        var score = fraudDetectionService.analyzeBooking(
                request.getAccountCreatedAt(),
                request.isEmailVerified(),
                request.isPhoneVerified(),
                request.getBookingsLast24h(),
                request.getBookingAmount(),
                request.getAverageUserAmount(),
                request.isVpnDetected());
        return Response.ok(score).build();
    }

    @POST
    @Path("/disputes")
    public Response createDispute(DisputeCreateRequest request) {
        return Response.ok(disputeService.createDispute(
                request.getBookingId(), request.getInitiatorId(), request.getReason())).build();
    }

    @POST
    @Path("/disputes/resolve")
    public Response resolveDispute(DisputeResolveRequest request) {
        return Response.ok(disputeService.mediateDispute(
                request.getDisputeId(), request.getAdminId(), request.getResolution())).build();
    }
}
