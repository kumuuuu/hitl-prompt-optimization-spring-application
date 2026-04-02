package com.kumuditha.hitl.controller;

/*
 * File: UserController.java
 *
 * Description:
 * REST controller for user-related endpoints (identity debug and "current user").
 *
 * Responsibilities:
 * - Exposes an authenticated "who am I" endpoint that maps JWT claims to an app User.
 * - Provides a debug endpoint for inspecting authentication during development.
 *
 * Used in:
 * - Frontend initialization to fetch the current user profile.
 */

import com.kumuditha.hitl.dto.UserMeResponse;
import com.kumuditha.hitl.entity.User;
import com.kumuditha.hitl.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Creates a controller with required service dependencies.
     *
     * @param userService user lookup/creation based on identity provider claims
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Development-only endpoint that returns the current JWT as seen by Spring
     * Security.
     *
     * <p>
     * This is useful for verifying that authentication is wired correctly and that
     * required claims are present.
     * </p>
     *
     * @param jwt JWT principal injected by Spring Security (may be null if
     *            unauthenticated)
     * @return the JWT object (or null)
     */
    @GetMapping("/debug")
    public Object debug(@AuthenticationPrincipal Jwt jwt) {
        logger.debug("/api/debug called; jwt present={}", jwt != null);
        if (jwt != null) {
            try {
                logger.debug("JWT subject={}, email={}", jwt.getSubject(), jwt.getClaimAsString("email"));
            } catch (Exception e) {
                logger.debug("Failed to read some JWT claims: {}", e.getMessage());
            }
        }
        return jwt;
    }

    /**
     * Returns the current application user derived from the authenticated
     * principal.
     *
     * <p>
     * The principal may be provided as a {@link Jwt} or as a map of claims
     * depending on
     * the Spring Security configuration and converters in use.
     * </p>
     *
     * @param authentication Spring Security authentication (must be present)
     * @return a normalized user response for the client
     * @throws ResponseStatusException if the caller is unauthenticated or uses an
     *                                 unsupported principal type
     */
    @GetMapping("/me")
    public UserMeResponse me(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            logger.warn("/api/me called but no authentication principal present");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Object principal = authentication.getPrincipal();
        Map<String, Object> claims;

        if (principal instanceof Jwt) {
            claims = ((Jwt) principal).getClaims();
        } else if (principal instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> p = (Map<String, Object>) principal;
            claims = p;
        } else {
            logger.warn("Unsupported principal type: {}", principal.getClass().getName());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unsupported principal type");
        }

        String supabaseUserId = safeString(claims.get("sub"));
        String email = safeString(claims.get("email"));

        Map<String, Object> userMeta = safeMap(claims.get("user_metadata"));
        String name = safeString(userMeta.get("full_name"));
        String avatarUrl = safeString(userMeta.get("avatar_url"));

        Map<String, Object> appMeta = safeMap(claims.get("app_metadata"));
        String provider = safeString(appMeta.get("provider"));

        User user = userService.findOrCreateUser(
                supabaseUserId,
                email,
                name,
                avatarUrl,
                provider);

        return new UserMeResponse(user);
    }

    /**
     * Safely coerces a value to a {@code Map<String, Object>}.
     *
     * @param o value to coerce
     * @return a map if {@code o} is a map; otherwise an empty map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> safeMap(Object o) {
        if (o instanceof Map) {
            return (Map<String, Object>) o;
        }
        return Map.of();
    }

    /**
     * Safely coerces a value to String.
     *
     * @param o value to coerce
     * @return {@code null} if {@code o} is null, otherwise {@code o.toString()}
     */
    private static String safeString(Object o) {
        return o == null ? null : o.toString();
    }

}
