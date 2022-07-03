package es.fernandopal.aforoqr.api.appuser;

import es.fernandopal.aforoqr.api.security.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final SecurityService securityService;

    public void save(AppUser appUser, boolean systemActionBypass) {
        if(!systemActionBypass) {
            securityService.preventAccess(!securityService.getUser().isAdmin());
        }

        appUserRepository.save(appUser);
    }

    public List<AppUser> getAll() {
        securityService.preventAccess(!securityService.getUser().isAdmin());

        return appUserRepository.findAll();
    }

    public boolean exists(AppUser appUser) {
        return appUserRepository.findAppUserByUid(appUser.getUid()).isPresent();
    }

    public Optional<AppUser> getAppUser(String uid) {
//        securityService.preventAccess(!securityService.isAdmin() || !securityService.getUser().getUid().equals(uid), "AppUserService.getAppUser");

        return appUserRepository.findAppUserByUid(uid);
    }

    public Integer getUserCount() {
        return appUserRepository.getTotalUsers();
    }
}
