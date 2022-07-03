package es.fernandopal.aforoqr.api.security;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.exception.ApiUnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Service
public class SecurityService {

    public AppUser getUser() {
        AppUser userPrincipal = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {
            userPrincipal = ((AppUser) principal);
        }
        return userPrincipal;
    }

    public void preventAccess(boolean preventAccess) {
        if (preventAccess) {
            throw new ApiUnauthorizedException("No tienes los permisos necesarios para acceder a este recurso. Si crees que se trata de un error puedes contactar con el administrador del sistema por medio del email mail@fpalomo.es");
        }
    }

    public Credentials getCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Credentials) authentication.getCredentials();
    }

    public String getBearerToken(HttpServletRequest request) {
        String bearerToken = null;
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            bearerToken = authorization.substring(7, authorization.length());
        }
        return bearerToken;
    }
}