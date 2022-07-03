package es.fernandopal.aforoqr.api.appuser.role;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import es.fernandopal.aforoqr.api.exception.ApiBadRequestException;
import es.fernandopal.aforoqr.api.properties.CustomProperties;
import es.fernandopal.aforoqr.api.security.SecurityService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//@Service
//@AllArgsConstructor
//public class RoleServiceImpl implements RoleService {
//
//    private final static Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);
//    private final FirebaseAuth firebaseAuth;
//    private final CustomProperties securityProps;
//    private final SecurityService securityService;
//
//    @Override
//    public void addRole(String uid, String role, boolean systemBypass) {
//        securityService.preventAccess(!systemBypass && !securityService.isAdmin());
//
//        try {
//            UserRecord user = firebaseAuth.getUser(uid);
//            Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
//            if (securityProps.getRoles().contains(role)) {
//                if (!claims.containsKey(role)) {
//                    claims.put(role, true);
//                }
//                firebaseAuth.setCustomUserClaims(uid, claims);
//            } else {
//                throw new ApiBadRequestException("Not a valid Application role");
//            }
//        } catch (FirebaseAuthException e) {
//            LOGGER.error(String.valueOf(e));
//        }
//    }
//
//    @Override
//    public void removeRole(String uid, String role, boolean systemBypass) {
//        securityService.preventAccess(!systemBypass && !securityService.isAdmin());
//
//        try {
//            UserRecord user = firebaseAuth.getUser(uid);
//            Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
//            claims.remove(role);
//            firebaseAuth.setCustomUserClaims(uid, claims);
//        } catch (FirebaseAuthException e) {
//            LOGGER.error(String.valueOf(e));
//        }
//    }
//}