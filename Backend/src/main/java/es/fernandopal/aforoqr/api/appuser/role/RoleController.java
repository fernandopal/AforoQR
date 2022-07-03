package es.fernandopal.aforoqr.api.appuser.role;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@RestController
//@AllArgsConstructor
//@RequestMapping("role")
//public class RoleController {
//
//    private final RoleService roleService;
//
//    @PostMapping("add")
//    @Operation(description = "Adds a role to an user")
//    public void addRole(@RequestParam String uid, @RequestParam String role) {
//        roleService.addRole(uid, role, false);
//    }
//
//    @PostMapping("remove")
//    @Operation(description = "Remove a role from an user")
//    public void removeRole(@RequestParam String uid, @RequestParam String role) {
//        roleService.removeRole(uid, role, false);
//    }
//}