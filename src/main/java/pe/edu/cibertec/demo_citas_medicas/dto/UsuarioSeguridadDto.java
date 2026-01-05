package pe.edu.cibertec.demo_citas_medicas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioSeguridadDto {
	 private Integer idusuario;
	    private String nomusuario;
	    private String token;
	    private String mensajeError;
	    
		public UsuarioSeguridadDto(Integer idusuario, String nomusuario, String token, String mensajeError) {
			this.idusuario = idusuario;
			this.nomusuario = nomusuario;
			this.token = token;
			this.mensajeError = mensajeError;
		}
	    
	    
}
