package es.fernandopal.aforoqr.api.security;

import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {

    private FirebaseToken decodedToken;
    private String idToken;

}