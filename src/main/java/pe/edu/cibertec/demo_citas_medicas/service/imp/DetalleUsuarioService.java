package pe.edu.cibertec.demo_citas_medicas.service.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.demo_citas_medicas.model.Rol;
import pe.edu.cibertec.demo_citas_medicas.model.Usuario;
import pe.edu.cibertec.demo_citas_medicas.service.IUsuarioService;

@Service
public class DetalleUsuarioService implements UserDetailsService{

	public final IUsuarioService usuarioService;
	
	public DetalleUsuarioService(IUsuarioService usuarioService) {

        this.usuarioService = usuarioService;
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioService.obtenerUsuarioPorNomusuario(username);


        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return getUserDetails(usuario, getAuthorities(usuario.getRoles()));
	}
	
	public List<GrantedAuthority> getAuthorities(Set<Rol> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Rol rol : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNomrol()));
        }
        return authorities;
    }

	
	private UserDetails getUserDetails(Usuario usuario, List<GrantedAuthority> authorities) {
        return new User(usuario.getNomusuario(),
                usuario.getPassword(),
                usuario.getActivo(),
                true, true, true, authorities);
    }
	
}
