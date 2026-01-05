package pe.edu.cibertec.demo_citas_medicas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pe.edu.cibertec.demo_citas_medicas.model.Paciente;
import pe.edu.cibertec.demo_citas_medicas.repository.PacienteRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente1;
    private Paciente paciente2;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        paciente1 = new Paciente();
        paciente1.setPacienteid(1);
        paciente1.setNombre("Juan");
        paciente1.setApellido("Pérez");
        paciente1.setEmail("juan@example.com");
        paciente1.setEdad(30);
        paciente1.setTelefono("987654321");
        paciente1.setDni("12345678");

        paciente2 = new Paciente();
        paciente2.setPacienteid(2);
        paciente2.setNombre("María");
        paciente2.setApellido("García");
        paciente2.setEmail("maria@example.com");
        paciente2.setEdad(25);
        paciente2.setTelefono("987654322");
        paciente2.setDni("87654321");
    }

    @Test
    void testGetAllPacientes_DeberiaRetornarListaDePacientes() {
        // Arrange (Preparar)
        List<Paciente> pacientesEsperados = Arrays.asList(paciente1, paciente2);
        when(pacienteRepository.findAll()).thenReturn(pacientesEsperados);

        // Act (Actuar)
        List<Paciente> resultado = pacienteService.getAllPacientes();

        // Assert (Verificar)
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("María", resultado.get(1).getNombre());
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPacientes_DeberiaRetornarListaVacia() {
        // Arrange
        when(pacienteRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Paciente> resultado = pacienteService.getAllPacientes();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPacientesPage_DeberiaRetornarPaginaDePacientes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Paciente> pacientes = Arrays.asList(paciente1, paciente2);
        Page<Paciente> paginaEsperada = new PageImpl<>(pacientes, pageable, pacientes.size());
        when(pacienteRepository.findAll(pageable)).thenReturn(paginaEsperada);

        // Act
        Page<Paciente> resultado = pacienteService.getAllPacientesPage(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());
        assertEquals(2, resultado.getContent().size());
        verify(pacienteRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetPacienteById_CuandoExiste_DeberiaRetornarPaciente() {
        // Arrange
        when(pacienteRepository.findById(1)).thenReturn(Optional.of(paciente1));

        // Act
        Optional<Paciente> resultado = pacienteService.getPacienteById(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
        assertEquals("12345678", resultado.get().getDni());
        verify(pacienteRepository, times(1)).findById(1);
    }

    @Test
    void testGetPacienteById_CuandoNoExiste_DeberiaRetornarEmpty() {
        // Arrange
        when(pacienteRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Paciente> resultado = pacienteService.getPacienteById(999);

        // Assert
        assertFalse(resultado.isPresent());
        verify(pacienteRepository, times(1)).findById(999);
    }

    @Test
    void testSavePaciente_DeberiaGuardarYRetornarPaciente() {
        // Arrange
        when(pacienteRepository.save(paciente1)).thenReturn(paciente1);

        // Act
        Paciente resultado = pacienteService.savePaciente(paciente1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("juan@example.com", resultado.getEmail());
        verify(pacienteRepository, times(1)).save(paciente1);
    }

    @Test
    void testSavePaciente_ConNuevoPaciente_DeberiaAsignarId() {
        // Arrange
        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setNombre("Carlos");
        nuevoPaciente.setApellido("López");
        
        Paciente pacienteGuardado = new Paciente();
        pacienteGuardado.setPacienteid(3);
        pacienteGuardado.setNombre("Carlos");
        pacienteGuardado.setApellido("López");
        
        when(pacienteRepository.save(nuevoPaciente)).thenReturn(pacienteGuardado);

        // Act
        Paciente resultado = pacienteService.savePaciente(nuevoPaciente);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getPacienteid());
        assertEquals(3, resultado.getPacienteid());
        verify(pacienteRepository, times(1)).save(nuevoPaciente);
    }
}