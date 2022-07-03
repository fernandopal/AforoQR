package es.fernandopal.aforoqr.api.appuser;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/user")
public class AppUserController {

    private final AppUserService appUserService;

    // fpalomo - Returns a list of all AppUsers
    @GetMapping()
    @Operation(description = "Returns a list of all the stored AppUser records in the database")
    public ResponseEntity<List<AppUser>> getAll() {
        List<AppUser> appUsers = appUserService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(appUsers);
    }

    // fpalomo - Get an AppUser by its id
    @GetMapping(value = "/{uid}")
    @Operation(description = "Returns all the data stored in the database about an AppUser")
    public ResponseEntity<AppUser> getById(@PathVariable("uid") String uid) {
        AppUser appUser = appUserService.getAppUser(uid).orElse(null);
        HttpStatus responseStatus = (appUser == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return ResponseEntity.status(responseStatus).body(appUser);
    }

    // fpalomo - Update an AppUser
    @PostMapping(value = "/update")
    @Operation(description = "Update an AppUser")
    public ResponseEntity<String> update(@RequestBody AppUserRequest appUserRequest) {
        AppUser appUser = appUserService.getAppUser(appUserRequest.getUid()).orElse(null);
        HttpStatus responseStatus = (appUser == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        if (appUser != null) {
            appUser.setAdmin(appUserRequest.isAdmin());
            appUser.setName(appUserRequest.getName());
            appUserService.save(appUser, false);
        }

        assert appUser != null;
        return ResponseEntity.status(responseStatus).body(appUser.getUid());
    }
}
