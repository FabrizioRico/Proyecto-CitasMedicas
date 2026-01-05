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
import pe.edu.cibertec.demo_citas_medicas.model.Medico;
import pe.edu.cibertec.demo_citas_medicas.service.MedicoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medicos")
public class MedicosController {
    private final MedicoService medicoService;

    // Este endpoint DEBE devolver datos paginados
    // para que coincida con lo que espera el frontend
    @GetMapping("/buscar")
    public ResponseEntity<Page<Medico>> buscarMedicosPage(
            @RequestParam(required = false, defaultValue = "") String filtro,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        // Si hay filtro, buscar por nombre
        if (filtro != null && !filtro.trim().isEmpty()) {
            Page<Medico> medicos = medicoService.buscarPorNombrePage(filtro, pageable);
            return ResponseEntity.ok(medicos);
        }
        
        // Si no hay filtro, devolver todos paginados
        Page<Medico> medicos = medicoService.getAllMedicosPage(pageable);
        return ResponseEntity.ok(medicos);
    }

    @GetMapping
    public ResponseEntity<List<Medico>> getAllMedicos() {
        List<Medico> medicos = medicoService.getAllMedicos();
        if (medicos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medicos);
    }

    @GetMapping("/pagina")
    public ResponseEntity<?> getAllMedicosPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Medico> medicos = medicoService.getAllMedicosPage(pageable);
        if (medicos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medicos);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Medico>> getMedicosActivos() {
        List<Medico> medicosActivos = medicoService.getMedicosActivos();
        if (medicosActivos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medicosActivos);
    }

    // /{id} debe ir DESPUÉS de las rutas específicas
    @GetMapping("/{id}")
    public ResponseEntity<Medico> getMedicoById(@PathVariable Integer id) throws Exception {
        Medico medico = medicoService.getMedicoById(id)
                .orElseThrow(() -> new Exception("El médico no existe"));
        return ResponseEntity.ok(medico);
    }

    @PostMapping
    public ResponseEntity<?> saveMedico(@Valid @RequestBody Medico medico, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Errores de validación");
            respuestaError.put("errors", errores);
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        if (medico.getNombre().trim().isEmpty() ||
            medico.getApellido().trim().isEmpty() ||
            medico.getTelefono().trim().isEmpty() ||
            medico.getEspecialidad().trim().isEmpty()) {
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Los campos no pueden estar vacíos");
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        try {
            Medico guardado = medicoService.saveMedico(medico);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Error al guardar el médico: " + e.getMessage());
            return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedico(
            @PathVariable Integer id,
            @Valid @RequestBody Medico medico,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Errores de validación");
            respuestaError.put("errors", errores);
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        Medico current = medicoService.getMedicoById(id)
                .orElseThrow(() -> new Exception("El médico no existe"));

        current.setNombre(medico.getNombre().trim());
        current.setApellido(medico.getApellido().trim());
        current.setTelefono(medico.getTelefono().trim());
        current.setEspecialidad(medico.getEspecialidad().trim());

        try {
            Medico actualizado = medicoService.saveMedico(current);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Error al actualizar el médico: " + e.getMessage());
            return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Medico> updatePartialMedico(
            @PathVariable Integer id, 
            @RequestBody Medico medico) throws Exception {
        Medico currentMedico = medicoService.getMedicoById(id)
                .orElseThrow(() -> new Exception("El médico no existe"));

        currentMedico.setNombre(medico.getNombre());
        currentMedico.setApellido(medico.getApellido());
        currentMedico.setTelefono(medico.getTelefono());
        currentMedico.setEspecialidad(medico.getEspecialidad());

        return new ResponseEntity<Medico>(medicoService.saveMedico(currentMedico), HttpStatus.OK);
    }
    
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Medico> toggleActivo(@PathVariable Integer id) throws Exception {
        Medico updated = medicoService.toggleActivo(id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}