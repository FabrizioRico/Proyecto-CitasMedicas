package pe.edu.cibertec.demo_citas_medicas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.demo_citas_medicas.dto.UsuarioSeguridadDto;
import pe.edu.cibertec.demo_citas_medicas.model.Usuario;
import pe.edu.cibertec.demo_citas_medicas.security.IJwtService;
import pe.edu.cibertec.demo_citas_medicas.service.IUsuarioService;
import pe.edu.cibertec.demo_citas_medicas.service.imp.DetalleUsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
	 private final IUsuarioService usuarioService;
	    private final IJwtService jwtService;
	    private final DetalleUsuarioService detalleUsuarioService;
	    private final AuthenticationManager authManager;

	    public AuthController(IUsuarioService usuarioService, IJwtService jwtService,
	                          DetalleUsuarioService detalleUsuarioService, AuthenticationManager authManager) {
	        this.usuarioService = usuarioService;
	        this.jwtService = jwtService;
	        this.detalleUsuarioService = detalleUsuarioService;
	        this.authManager = authManager;
	    }

	    @GetMapping("/login")
	    public ResponseEntity<UsuarioSeguridadDto> login(
	            @RequestParam String username,
	            @RequestParam String password) {
	        try {
	            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	            if (auth.isAuthenticated()) {
	                Usuario usuario = usuarioService.obtenerUsuarioPorNomusuario(username);
	                List<GrantedAuthority> auths = detalleUsuarioService.getAuthorities(usuario.getRoles());
	                String token = jwtService.generarTokens(usuario, auths);
	                UsuarioSeguridadDto userSecurityDto = new UsuarioSeguridadDto(
	                    usuario.getIdusuario(),
	                    usuario.getNomusuario(),
	                    token, 
	                    null
	                );
	                return ResponseEntity.ok(userSecurityDto);
	            }
	        } catch (Exception e) {
	            // Autenticación fallida
	        }
	        UsuarioSeguridadDto userSecurityDto = new UsuarioSeguridadDto(null, null, null, "Usuario y/o password incorrecto");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userSecurityDto);
	    }
}
