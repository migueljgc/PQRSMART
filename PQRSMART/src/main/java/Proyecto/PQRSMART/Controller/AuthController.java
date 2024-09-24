package Proyecto.PQRSMART.Controller;

import Proyecto.PQRSMART.Controller.models.AuthResponse;
import Proyecto.PQRSMART.Controller.models.AuthenticationRequest;
import Proyecto.PQRSMART.Controller.models.RegisterRequest;
import Proyecto.PQRSMART.Domain.Dto.UsuarioDto;
import Proyecto.PQRSMART.Domain.Service.AuthService;
import Proyecto.PQRSMART.Domain.Service.JwtService;
import Proyecto.PQRSMART.Domain.Service.UsuarioService;
import Proyecto.PQRSMART.Persistence.Entity.User;
import Proyecto.PQRSMART.Persistence.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    UsuarioRepository userRepository;

    @Autowired
    private UsuarioService userService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> Register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/registerUser")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/editar")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication));
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestBody UsuarioDto userDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        userDTO.setUser(username);  // Assuming the UserDTO has a user field to store the username
        UsuarioDto updatedUser = userService.upda(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            if (jwtService.validateToken(token)) {
                String username = jwtService.getUserName(token);
                userService.verifyUser(username); // Implementa la lógica para marcar al usuario como verificado
                return ResponseEntity.ok("Correo electrónico verificado correctamente.");
            } else {
                return ResponseEntity.badRequest().body("Enlace de verificación inválido o expirado.");
            }
        }
        catch (Exception e){
            System.out.println(e);
            return ResponseEntity.internalServerError().body("error"+e);
        }

    }


}
