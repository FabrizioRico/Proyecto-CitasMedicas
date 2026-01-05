package pe.edu.cibertec.demo_citas_medicas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "citamedica")
public class Citamedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer citaid;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotNull(message = "Paciente obligatorio")
    @ManyToOne
    @JoinColumn(name = "pacienteid")
    private Paciente pacienterelacion;

    @NotNull(message = "Médico obligatorio")
    @ManyToOne
    @JoinColumn(name = "medicoid")
    private Medico medicorelacion;

    @Column(nullable = false)
    private Boolean atendida = false;
}
