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
import pe.edu.cibertec.demo_citas_medicas.model.Citamedica;
import pe.edu.cibertec.demo_citas_medicas.service.CitamedicaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/citamedica")
public class CitamedicaController {
    private final CitamedicaService citamedicaService;
    
    @GetMapping
    public ResponseEntity<List<Citamedica>> getAllCitamedicas() {
        List<Citamedica> list = citamedicaService.getAllCitamedicas();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/pagina")
    public ResponseEntity<?> getAllCitamedicasPage(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Citamedica> list = citamedicaService.getAllCitamedicasPage(pageable);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Citamedica> getCitamedicaById(@PathVariable Integer id) throws Exception {
        Citamedica cita = citamedicaService.getCitamedicaById(id)
                .orElseThrow(() -> new Exception("La cita médica no existe"));
        return ResponseEntity.ok(cita);
    }
    
    @PostMapping
    public ResponseEntity<?> saveCitamedica(@Valid @RequestBody Citamedica citamedica, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Errores de validación");
            respuestaError.put("errors", errores);
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        try {
            Citamedica saved = citamedicaService.saveCitamedica(citamedica);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Error al guardar la cita: " + e.getMessage());
            return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCitamedica(
            @PathVariable Integer id,
            @Valid @RequestBody Citamedica citamedica,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Errores de validación");
            respuestaError.put("errors", errores);
            return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
        }

        Citamedica current = citamedicaService.getCitamedicaById(id)
                .orElseThrow(() -> new Exception("La cita médica no existe"));

        current.setFecha(citamedica.getFecha());
        current.setHora(citamedica.getHora());
        current.setMedicorelacion(citamedica.getMedicorelacion());
        current.setPacienterelacion(citamedica.getPacienterelacion());

        try {
            Citamedica updated = citamedicaService.saveCitamedica(current);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> respuestaError = new HashMap<>();
            respuestaError.put("message", "Error al actualizar la cita: " + e.getMessage());
            return new ResponseEntity<>(respuestaError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/atender")
    public ResponseEntity<Citamedica> marcarComoAtendida(@PathVariable Integer id) {
        try {
            Citamedica updated = citamedicaService.marcarComoAtendida(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}