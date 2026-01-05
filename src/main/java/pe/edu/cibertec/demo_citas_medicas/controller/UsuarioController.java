package pe.edu.cibertec.demo_citas_medicas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.edu.cibertec.demo_citas_medicas.model.Usuario;
import pe.edu.cibertec.demo_citas_medicas.service.LoginService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final LoginService loginService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioController(LoginService loginService, BCryptPasswordEncoder passwordEncoder) {
        this.loginService = loginService;
        this.passwordEncoder = passwordEncoder;
    }

    // Agregado @Valid y manejo de errores
    @PostMapping
    public ResponseEntity<?> saveUsuario(@Valid @RequestBody Usuario usuario, BindingResult result) {
        
        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            
            // Recopilar todos los errores
            result.getFieldErrors().forEach(error -> {
                errores.put(error.getField(), error.getDefaultMessage());
            });
            
            // Crear respuesta de error
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Errores de validación");
            respuestaError.put("errors", errores);
            
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }
        
        // Validar que los campos no estén vacíos (trim)
        if (usuario.getNomusuario().trim().isEmpty() || 
            usuario.getNombres().trim().isEmpty() || 
            usuario.getApellidos().trim().isEmpty() || 
            usuario.getEmail().trim().isEmpty() || 
            usuario.getPassword().trim().isEmpty()) {
            
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Los campos no pueden estar vacíos");
            
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }
        
        try {
            // Encriptar la contraseña antes de guardar
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setActivo(true);
            
            Usuario usuarioGuardado = loginService.saveUsuario(usuario);
            return new ResponseEntity<>(usuarioGuardado, HttpStatus.CREATED);
            
        } catch (Exception e) {
            // Manejo de excepciones
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Error al guardar el usuario: " + e.getMessage());
            
            return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}