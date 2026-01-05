package pe.edu.cibertec.demo_citas_medicas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.edu.cibertec.demo_citas_medicas.model.Paciente;
import pe.edu.cibertec.demo_citas_medicas.service.PacienteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pacientes")
public class PacientesController {
    private final PacienteService pacienteService;

    // Endpoint de búsqueda - DEBE IR ANTES de /{id}
    @GetMapping("/buscar")
    public ResponseEntity<Page<Paciente>> buscarPacientes(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Paciente> pacientes = pacienteService.getAllPacientesPage(pageable);
        
        // Si hay filtro, podrías filtrar aquí (opcional)
        // Por ahora devuelve todos paginados
        
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes() {
        List<Paciente> pacientes = pacienteService.getAllPacientes();
        if (pacientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/pagina")
    public ResponseEntity<?> getAllPacientesPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Paciente> pacientes = pacienteService.getAllPacientesPage(pageable);
        if (pacientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> getPacienteById(@PathVariable Integer id) throws Exception {
        Paciente paciente = pacienteService.getPacienteById(id)
                .orElseThrow(() -> new Exception("El paciente no existe"));
        
        return ResponseEntity.ok(paciente);
    }

    @PostMapping
    public ResponseEntity<?> savePaciente(@Valid @RequestBody Paciente paciente, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Errores de validación");
            respuestaError.put("errors", errores);
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        // Validaciones adicionales de trim (evitar espacios)
        if (paciente.getNombre().trim().isEmpty() ||
            paciente.getApellido().trim().isEmpty() ||
            paciente.getEmail().trim().isEmpty() ||
            paciente.getTelefono().trim().isEmpty() ||
            paciente.getDni().trim().isEmpty()) {

            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Los campos no pueden estar vacíos");
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        try {
            Paciente guardado = pacienteService.savePaciente(paciente);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Error al guardar el paciente: " + e.getMessage());
            return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaciente(
            @PathVariable Integer id,
            @Valid @RequestBody Paciente paciente,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Errores de validación");
            respuestaError.put("errors", errores);
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        Paciente current = pacienteService.getPacienteById(id)
                .orElseThrow(() -> new Exception("El paciente no existe"));

        current.setNombre(paciente.getNombre().trim());
        current.setApellido(paciente.getApellido().trim());
        current.setEmail(paciente.getEmail().trim());
        current.setEdad(paciente.getEdad());
        current.setTelefono(paciente.getTelefono().trim());
        current.setDni(paciente.getDni().trim());

        try {
            Paciente actualizado = pacienteService.savePaciente(current);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Error al actualizar el paciente: " + e.getMessage());
            return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Paciente> updatePartialPaciente(
            @PathVariable Integer id,
            @RequestBody Paciente paciente) throws Exception {
        Paciente currentPaciente = pacienteService.getPacienteById(id)
                .orElseThrow(() -> new Exception("El paciente no existe"));
        
        currentPaciente.setNombre(paciente.getNombre());
        currentPaciente.setApellido(paciente.getApellido());
        currentPaciente.setEmail(paciente.getEmail());
        currentPaciente.setEdad(paciente.getEdad());
        currentPaciente.setTelefono(paciente.getTelefono());
        currentPaciente.setDni(paciente.getDni());
        
        return new ResponseEntity<Paciente>(
                pacienteService.savePaciente(currentPaciente), HttpStatus.OK);
    }
}