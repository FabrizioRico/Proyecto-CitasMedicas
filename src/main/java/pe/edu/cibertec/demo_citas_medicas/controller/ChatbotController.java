package pe.edu.cibertec.demo_citas_medicas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.demo_citas_medicas.dto.ChatbotRequestDto;
import pe.edu.cibertec.demo_citas_medicas.dto.ChatbotResponseDto;
import pe.edu.cibertec.demo_citas_medicas.service.ChatbotService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/consulta")
    public ResponseEntity<ChatbotResponseDto> procesarConsulta(@RequestBody ChatbotRequestDto request) {
        ChatbotResponseDto response = chatbotService.procesarConsulta(request.getMensaje());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot funcionando correctamente");
    }
}