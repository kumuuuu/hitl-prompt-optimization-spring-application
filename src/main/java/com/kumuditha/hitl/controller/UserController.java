package com.kumuditha.hitl.controller;

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

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
                provider
        );

        return new UserMeResponse(user);
    }

    // Helper: safely coerce an object to Map<String,Object>, never null
    @SuppressWarnings("unchecked")
    private static Map<String, Object> safeMap(Object o) {
        if (o instanceof Map) {
            return (Map<String, Object>) o;
        }
        return Map.of();
    }

    // Helper: safely coerce to String (null -> null)
    private static String safeString(Object o) {
        return o == null ? null : o.toString();
    }

}
