package es.fernandopal.aforoqr.api.security;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping
public class SessionController {

    private SecurityService securityService;

    @GetMapping("session/me")
    @Operation(description = "Returns info about the logged in user")
    public AppUser getUser() {
        return securityService.getUser();
    }

    @GetMapping("session/roles")
    @Operation(description = "Returns the roles of the user")
    public Set<String> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @GetMapping("error")
    @Operation(description = "Internal SpringBoot error mapping")
    public ResponseEntity<HttpStatus> error() {
        return ResponseEntity.internalServerError().build();
    }
}