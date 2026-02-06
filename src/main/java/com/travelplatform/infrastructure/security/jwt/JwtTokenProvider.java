package com.travelplatform.infrastructure.security.jwt;

import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * JWT Token Provider for generating and validating JWT tokens.
 * This class handles token creation for authentication and authorization.
 */
@ApplicationScoped
public class JwtTokenProvider {

    @Inject
    @ConfigProperty(name = "jwt.secret")
    String jwtSecret;

    @Inject
    @ConfigProperty(name = "jwt.expiration")
    long jwtExpirationMs;

    @Inject
    @ConfigProperty(name = "jwt.refresh-expiration")
    long jwtRefreshExpirationMs;

    @Inject
    @ConfigProperty(name = "jwt.issuer")
    String jwtIssuer;

    @Inject
    JWTParser jwtParser;

    private static final String TYPE_CLAIM = "type";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";
    private static final String STATUS_CLAIM = "status";

    /**
     * Generate JWT access token for a user.
     *
     * @param userId User ID
     * @param email  User email
     * @param role   User role
     * @param status User status
     * @return JWT token string
     */
    public String generateAccessToken(UUID userId, String email, UserRole role, UserStatus status) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);

        JwtClaimsBuilder claimsBuilder = Jwt.claims()
                .subject(userId.toString())
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("email", email)
                .claim("role", role.name())
                .claim("status", status.name())
                .claim(TYPE_CLAIM, "access");

        // Add role-based groups for authorization
        Set<String> groups = new HashSet<>();
        groups.add(role.name());
        if (status == UserStatus.ACTIVE) {
            groups.add("ACTIVE");
        }
        claimsBuilder.groups(groups);

        return claimsBuilder
                .jws()
                .keyId(jwtSecret)
                .sign(jwtSecret);
    }

    /**
     * Generate JWT refresh token for a user.
     *
     * @param userId User ID
     * @return JWT refresh token string
     */
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtRefreshExpirationMs, ChronoUnit.MILLIS);

        return Jwt.claims()
                .subject(userId.toString())
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(expiration)
                .claim(TYPE_CLAIM, "refresh")
                .jws()
                .keyId(jwtSecret)
                .sign(jwtSecret);
    }

    /**
     * Generate JWT token with custom expiration time.
     *
     * @param userId       User ID
     * @param email        User email
     * @param role         User role
     * @param status       User status
     * @param expirationMs Custom expiration time in milliseconds
     * @return JWT token string
     */
    public String generateTokenWithCustomExpiration(UUID userId, String email, UserRole role, UserStatus status,
            long expirationMs) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationMs, ChronoUnit.MILLIS);

        JwtClaimsBuilder claimsBuilder = Jwt.claims()
                .subject(userId.toString())
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("email", email)
                .claim("role", role.name())
                .claim("status", status.name());

        Set<String> groups = new HashSet<>();
        groups.add(role.name());
        if (status == UserStatus.ACTIVE) {
            groups.add("ACTIVE");
        }
        claimsBuilder.groups(groups);

        return claimsBuilder
                .jws()
                .keyId(jwtSecret)
                .sign(jwtSecret);
    }

    /**
     * Get token expiration date from token string.
     *
     * @param token JWT token string
     * @return Expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return parse(token)
                .map(JsonWebToken::getExpirationTime)
                .map(exp -> Date.from(Instant.ofEpochSecond(exp)))
                .orElse(null);
    }

    /**
     * Get user ID from token string.
     *
     * @param token JWT token string
     * @return User ID
     */
    public UUID getUserIdFromToken(String token) {
        return parse(token)
                .map(JsonWebToken::getSubject)
                .map(UUID::fromString)
                .orElse(null);
    }

    /**
     * Get user role from token string.
     *
     * @param token JWT token string
     * @return User role
     */
    public UserRole getRoleFromToken(String token) {
        return parse(token)
                .map(jwt -> jwt.getClaim(ROLE_CLAIM))
                .map(Object::toString)
                .map(UserRole::valueOf)
                .orElse(null);
    }

    /**
     * Get user email from token string.
     *
     * @param token JWT token string
     * @return User email
     */
    public String getEmailFromToken(String token) {
        return parse(token)
                .map(jwt -> jwt.getClaim(EMAIL_CLAIM))
                .map(Object::toString)
                .orElse(null);
    }

    /**
     * Check if token is expired.
     *
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }

    /**
     * Validate token.
     *
     * @param token JWT token string
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            return parse(token).isPresent() && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get token type (access or refresh).
     *
     * @param token JWT token string
     * @return Token type
     */
    public String getTokenType(String token) {
        return parse(token)
                .map(jwt -> jwt.getClaim(TYPE_CLAIM))
                .map(Object::toString)
                .orElse(null);
    }

    /**
     * Check if token is a refresh token.
     *
     * @param token JWT token string
     * @return true if refresh token, false otherwise
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * Check if token is an access token.
     *
     * @param token JWT token string
     * @return true if access token, false otherwise
     */
    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    private Optional<JsonWebToken> parse(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        String normalized = token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;
        try {
            return Optional.of(jwtParser.parse(normalized));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
