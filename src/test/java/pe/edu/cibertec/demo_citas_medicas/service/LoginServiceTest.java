package pe.edu.cibertec.demo_citas_medicas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.cibertec.demo_citas_medicas.model.Rol;
import pe.edu.cibertec.demo_citas_medicas.model.Usuario;
import pe.edu.cibertec.demo_citas_medicas.repository.RolRepository;
import pe.edu.cibertec.demo_citas_medicas.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    void getAllUsuarios() {
        when(usuarioRepository.findAll())
                .thenReturn(List.of(new Usuario(), new Usuario()));

        List<Usuario> usuarios = loginService.getAllUsuarios();

        assertEquals(2, usuarios.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void getUsuarioById_existente() {
        Usuario usuario = new Usuario();
        usuario.setIdusuario(1);

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> result = loginService.getUsuarioById(1);

        assertTrue(result.isPresent());
        verify(usuarioRepository).findById(1);
    }

    @Test
    void getUsuarioById_noExiste() {
        when(usuarioRepository.findById(99))
                .thenReturn(Optional.empty());

        Optional<Usuario> result = loginService.getUsuarioById(99);

        assertTrue(result.isEmpty());
        verify(usuarioRepository).findById(99);
    }

    @Test
    void saveUsuario_normal_USER() {
        Usuario usuario = new Usuario();
        usuario.setAdmin(false);
        usuario.setRoles(new HashSet<>());

        Rol rolUser = new Rol();
        rolUser.setNomrol("USER");

        when(rolRepository.findByNomrol("USER"))
                .thenReturn(rolUser);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        Usuario result = loginService.saveUsuario(usuario);

        assertNotNull(result);
        assertEquals(1, result.getRoles().size());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void saveUsuario_admin_USER_y_ADMIN() {
        Usuario usuario = new Usuario();
        usuario.setAdmin(true);
        usuario.setRoles(new HashSet<>());

        Rol rolUser = new Rol();
        rolUser.setNomrol("USER");

        Rol rolAdmin = new Rol();
        rolAdmin.setNomrol("ADMIN");

        when(rolRepository.findByNomrol("USER"))
                .thenReturn(rolUser);
        when(rolRepository.findByNomrol("ADMIN"))
                .thenReturn(rolAdmin);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        Usuario result = loginService.saveUsuario(usuario);

        assertNotNull(result);
        assertEquals(2, result.getRoles().size());
        verify(usuarioRepository).save(usuario);
    }
}

