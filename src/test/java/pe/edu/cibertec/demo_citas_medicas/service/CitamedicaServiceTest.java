package pe.edu.cibertec.demo_citas_medicas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.cibertec.demo_citas_medicas.model.Citamedica;
import pe.edu.cibertec.demo_citas_medicas.model.Medico;
import pe.edu.cibertec.demo_citas_medicas.model.Paciente;
import pe.edu.cibertec.demo_citas_medicas.repository.CitamedicaRepository;
import pe.edu.cibertec.demo_citas_medicas.repository.MedicoRepository;
import pe.edu.cibertec.demo_citas_medicas.repository.PacienteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitamedicaServiceTest {

    @Mock
    private CitamedicaRepository citamedicaRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private CitamedicaService service;



    @Test
    void getAllCitamedicas() {
        when(citamedicaRepository.findAll()).thenReturn(List.of(new Citamedica()));
        List<Citamedica> result = service.getAllCitamedicas();
        assertEquals(1, result.size());
    }

    @Test
    void getCitamedicaById() {
        Citamedica cita = new Citamedica();
        when(citamedicaRepository.findById(1)).thenReturn(Optional.of(cita));

        Optional<Citamedica> result = service.getCitamedicaById(1);
        assertTrue(result.isPresent());
    }

    @Test
    void saveCitamedica() {
        // Datos simulados
        Medico medico = new Medico();
        medico.setMedicoid(10);

        Paciente paciente = new Paciente();
        paciente.setPacienteid(20);

        Citamedica cita = new Citamedica();
        cita.setMedicorelacion(medico);
        cita.setPacienterelacion(paciente);

        // Mocks esperados
        when(medicoRepository.findById(10)).thenReturn(Optional.of(medico));
        when(pacienteRepository.findById(20)).thenReturn(Optional.of(paciente));
        when(citamedicaRepository.save(any(Citamedica.class))).thenReturn(cita);

        // Ejecutar
        Citamedica result = service.saveCitamedica(cita);

        // Validar
        assertNotNull(result);
        verify(citamedicaRepository).save(any(Citamedica.class));

    }

    @Test
    void marcarComoAtendida() {
        Citamedica citaMock = new Citamedica();
        citaMock.setCitaid(1);
        citaMock.setAtendida(false);

        when(citamedicaRepository.findById(1))
                .thenReturn(Optional.of(citaMock));
        when(citamedicaRepository.save(any()))
                .thenReturn(citaMock);

        Citamedica result = service.marcarComoAtendida(1);

        assertTrue(result.getAtendida());

    }

}