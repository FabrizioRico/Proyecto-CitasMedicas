package pe.edu.cibertec.demo_citas_medicas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import pe.edu.cibertec.demo_citas_medicas.model.Rol;
import pe.edu.cibertec.demo_citas_medicas.model.Usuario;
import pe.edu.cibertec.demo_citas_medicas.repository.RolRepository;
import pe.edu.cibertec.demo_citas_medicas.repository.UsuarioRepository;

@Service
public class LoginService {

    private UsuarioRepository usuarioRepository;
    private RolRepository roleRepository;

    public LoginService(UsuarioRepository usuarioRepository, RolRepository roleRepository) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuarioById(Integer id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return usuario;
        }
        return Optional.empty();
    }

    public Usuario saveUsuario(Usuario usuario) {
        Rol rolUser = this.roleRepository.findByNomrol("USER");

        usuario.getRoles().add(rolUser);

        if (usuario.isAdmin()) {
            Rol rolAdmin = this.roleRepository.findByNomrol("ADMIN");
            usuario.getRoles().add(rolAdmin);
        }

        return usuarioRepository.save(usuario);
    }

}
