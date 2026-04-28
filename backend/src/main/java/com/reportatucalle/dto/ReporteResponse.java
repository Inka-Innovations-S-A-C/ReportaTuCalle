package com.reportatucalle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private Long categoriaId;
    private String estado; // nuevo, revisado, asignado, en_proceso, resuelto, cerrado
    private String prioridad; // baja, media, alta, critica
    private LocalDateTime fechaCreacion;
    private Long usuarioId;
}
