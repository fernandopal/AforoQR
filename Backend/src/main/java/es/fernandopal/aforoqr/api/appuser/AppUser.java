package es.fernandopal.aforoqr.api.appuser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class AppUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 4408418647685225829L;

    @Id
    private String uid;
    private String name;
    private String email;
    private boolean isEmailVerified;
    private boolean isAdmin;
    private String issuer;
    private String picture;
    private Integer reputation;
    @ColumnDefault("false")
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppUser appUser = (AppUser) o;
        return uid != null && Objects.equals(uid, appUser.uid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}