package pe.edu.cibertec.demo_citas_medicas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.cibertec.demo_citas_medicas.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer>{
	
	Rol findByNomrol(String nomrol);

}
