package es.fernandopal.aforoqr.api.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findAppUserByUid(String uid);

    @Query("SELECT COUNT(appUser.uid) FROM AppUser appUser")
    Integer getTotalUsers();
}