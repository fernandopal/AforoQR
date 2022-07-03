package es.fernandopal.aforoqr.api.appuser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppUserRequest {
    private String uid;
    private String name;
    private String email;
    private boolean isEmailVerified;
    private boolean admin;
    private String issuer;
    private String picture;
    private Integer reputation;
    private boolean active;

    @Override
    public String toString() {
        return "AppUserSequest{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isEmailVerified=" + isEmailVerified +
                ", isAdmin=" + admin +
                ", issuer='" + issuer + '\'' +
                ", picture='" + picture + '\'' +
                ", reputation=" + reputation +
                ", active=" + active +
                '}';
    }
}
