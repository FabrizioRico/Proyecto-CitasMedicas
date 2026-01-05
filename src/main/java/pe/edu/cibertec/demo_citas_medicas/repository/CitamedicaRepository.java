package pe.edu.cibertec.demo_citas_medicas.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.demo_citas_medicas.model.Citamedica;

@Repository
public interface CitamedicaRepository extends JpaRepository<Citamedica, Integer> {
   
	// Valida fecha Y hora para evitar conflictos
    @Query("SELECT COUNT(c) > 0 FROM Citamedica c WHERE c.medicorelacion.medicoid = :medicoId AND c.fecha = :fecha AND c.hora = :hora")
    boolean existsByMedicoFechaYHora(@Param("medicoId") Integer medicoId, 
                                      @Param("fecha") LocalDate fecha, 
                                      @Param("hora") LocalTime hora);
    
    // Obtener citas de un médico en una fecha específica
    @Query("SELECT c FROM Citamedica c WHERE c.medicorelacion.medicoid = :medicoId AND c.fecha = :fecha ORDER BY c.hora")
    List<Citamedica> obtenerCitasPorMedicoYFecha(@Param("medicoId") Integer medicoId, 
                                                   @Param("fecha") LocalDate fecha);
    
    // Buscar citas por ID de paciente
    @Query("SELECT c FROM Citamedica c WHERE c.pacienterelacion.pacienteid = :pacienteId ORDER BY c.fecha DESC, c.hora DESC")
    List<Citamedica> buscarPorPacienteId(@Param("pacienteId") Integer pacienteId);
    
    // Buscar citas por DNI de paciente
    @Query("SELECT c FROM Citamedica c WHERE c.pacienterelacion.dni = :dni ORDER BY c.fecha DESC, c.hora DESC")
    List<Citamedica> buscarPorDniPaciente(@Param("dni") String dni);
    
    // Buscar citas por nombre de paciente (parcial)
    @Query("SELECT c FROM Citamedica c WHERE LOWER(c.pacienterelacion.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(c.pacienterelacion.apellido) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY c.fecha DESC, c.hora DESC")
    List<Citamedica> buscarPorNombrePaciente(@Param("nombre") String nombre);
    
    // Buscar citas por ID de médico
    @Query("SELECT c FROM Citamedica c WHERE c.medicorelacion.medicoid = :medicoId ORDER BY c.fecha DESC, c.hora DESC")
    List<Citamedica> buscarPorMedicoId(@Param("medicoId") Integer medicoId);
    
    // Buscar citas pendientes de un paciente
    @Query("SELECT c FROM Citamedica c WHERE c.pacienterelacion.pacienteid = :pacienteId AND c.atendida = false ORDER BY c.fecha ASC, c.hora ASC")
    List<Citamedica> buscarCitasPendientesPorPaciente(@Param("pacienteId") Integer pacienteId);
    
    // Buscar citas de hoy
    @Query("SELECT c FROM Citamedica c WHERE c.fecha = :fecha ORDER BY c.hora ASC")
    List<Citamedica> buscarCitasDeHoy(@Param("fecha") LocalDate fecha);
}