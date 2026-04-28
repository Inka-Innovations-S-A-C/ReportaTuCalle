package com.reportatucalle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequest {
    private String titulo;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private Long categoriaId;
    private String usuarioEmail; // opcional
}
