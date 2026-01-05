package pe.edu.cibertec.demo_citas_medicas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.demo_citas_medicas.model.Paciente;
import pe.edu.cibertec.demo_citas_medicas.repository.PacienteRepository;

@RequiredArgsConstructor
@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;

    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }

    public Page<Paciente> getAllPacientesPage(Pageable pageable) {
        return pacienteRepository.findAll(pageable);
    }

    public Optional<Paciente> getPacienteById(Integer id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isPresent()) {
            return paciente;
        }
        return Optional.empty();
    }
    
    // Buscar paciente por DNI exacto
    public Optional<Paciente> getPacienteByDni(String dni) {
        return pacienteRepository.findByDni(dni);
    }
    
    // Buscar pacientes por DNI parcial
    public List<Paciente> buscarPorDni(String dni) {
        return pacienteRepository.buscarPorDni(dni);
    }
    
    // Buscar pacientes por nombre o apellido
    public List<Paciente> buscarPorNombreOApellido(String texto) {
        return pacienteRepository.buscarPorNombreOApellido(texto);
    }

    public Paciente savePaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }
}