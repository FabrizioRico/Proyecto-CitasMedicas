package pe.edu.cibertec.demo_citas_medicas.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import pe.edu.cibertec.demo_citas_medicas.model.Usuario;

public interface IJwtService {

	
	String generarTokens(Usuario usuario, List<GrantedAuthority> auths);
    Claims parseClaims(String token);
    boolean tokenValido(String token);
    String extraerToken(HttpServletRequest request);
    void setAutenticacion(Claims claims);
}
