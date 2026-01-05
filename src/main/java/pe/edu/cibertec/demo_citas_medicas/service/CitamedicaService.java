package pe.edu.cibertec.demo_citas_medicas.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.demo_citas_medicas.model.Citamedica;
import pe.edu.cibertec.demo_citas_medicas.model.Medico;
import pe.edu.cibertec.demo_citas_medicas.model.Paciente;
import pe.edu.cibertec.demo_citas_medicas.repository.CitamedicaRepository;
import pe.edu.cibertec.demo_citas_medicas.repository.MedicoRepository;
import pe.edu.cibertec.demo_citas_medicas.repository.PacienteRepository;

@RequiredArgsConstructor
@Service
public class CitamedicaService {
    private final CitamedicaRepository citamedicaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public List<Citamedica> getAllCitamedicas() {
        return citamedicaRepository.findAll();
    }

    public Page<Citamedica> getAllCitamedicasPage(Pageable pageable) {
        return citamedicaRepository.findAll(pageable);
    }

    public Optional<Citamedica> getCitamedicaById(Integer id) {
        return citamedicaRepository.findById(id);
    }

    // Buscar citas por ID de paciente
    public List<Citamedica> buscarPorPacienteId(Integer pacienteId) {
        return citamedicaRepository.buscarPorPacienteId(pacienteId);
    }

    // Buscar citas por DNI de paciente
    public List<Citamedica> buscarPorDniPaciente(String dni) {
        return citamedicaRepository.buscarPorDniPaciente(dni);
    }

    // Buscar citas por nombre de paciente
    public List<Citamedica> buscarPorNombrePaciente(String nombre) {
        return citamedicaRepository.buscarPorNombrePaciente(nombre);
    }

    // Buscar citas por ID de médico
    public List<Citamedica> buscarPorMedicoId(Integer medicoId) {
        return citamedicaRepository.buscarPorMedicoId(medicoId);
    }

    // Buscar citas pendientes de un paciente
    public List<Citamedica> buscarCitasPendientesPorPaciente(Integer pacienteId) {
        return citamedicaRepository.buscarCitasPendientesPorPaciente(pacienteId);
    }

    // ✅ CORREGIDO: Buscar citas de hoy con zona horaria de Perú
    public List<Citamedica> buscarCitasDeHoy() {
        // Usar zona horaria de Perú (UTC-5)
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        System.out.println("DEBUG - Fecha actual (Lima): " + hoy);
        return citamedicaRepository.buscarCitasDeHoy(hoy);
    }
    
    // Obtener citas de un médico en una fecha
    public List<Citamedica> obtenerCitasPorMedicoYFecha(Integer medicoId, LocalDate fecha) {
        return citamedicaRepository.obtenerCitasPorMedicoYFecha(medicoId, fecha);
    }

    @Transactional
    public Citamedica saveCitamedica(Citamedica citamedica) {
        Integer medicoId = citamedica.getMedicorelacion().getMedicoid();
        Integer pacienteId = citamedica.getPacienterelacion().getPacienteid();
        LocalDate fecha = citamedica.getFecha();
        LocalTime hora = citamedica.getHora();

        // Validar que no exista una cita con el mismo médico, fecha Y hora
        if (citamedica.getCitaid() == null) {
            // Solo validar en creación (no en edición)
            if (citamedicaRepository.existsByMedicoFechaYHora(medicoId, fecha, hora)) {
                throw new IllegalArgumentException(
                    String.format("El médico ya tiene una cita registrada el %s a las %s.", 
                        fecha, hora)
                );
            }
        }

        // Verificar que el médico existe
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));
        
        // Verificar que el paciente existe
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        citamedica.setMedicorelacion(medico);
        citamedica.setPacienterelacion(paciente);

        return citamedicaRepository.save(citamedica);
    }

    public Citamedica marcarComoAtendida(Integer id) {
        Citamedica cita = citamedicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        if (Boolean.TRUE.equals(cita.getAtendida())) {
            return cita;
        }
        cita.setAtendida(true);
        return citamedicaRepository.save(cita);
    }
}