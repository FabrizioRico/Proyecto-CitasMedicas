package pe.edu.cibertec.demo_citas_medicas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.demo_citas_medicas.model.Medico;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    
    // Lista de médicos activos
    List<Medico> findByActivoTrue();
    
    // Búsqueda por nombre (devuelve lista)
    @Query("SELECT m FROM Medico m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(m.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Medico> buscarPorNombre(@Param("nombre") String nombre);
    
    // Búsqueda por nombre con paginación nativa
    @Query("SELECT m FROM Medico m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(m.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Medico> buscarPorNombrePage(@Param("nombre") String nombre, Pageable pageable);
    
    // Búsqueda por especialidad
    @Query("SELECT m FROM Medico m WHERE LOWER(m.especialidad) LIKE LOWER(CONCAT('%', :especialidad, '%'))")
    List<Medico> buscarPorEspecialidad(@Param("especialidad") String especialidad);
    
    // Listar todas las especialidades únicas
    @Query("SELECT DISTINCT m.especialidad FROM Medico m ORDER BY m.especialidad")
    List<String> obtenerEspecialidades();
}