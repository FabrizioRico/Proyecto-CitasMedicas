package pe.edu.cibertec.demo_citas_medicas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.demo_citas_medicas.model.Medico;
import pe.edu.cibertec.demo_citas_medicas.repository.MedicoRepository;

@RequiredArgsConstructor
@Service
public class MedicoService {
    private final MedicoRepository medicoRepository;

    // Búsqueda por nombre (devuelve lista simple)
    public List<Medico> buscarPorNombre(String nombre) {
        return medicoRepository.buscarPorNombre(nombre);
    }

    // Búsqueda por nombre con paginación
    public Page<Medico> buscarPorNombrePage(String nombre, Pageable pageable) {
        return medicoRepository.buscarPorNombrePage(nombre, pageable);
    }

    // Búsqueda por especialidad
    public List<Medico> buscarPorEspecialidad(String especialidad) {
        return medicoRepository.buscarPorEspecialidad(especialidad);
    }
    
    // Obtener todas las especialidades
    public List<String> obtenerEspecialidades() {
        return medicoRepository.obtenerEspecialidades();
    }

    public List<Medico> getAllMedicos() {
        return medicoRepository.findAll();
    }
    
    public Page<Medico> getAllMedicosPage(Pageable pageable) {
        return medicoRepository.findAll(pageable);
    }

    public Optional<Medico> getMedicoById(Integer id) {
        Optional<Medico> medico = medicoRepository.findById(id);
        
        if (medico.isPresent())
            return medico;
        return Optional.empty();
    }

    public Medico saveMedico(Medico medico) {
        return medicoRepository.save(medico);
    }
    
    public Medico toggleActivo(Integer id) throws Exception {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new Exception("El médico no existe"));
        medico.setActivo(!medico.isActivo());
        return medicoRepository.save(medico);
    }
    
    public List<Medico> getMedicosActivos() {
        return medicoRepository.findByActivoTrue();
    }
}