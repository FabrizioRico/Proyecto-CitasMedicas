package pe.edu.cibertec.demo_citas_medicas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pacienteid;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no debe superar 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no debe superar 50 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 200, message = "El email no debe superar 200 caracteres")
    private String email;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 1, message = "Edad mínima 1")
    @Max(value = 120, message = "Edad máxima 120")
    private Integer edad;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 7, max = 15, message = "El teléfono debe tener entre 7 y 15 dígitos")
    private String telefono;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 15, message = "El DNI no debe superar 15 caracteres")
    private String dni;
}
