package pe.edu.cibertec.demo_citas_medicas.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import pe.edu.cibertec.demo_citas_medicas.model.Usuario;

import java.security.Key;

@Service
public class JwtService implements IJwtService {

    private static final String SECRET = "mi-clave-secreta-super-larga-y-segura-123456789";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    @Override
    public String generarTokens(Usuario usuario, List<GrantedAuthority> auths) {
        return Jwts.builder()
                .id(usuario.getIdusuario().toString())
                .subject(usuario.getNomusuario())
                .claim("Authorities",
                        auths.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .claim("admin", usuario.isAdmin())
                .claim("admin", auths.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(KEY)
                .compact();
    }

    @Override
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean tokenValido(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    @Override
    public void setAutenticacion(Claims claims) {
        List<String> auths = claims.get("Authorities", List.class);
        List<SimpleGrantedAuthority> list = auths.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null, list);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
