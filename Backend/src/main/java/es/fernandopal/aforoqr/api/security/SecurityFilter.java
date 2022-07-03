package es.fernandopal.aforoqr.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.appuser.AppUserService;
import es.fernandopal.aforoqr.api.exception.ApiInternalServerErrorException;
import es.fernandopal.aforoqr.api.exception.ApiUnauthorizedException;
import es.fernandopal.aforoqr.api.exception.handle.ApiException;
import es.fernandopal.aforoqr.api.properties.CustomProperties;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
    private final SecurityService securityService;
//    private final RoleService securityRoleService;
    private final CustomProperties securityProps;
    private final AppUserService appUserService;
    private final FirebaseAuth firebaseAuth;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info(request.getMethod() + " " + request.getRequestURI());

        try {
            authorize(request);
            filterChain.doFilter(request, response);
        } catch(ApiUnauthorizedException ex) {
            throwError(response, new ApiException(ex.getMessage(), HttpStatus.UNAUTHORIZED, ZonedDateTime.now(ZoneId.of("Z"))));
        } catch (ApiInternalServerErrorException ex) {
            throwError(response, new ApiException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, ZonedDateTime.now(ZoneId.of("Z"))));
        }
    }

    private void authorize(HttpServletRequest request) {
        SecurityContextHolder.getContext().setAuthentication(null);

        if (request.getHeader("Authorization") != null) {
            final String token = securityService.getBearerToken(request);
            FirebaseToken decodedToken;

            if (token == null) {
                throw new ApiUnauthorizedException("Invalid Authorization");
            }

            try {
                decodedToken = firebaseAuth.verifyIdToken(token);
            } catch (FirebaseAuthException e) {
                throw new ApiUnauthorizedException("Invalid Authorization");
            }

            final List<GrantedAuthority> authorities = new ArrayList<>();
            final AppUser appUser = firebaseTokenToUserDto(decodedToken);

            // Asignación de roles
            if (appUser != null && decodedToken != null) {
                // SecurityContext temporal para poder trabajar con los roles
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(appUser, new Credentials(decodedToken, token), authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Admins, solo se pueden dar por config
                if (!appUser.isAdmin() && securityProps.getAdmins().contains(appUser.getEmail())) {
//                    if (!decodedToken.getClaims().containsKey(Role.ADMIN.name())) {
//                        try {
//                            securityRoleService.addRole(decodedToken.getUid(), Role.ADMIN.name(), true);
                            appUser.setAdmin(true);
//                            LOGGER.warn("Role " + Role.ADMIN.name() + " was assigned to the user " + decodedToken.getUid());
//                        } catch (Exception e) {
//                            LOGGER.error("Error giving role: ", e);
//                        }
//                    }
//                    authorities.add(new SimpleGrantedAuthority(Role.ADMIN.name()));
                }

                // Otros roles
                decodedToken.getClaims().forEach((k, v) -> authorities.add(new SimpleGrantedAuthority(k)));

                // SecurityContext final
                authentication = new UsernamePasswordAuthenticationToken(appUser, new Credentials(decodedToken, token), authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Guardamos el usuario en la db
                saveUser(appUser);
            }
        }
    }

    private void saveUser(AppUser appUser) {
        if(appUser == null) {
            throw new ApiInternalServerErrorException("Unknown error when trying to register an user, this account will probably need to be fixed");
        }

        if(!appUserService.exists(appUser)) {
            appUserService.save(appUser, true);
        }
    }

    // Los filtros de spring se ejecutan antes que los ExceptionHandlers así que para lanzar excepciones desde
    // un filtro debemos crear la respuesta manualmente
    protected void throwError(HttpServletResponse response, ApiException exception) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(exception.getHttpStatus().value());

        try (OutputStream out = response.getOutputStream()) {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            out.write(mapper.writeValueAsBytes(exception));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private AppUser firebaseTokenToUserDto(FirebaseToken decodedToken) {
        AppUser user = null;
        if (decodedToken != null) {
            user = new AppUser();
            user.setUid(decodedToken.getUid());
            user.setName(decodedToken.getName());
            user.setEmail(decodedToken.getEmail());
            user.setPicture(decodedToken.getPicture());
            user.setIssuer(decodedToken.getIssuer());
            user.setEmailVerified(decodedToken.isEmailVerified());
        }
        return user;
    }
}