package pe.edu.cibertec.demo_citas_medicas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.demo_citas_medicas.model.Paciente;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    
    // Búsqueda por DNI exacto
    Optional<Paciente> findByDni(String dni);
    
    // Búsqueda por DNI parcial
    @Query("SELECT p FROM Paciente p WHERE p.dni LIKE CONCAT('%', :dni, '%')")
    List<Paciente> buscarPorDni(@Param("dni") String dni);
    
    // Búsqueda por nombre o apellido
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Paciente> buscarPorNombreOApellido(@Param("texto") String texto);
}