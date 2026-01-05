package pe.edu.cibertec.demo_citas_medicas.service.imp;

import org.springframework.stereotype.Service;

import pe.edu.cibertec.demo_citas_medicas.model.Usuario;
import pe.edu.cibertec.demo_citas_medicas.repository.UsuarioRepository;
import pe.edu.cibertec.demo_citas_medicas.service.IUsuarioService;

@Service
public class UsuarioService implements IUsuarioService{

	private final UsuarioRepository usuarioRepository;
	
	
	
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}



	@Override
	public Usuario obtenerUsuarioPorNomusuario(String nomUsuario) {
		return usuarioRepository.findByNomusuario(nomUsuario);
	}

}
