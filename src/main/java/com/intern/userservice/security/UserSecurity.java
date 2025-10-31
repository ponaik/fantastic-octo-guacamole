package com.intern.userservice.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class UserSecurity implements AuthorizationManager<RequestAuthorizationContext> {

    public boolean hasUserId(Authentication authentication, Long userId) {
//        authentication.getPrincipal();
        return true;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        return null; //deprecated
    }

    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        // get {userId} from the request
        Long userId = Long.parseLong(ctx.getVariables().get("userId"));

        Authentication authentication = (Authentication) authenticationSupplier.get();
        return new AuthorizationDecision(hasUserId(authentication, userId));
    }
}