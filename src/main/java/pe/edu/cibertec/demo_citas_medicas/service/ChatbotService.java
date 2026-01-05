package pe.edu.cibertec.demo_citas_medicas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.demo_citas_medicas.dto.ChatbotResponseDto;
import pe.edu.cibertec.demo_citas_medicas.model.Citamedica;
import pe.edu.cibertec.demo_citas_medicas.model.Medico;
import pe.edu.cibertec.demo_citas_medicas.model.Paciente;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final MedicoService medicoService;
    private final PacienteService pacienteService;
    private final CitamedicaService citamedicaService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key:}")
    private String apiKey;

    public ChatbotResponseDto procesarConsulta(String mensaje) {
        try {
            String intencion = analizarIntencion(mensaje);
            return ejecutarAccion(intencion, mensaje);
        } catch (Exception e) {
            return new ChatbotResponseDto(
                "Lo siento, no pude procesar tu consulta. Por favor, intenta reformularla.",
                null,
                "error"
            );
        }
    }

    private String analizarIntencion(String mensaje) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return analizarIntencionBasica(mensaje);
            }

            String prompt = construirPromptAnalisis(mensaje);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "claude-sonnet-4-20250514");
            requestBody.put("max_tokens", 100);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));

            String response = webClient.post()
                    .uri("https://api.anthropic.com/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonResponse = objectMapper.readTree(response);
            String contenido = jsonResponse.get("content").get(0).get("text").asText();
            
            return contenido.trim().toLowerCase();
        } catch (Exception e) {
            return analizarIntencionBasica(mensaje);
        }
    }

    private String analizarIntencionBasica(String mensaje) {
        String mensajeLower = mensaje.toLowerCase();
        
        // ✅ NUEVO: Detectar "datos de medico/paciente" para búsqueda específica
        if (mensajeLower.contains("datos de médico") || mensajeLower.contains("datos de medico") ||
            mensajeLower.contains("datos del médico") || mensajeLower.contains("datos del medico") ||
            mensajeLower.contains("información de médico") || mensajeLower.contains("informacion de medico") ||
            mensajeLower.contains("información del médico") || mensajeLower.contains("informacion del medico")) {
            return "ver_datos_medico";
        }
        
        if (mensajeLower.contains("datos de paciente") || mensajeLower.contains("datos del paciente") ||
            mensajeLower.contains("información de paciente") || mensajeLower.contains("informacion de paciente") ||
            mensajeLower.contains("información del paciente") || mensajeLower.contains("informacion del paciente")) {
            return "ver_datos_paciente";
        }
        
        // Detección de especialidad
        if (mensajeLower.contains("especialidad")) {
            if (mensajeLower.contains("listar") || mensajeLower.contains("mostrar") || 
                mensajeLower.contains("cuál") || mensajeLower.contains("cual") ||
                mensajeLower.contains("qué") || mensajeLower.contains("que") ||
                mensajeLower.contains("hay") || mensajeLower.contains("existen")) {
                return "listar_especialidades";
            }
            return "buscar_por_especialidad";
        }
        
        // Detectar búsqueda por especialidad sin la palabra "especialidad"
        if (mensajeLower.contains("cardiólogo") || mensajeLower.contains("cardiologo") ||
            mensajeLower.contains("cardiología") || mensajeLower.contains("cardiologia")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("psicólogo") || mensajeLower.contains("psicologo") ||
            mensajeLower.contains("psicología") || mensajeLower.contains("psicologia")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("psiquiatra") || mensajeLower.contains("psiquiatría") || 
            mensajeLower.contains("psiquiatria")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("odontólogo") || mensajeLower.contains("odontologo") ||
            mensajeLower.contains("odontología") || mensajeLower.contains("odontologia") ||
            mensajeLower.contains("dentista")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("patólogo") || mensajeLower.contains("patologo") ||
            mensajeLower.contains("patología") || mensajeLower.contains("patologia")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("endocrinólogo") || mensajeLower.contains("endocrinologo") ||
            mensajeLower.contains("endocrinología") || mensajeLower.contains("endocrinologia")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("oftalmólogo") || mensajeLower.contains("oftalmologo") ||
            mensajeLower.contains("oftalmología") || mensajeLower.contains("oftalmologia") ||
            mensajeLower.contains("oculista")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("oncólogo") || mensajeLower.contains("oncologo") ||
            mensajeLower.contains("oncología") || mensajeLower.contains("oncologia")) {
            return "buscar_por_especialidad";
        }
        if (mensajeLower.contains("pediatra") || mensajeLower.contains("pediatría") || 
            mensajeLower.contains("pediatria")) {
            return "buscar_por_especialidad";
        }
        
        // Detección de DNI
        if (mensajeLower.contains("dni")) {
            if (mensajeLower.contains("cita")) {
                return "buscar_citas_por_dni";
            }
            return "buscar_paciente_por_dni";
        }
        
        // ✅ CORREGIDO: Detección de citas de hoy (PRIMERO)
        if (mensajeLower.contains("cita") && 
            (mensajeLower.contains("hoy") || mensajeLower.contains("día") || mensajeLower.contains("dia"))) {
            return "listar_citas_hoy";
        }
        
        // Detección de citas de paciente (DESPUÉS)
        if (mensajeLower.contains("cita") && 
            (mensajeLower.contains("paciente") || mensajeLower.contains("de "))) {
            if (mensajeLower.contains("pendiente")) {
                return "buscar_citas_pendientes_paciente";
            }
            return "buscar_citas_paciente";
        }
        
        // Médicos
        if (mensajeLower.contains("médico") || mensajeLower.contains("medico") || 
            mensajeLower.contains("doctor")) {
            if (mensajeLower.contains("activo") || mensajeLower.contains("disponible")) {
                return "listar_medicos_activos";
            }
            if (mensajeLower.contains("cuántos") || mensajeLower.contains("cantidad") ||
                mensajeLower.contains("numero") || mensajeLower.contains("total")) {
                return "contar_medicos";
            }
            if (mensajeLower.contains("buscar") || mensajeLower.contains("encontrar")) {
                return "buscar_medico";
            }
            return "listar_medicos";
        }
        
        // Pacientes
        if (mensajeLower.contains("paciente")) {
            if (mensajeLower.contains("cuántos") || mensajeLower.contains("cantidad") ||
                mensajeLower.contains("numero") || mensajeLower.contains("total")) {
                return "contar_pacientes";
            }
            if (mensajeLower.contains("buscar") || mensajeLower.contains("encontrar")) {
                return "buscar_paciente";
            }
            return "listar_pacientes";
        }
        
        // Citas
        if (mensajeLower.contains("cita")) {
            if (mensajeLower.contains("pendiente") || mensajeLower.contains("no atendida")) {
                return "listar_citas_pendientes";
            }
            if (mensajeLower.contains("atendida") || mensajeLower.contains("completada")) {
                return "listar_citas_atendidas";
            }
            if (mensajeLower.contains("cuántas") || mensajeLower.contains("cantidad") ||
                mensajeLower.contains("numero") || mensajeLower.contains("total")) {
                return "contar_citas";
            }
            return "listar_citas";
        }
        
        return "ayuda";
    }

    private String construirPromptAnalisis(String mensaje) {
        return String.format(
            "Analiza el siguiente mensaje del usuario y responde ÚNICAMENTE con una de estas palabras: " +
            "listar_medicos, listar_medicos_activos, contar_medicos, buscar_medico, ver_datos_medico, " +
            "buscar_por_especialidad, listar_especialidades, " +
            "listar_pacientes, contar_pacientes, buscar_paciente, ver_datos_paciente, buscar_paciente_por_dni, " +
            "listar_citas, listar_citas_pendientes, listar_citas_atendidas, listar_citas_hoy, contar_citas, " +
            "buscar_citas_paciente, buscar_citas_por_dni, buscar_citas_pendientes_paciente, " +
            "ayuda\n\n" +
            "Mensaje: %s\n\n" +
            "Responde solo con la palabra clave correspondiente.",
            mensaje
        );
    }

    private ChatbotResponseDto ejecutarAccion(String intencion, String mensaje) {
        switch (intencion) {
            // ========== MÉDICOS ==========
            case "listar_medicos":
                List<Medico> medicos = medicoService.getAllMedicos();
                return new ChatbotResponseDto(
                    String.format("He encontrado %d médicos registrados en el sistema.", medicos.size()),
                    medicos,
                    "medicos"
                );

            case "listar_medicos_activos":
                List<Medico> medicosActivos = medicoService.getMedicosActivos();
                return new ChatbotResponseDto(
                    String.format("Hay %d médicos activos disponibles.", medicosActivos.size()),
                    medicosActivos,
                    "medicos"
                );

            case "contar_medicos":
                int totalMedicos = medicoService.getAllMedicos().size();
                return new ChatbotResponseDto(
                    String.format("Actualmente hay %d médicos registrados en el sistema.", totalMedicos),
                    null,
                    "informacion"
                );

            case "buscar_medico":
                String nombreBuscar = extraerNombreDeMensaje(mensaje);
                List<Medico> medicosEncontrados = medicoService.buscarPorNombre(nombreBuscar);
                if (medicosEncontrados.isEmpty()) {
                    return new ChatbotResponseDto(
                        "No se encontraron médicos con ese nombre.",
                        null,
                        "informacion"
                    );
                }
                return new ChatbotResponseDto(
                    String.format("He encontrado %d médico(s) con ese nombre.", medicosEncontrados.size()),
                    medicosEncontrados,
                    "medicos"
                );

            // ✅ NUEVO: Ver datos específicos de un médico
            case "ver_datos_medico":
                String nombreCompletoMedico = extraerNombreCompletoDeMensaje(mensaje, "medico");
                System.out.println("DEBUG - Nombre completo extraído: '" + nombreCompletoMedico + "'");
                
                List<Medico> medicosEspecificos;
                
                // Si tiene espacio, dividir en nombre y apellido
                if (nombreCompletoMedico.contains(" ")) {
                    String[] partes = nombreCompletoMedico.split("\\s+", 2);
                    String primerNombre = partes[0];
                    String apellido = partes[1];
                    
                    System.out.println("DEBUG - Buscando por apellido: '" + apellido + "'");
                    
                    // Buscar primero por apellido
                    medicosEspecificos = medicoService.buscarPorNombre(apellido);
                    
                    // Filtrar por nombre si encontró varios
                    if (medicosEspecificos.size() > 1) {
                        String nombreFinal = primerNombre;
                        medicosEspecificos = medicosEspecificos.stream()
                            .filter(m -> m.getNombre().toLowerCase().contains(nombreFinal.toLowerCase()))
                            .collect(Collectors.toList());
                    }
                } else {
                    // Si solo es una palabra, buscar normalmente
                    medicosEspecificos = medicoService.buscarPorNombre(nombreCompletoMedico);
                }
                
                if (medicosEspecificos.isEmpty()) {
                    return new ChatbotResponseDto(
                        String.format("No se encontró ningún médico con el nombre '%s'.", nombreCompletoMedico),
                        null,
                        "informacion"
                    );
                }
                
                // Si encontró exactamente uno, mostrar sus datos completos
                if (medicosEspecificos.size() == 1) {
                    Medico medico = medicosEspecificos.get(0);
                    return new ChatbotResponseDto(
                        String.format("📋 Datos del Dr. %s %s:\n" +
                            "• Especialidad: %s\n" +
                            "• Teléfono: %s\n" +
                            "• Estado: %s",
                            medico.getNombre(), 
                            medico.getApellido(),
                            medico.getEspecialidad(),
                            medico.getTelefono(),
                            medico.isActivo() ? "Activo ✅" : "Inactivo ❌"),
                        List.of(medico),
                        "medicos"
                    );
                }
                
                // Si encontró varios, mostrar la lista para que elija
                return new ChatbotResponseDto(
                    String.format("Se encontraron %d médicos con ese nombre. Aquí están los resultados:", 
                        medicosEspecificos.size()),
                    medicosEspecificos,
                    "medicos"
                );

            // Buscar médicos por especialidad
            case "buscar_por_especialidad":
                String especialidad = extraerEspecialidadDeMensaje(mensaje);
                
                System.out.println("DEBUG - Especialidad extraída: '" + especialidad + "'");
                
                if (especialidad.isEmpty()) {
                    especialidad = detectarEspecialidadDirecta(mensaje);
                    System.out.println("DEBUG - Especialidad detectada directamente: '" + especialidad + "'");
                }
                
                if (especialidad.isEmpty()) {
                    return new ChatbotResponseDto(
                        "No pude identificar la especialidad. Por favor, especifica la especialidad que buscas (ej: 'cardiología', 'pediatría', etc.)",
                        null,
                        "informacion"
                    );
                }
                
                List<Medico> medicosPorEspecialidad = medicoService.buscarPorEspecialidad(especialidad);
                
                System.out.println("DEBUG - Médicos encontrados: " + medicosPorEspecialidad.size());
                
                if (medicosPorEspecialidad.isEmpty()) {
                    return new ChatbotResponseDto(
                        String.format("No se encontraron médicos con la especialidad '%s'. " +
                            "Usa 'listar especialidades' para ver las disponibles.", especialidad),
                        null,
                        "informacion"
                    );
                }
                return new ChatbotResponseDto(
                    String.format("He encontrado %d médico(s) con la especialidad '%s'.", 
                        medicosPorEspecialidad.size(), especialidad),
                    medicosPorEspecialidad,
                    "medicos"
                );

            // Listar especialidades
            case "listar_especialidades":
                List<String> especialidades = medicoService.obtenerEspecialidades();
                return new ChatbotResponseDto(
                    String.format("Las especialidades disponibles son:\n%s", 
                        especialidades.stream()
                            .map(e -> "• " + e)
                            .collect(Collectors.joining("\n"))),
                    especialidades,
                    "informacion"
                );

            // ========== PACIENTES ==========
            case "listar_pacientes":
                List<Paciente> pacientes = pacienteService.getAllPacientes();
                return new ChatbotResponseDto(
                    String.format("He encontrado %d pacientes registrados.", pacientes.size()),
                    pacientes,
                    "pacientes"
                );

            case "contar_pacientes":
                int totalPacientes = pacienteService.getAllPacientes().size();
                return new ChatbotResponseDto(
                    String.format("Hay %d pacientes registrados en el sistema.", totalPacientes),
                    null,
                    "informacion"
                );

            case "buscar_paciente":
                String nombrePaciente = extraerNombreDeMensaje(mensaje);
                List<Paciente> pacientesEncontrados = pacienteService.buscarPorNombreOApellido(nombrePaciente);
                if (pacientesEncontrados.isEmpty()) {
                    return new ChatbotResponseDto(
                        "No se encontraron pacientes con ese nombre.",
                        null,
                        "informacion"
                    );
                }
                return new ChatbotResponseDto(
                    String.format("He encontrado %d paciente(s).", pacientesEncontrados.size()),
                    pacientesEncontrados,
                    "pacientes"
                );

            // ✅ NUEVO: Ver datos específicos de un paciente
            case "ver_datos_paciente":
                String nombreCompletoPaciente = extraerNombreCompletoDeMensaje(mensaje, "paciente");
                System.out.println("DEBUG - Nombre completo extraído: '" + nombreCompletoPaciente + "'");
                
                List<Paciente> pacientesEspecificos;
                
                // Si tiene espacio, dividir en nombre y apellido
                if (nombreCompletoPaciente.contains(" ")) {
                    String[] partes = nombreCompletoPaciente.split("\\s+", 2);
                    String primerNombre = partes[0];
                    String apellido = partes[1];
                    
                    System.out.println("DEBUG - Buscando por apellido: '" + apellido + "'");
                    
                    // Buscar primero por apellido
                    pacientesEspecificos = pacienteService.buscarPorNombreOApellido(apellido);
                    
                    // Filtrar por nombre si encontró varios
                    if (pacientesEspecificos.size() > 1) {
                        String nombreFinal = primerNombre;
                        pacientesEspecificos = pacientesEspecificos.stream()
                            .filter(p -> p.getNombre().toLowerCase().contains(nombreFinal.toLowerCase()))
                            .collect(Collectors.toList());
                    }
                } else {
                    // Si solo es una palabra, buscar normalmente
                    pacientesEspecificos = pacienteService.buscarPorNombreOApellido(nombreCompletoPaciente);
                }
                
                if (pacientesEspecificos.isEmpty()) {
                    return new ChatbotResponseDto(
                        String.format("No se encontró ningún paciente con el nombre '%s'.", nombreCompletoPaciente),
                        null,
                        "informacion"
                    );
                }
                
                // Si encontró exactamente uno, mostrar sus datos completos
                if (pacientesEspecificos.size() == 1) {
                    Paciente paciente = pacientesEspecificos.get(0);
                    return new ChatbotResponseDto(
                        String.format("📋 Datos de %s %s:\n" +
                            "• DNI: %s\n" +
                            "• Edad: %d años\n" +
                            "• Teléfono: %s\n" +
                            "• Email: %s",
                            paciente.getNombre(), 
                            paciente.getApellido(),
                            paciente.getDni(),
                            paciente.getEdad(),
                            paciente.getTelefono(),
                            paciente.getEmail()),
                        List.of(paciente),
                        "pacientes"
                    );
                }
                
                // Si encontró varios, mostrar la lista para que elija
                return new ChatbotResponseDto(
                    String.format("Se encontraron %d pacientes con ese nombre. Aquí están los resultados:", 
                        pacientesEspecificos.size()),
                    pacientesEspecificos,
                    "pacientes"
                );

            // Buscar paciente por DNI
            case "buscar_paciente_por_dni":
                String dni = extraerDniDeMensaje(mensaje);
                Optional<Paciente> pacientePorDni = pacienteService.getPacienteByDni(dni);
                if (pacientePorDni.isEmpty()) {
                    List<Paciente> pacientesParcial = pacienteService.buscarPorDni(dni);
                    if (pacientesParcial.isEmpty()) {
                        return new ChatbotResponseDto(
                            String.format("No se encontró ningún paciente con DNI '%s'.", dni),
                            null,
                            "informacion"
                        );
                    }
                    return new ChatbotResponseDto(
                        String.format("He encontrado %d paciente(s) con DNI similar a '%s'.", 
                            pacientesParcial.size(), dni),
                        pacientesParcial,
                        "pacientes"
                    );
                }
                return new ChatbotResponseDto(
                    String.format("Paciente encontrado: %s %s (DNI: %s)", 
                        pacientePorDni.get().getNombre(), 
                        pacientePorDni.get().getApellido(),
                        pacientePorDni.get().getDni()),
                    List.of(pacientePorDni.get()),
                    "pacientes"
                );

            // ========== CITAS ==========
            case "listar_citas":
                List<Citamedica> citas = citamedicaService.getAllCitamedicas();
                return new ChatbotResponseDto(
                    String.format("He encontrado %d citas médicas.", citas.size()),
                    citas,
                    "citas"
                );

            case "listar_citas_pendientes":
                List<Citamedica> citasPendientes = citamedicaService.getAllCitamedicas()
                    .stream()
                    .filter(c -> !c.getAtendida())
                    .collect(Collectors.toList());
                return new ChatbotResponseDto(
                    String.format("Hay %d citas pendientes por atender.", citasPendientes.size()),
                    citasPendientes,
                    "citas"
                );

            case "listar_citas_atendidas":
                List<Citamedica> citasAtendidas = citamedicaService.getAllCitamedicas()
                    .stream()
                    .filter(Citamedica::getAtendida)
                    .collect(Collectors.toList());
                return new ChatbotResponseDto(
                    String.format("Se han atendido %d citas.", citasAtendidas.size()),
                    citasAtendidas,
                    "citas"
                );

            case "contar_citas":
                int totalCitas = citamedicaService.getAllCitamedicas().size();
                return new ChatbotResponseDto(
                    String.format("Hay %d citas médicas registradas.", totalCitas),
                    null,
                    "informacion"
                );

            case "listar_citas_hoy":
                List<Citamedica> citasHoy = citamedicaService.buscarCitasDeHoy();
                return new ChatbotResponseDto(
                    String.format("Hay %d citas programadas para hoy.", citasHoy.size()),
                    citasHoy,
                    "citas"
                );

            case "buscar_citas_paciente":
                String nombrePacienteCita = extraerNombreDeMensaje(mensaje);
                List<Citamedica> citasPaciente = citamedicaService.buscarPorNombrePaciente(nombrePacienteCita);
                if (citasPaciente.isEmpty()) {
                    return new ChatbotResponseDto(
                        "No se encontraron citas para ese paciente.",
                        null,
                        "informacion"
                    );
                }
                return new ChatbotResponseDto(
                    String.format("He encontrado %d cita(s) para ese paciente.", citasPaciente.size()),
                    citasPaciente,
                    "citas"
                );

            case "buscar_citas_por_dni":
                String dniCita = extraerDniDeMensaje(mensaje);
                List<Citamedica> citasPorDni = citamedicaService.buscarPorDniPaciente(dniCita);
                if (citasPorDni.isEmpty()) {
                    return new ChatbotResponseDto(
                        String.format("No se encontraron citas para el paciente con DNI '%s'.", dniCita),
                        null,
                        "informacion"
                    );
                }
                return new ChatbotResponseDto(
                    String.format("He encontrado %d cita(s) para el paciente con DNI '%s'.", 
                        citasPorDni.size(), dniCita),
                    citasPorDni,
                    "citas"
                );

            case "buscar_citas_pendientes_paciente":
                String nombrePacientePendiente = extraerNombreDeMensaje(mensaje);
                List<Citamedica> citasPendientesPaciente = citamedicaService
                    .buscarPorNombrePaciente(nombrePacientePendiente)
                    .stream()
                    .filter(c -> !c.getAtendida())
                    .collect(Collectors.toList());
                if (citasPendientesPaciente.isEmpty()) {
                    return new ChatbotResponseDto(
                        "No se encontraron citas pendientes para ese paciente.",
                        null,
                        "informacion"
                    );
                }
                return new ChatbotResponseDto(
                    String.format("El paciente tiene %d cita(s) pendiente(s).", 
                        citasPendientesPaciente.size()),
                    citasPendientesPaciente,
                    "citas"
                );

            // ========== AYUDA ==========
            case "ayuda":
            default:
                return new ChatbotResponseDto(
                    "Puedo ayudarte con:\n\n" +
                    "📋 Médicos:\n" +
                    "• Listar médicos, médicos activos\n" +
                    "• Buscar médicos por nombre\n" +
                    "• Ver datos de un médico específico\n" +
                    "• Buscar médicos por especialidad\n" +
                    "• Listar especialidades disponibles\n\n" +
                    "👥 Pacientes:\n" +
                    "• Listar pacientes\n" +
                    "• Buscar pacientes por nombre\n" +
                    "• Ver datos de un paciente específico\n" +
                    "• Buscar pacientes por DNI\n\n" +
                    "📅 Citas:\n" +
                    "• Listar todas las citas\n" +
                    "• Ver citas pendientes o atendidas\n" +
                    "• Ver citas de hoy\n" +
                    "• Buscar citas de un paciente (por nombre o DNI)\n" +
                    "• Ver citas pendientes de un paciente\n\n" +
                    "¿Qué información necesitas?",
                    null,
                    "ayuda"
                );
        }
    }

    private String extraerNombreDeMensaje(String mensaje) {
        String[] palabrasClave = {"buscar", "encontrar", "médico", "doctor", "paciente", "llamado", "nombre", "de", "del"};
        String mensajeLower = mensaje.toLowerCase();
        
        for (String palabra : palabrasClave) {
            int index = mensajeLower.indexOf(palabra);
            if (index != -1) {
                String resto = mensaje.substring(index + palabra.length()).trim();
                if (!resto.isEmpty()) {
                    String[] palabras = resto.split("\\s+");
                    if (palabras.length > 0 && !palabras[0].isEmpty()) {
                        return palabras[0];
                    }
                }
            }
        }
        
        return "";
    }

    // ✅ NUEVO: Extraer nombre completo (nombre + apellido)
    private String extraerNombreCompletoDeMensaje(String mensaje, String tipo) {
        String mensajeLower = mensaje.toLowerCase();
        
        // Palabras clave según el tipo
        String[] palabrasClave;
        if (tipo.equals("medico")) {
            palabrasClave = new String[]{"datos de médico", "datos de medico", "datos del médico", 
                "datos del medico", "información de médico", "informacion de medico", 
                "información del médico", "informacion del medico"};
        } else {
            palabrasClave = new String[]{"datos de paciente", "datos del paciente", 
                "información de paciente", "informacion de paciente", 
                "información del paciente", "informacion del paciente"};
        }
        
        for (String palabraClave : palabrasClave) {
            int index = mensajeLower.indexOf(palabraClave);
            if (index != -1) {
                // Tomar el texto DESPUÉS de la palabra clave
                String resto = mensaje.substring(index + palabraClave.length()).trim();
                
                if (!resto.isEmpty()) {
                    // Tomar hasta 2 palabras (nombre + apellido)
                    String[] palabras = resto.split("\\s+");
                    
                    if (palabras.length >= 2) {
                        // Retornar nombre + apellido
                        return palabras[0] + " " + palabras[1];
                    } else if (palabras.length == 1) {
                        // Solo una palabra
                        return palabras[0];
                    }
                }
            }
        }
        
        return "";
    }

    private String extraerEspecialidadDeMensaje(String mensaje) {
        String mensajeLower = mensaje.toLowerCase();
        
        String[] palabrasClave = {"especialidad", "especialista", "de", "en", "médicos de", "doctores de"};
        
        for (String palabra : palabrasClave) {
            int index = mensajeLower.indexOf(palabra);
            if (index != -1) {
                String resto = mensajeLower.substring(index + palabra.length()).trim();
                
                if (!resto.isEmpty()) {
                    resto = resto.replaceFirst("^(la|el|los|las)\\s+", "");
                    
                    String[] palabras = resto.split("\\s+");
                    
                    StringBuilder especialidadExtraida = new StringBuilder();
                    int maxPalabras = Math.min(palabras.length, 3);
                    
                    for (int i = 0; i < maxPalabras; i++) {
                        String palabraActual = palabras[i];
                        
                        if (esPalabraDeParada(palabraActual)) {
                            break;
                        }
                        
                        if (especialidadExtraida.length() > 0) {
                            especialidadExtraida.append(" ");
                        }
                        especialidadExtraida.append(palabraActual);
                    }
                    
                    String resultado = especialidadExtraida.toString().trim();
                    if (!resultado.isEmpty()) {
                        return resultado;
                    }
                }
            }
        }
        
        return "";
    }

    // ✅ ACTUALIZADO: Con tus especialidades específicas
    private String detectarEspecialidadDirecta(String mensaje) {
        String mensajeLower = mensaje.toLowerCase();
        
        Map<String, String> especialidadesMap = new HashMap<>();
        
        // Cardiología
        especialidadesMap.put("cardiólogo", "Cardiología");
        especialidadesMap.put("cardiologo", "Cardiología");
        especialidadesMap.put("cardiología", "Cardiología");
        especialidadesMap.put("cardiologia", "Cardiología");
        
        // Psicología
        especialidadesMap.put("psicólogo", "Psicología");
        especialidadesMap.put("psicologo", "Psicología");
        especialidadesMap.put("psicología", "Psicología");
        especialidadesMap.put("psicologia", "Psicología");
        
        // Psiquiatría
        especialidadesMap.put("psiquiatra", "Psiquiatría");
        especialidadesMap.put("psiquiatría", "Psiquiatría");
        especialidadesMap.put("psiquiatria", "Psiquiatría");
        
        // Odontología
        especialidadesMap.put("odontólogo", "Odontología");
        especialidadesMap.put("odontologo", "Odontología");
        especialidadesMap.put("odontología", "Odontología");
        especialidadesMap.put("odontologia", "Odontología");
        especialidadesMap.put("dentista", "Odontología");
        
        // Patología
        especialidadesMap.put("patólogo", "Patología");
        especialidadesMap.put("patologo", "Patología");
        especialidadesMap.put("patología", "Patología");
        especialidadesMap.put("patologia", "Patología");
        
        // Endocrinología
        especialidadesMap.put("endocrinólogo", "Endocrinología");
        especialidadesMap.put("endocrinologo", "Endocrinología");
        especialidadesMap.put("endocrinología", "Endocrinología");
        especialidadesMap.put("endocrinologia", "Endocrinología");
        
        // Oftalmología
        especialidadesMap.put("oftalmólogo", "Oftalmología");
        especialidadesMap.put("oftalmologo", "Oftalmología");
        especialidadesMap.put("oftalmología", "Oftalmología");
        especialidadesMap.put("oftalmologia", "Oftalmología");
        especialidadesMap.put("oculista", "Oftalmología");
        
        // Oncología
        especialidadesMap.put("oncólogo", "Oncología");
        especialidadesMap.put("oncologo", "Oncología");
        especialidadesMap.put("oncología", "Oncología");
        especialidadesMap.put("oncologia", "Oncología");
        
        // Pediatría
        especialidadesMap.put("pediatra", "Pediatría");
        especialidadesMap.put("pediatría", "Pediatría");
        especialidadesMap.put("pediatria", "Pediatría");
        
        for (Map.Entry<String, String> entry : especialidadesMap.entrySet()) {
            if (mensajeLower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return "";
    }

    private boolean esPalabraDeParada(String palabra) {
        String[] palabrasDeParada = {
            "activo", "activos", "disponible", "disponibles",
            "hay", "tiene", "son", "están",
            "que", "con", "para", "por",
            "y", "o", "pero", "aunque"
        };
        
        for (String parada : palabrasDeParada) {
            if (palabra.equals(parada)) {
                return true;
            }
        }
        
        return false;
    }

    private String extraerDniDeMensaje(String mensaje) {
        String[] palabras = mensaje.split("\\s+");
        for (String palabra : palabras) {
            String numeros = palabra.replaceAll("[^0-9]", "");
            if (numeros.length() >= 7 && numeros.length() <= 9) {
                return numeros;
            }
        }
        
        String mensajeLower = mensaje.toLowerCase();
        int index = mensajeLower.indexOf("dni");
        if (index != -1) {
            String resto = mensaje.substring(index + 3).trim();
            String[] palabras2 = resto.split("\\s+");
            if (palabras2.length > 0) {
                return palabras2[0].replaceAll("[^0-9]", "");
            }
        }
        
        return "";
    }
}