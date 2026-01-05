package pe.edu.cibertec.demo_citas_medicas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponseDto {
    private String respuesta;
    private Object datos; // Puede contener listas de médicos, pacientes o citas
    private String tipo; // "medicos", "pacientes", "citas", "informacion"
}
