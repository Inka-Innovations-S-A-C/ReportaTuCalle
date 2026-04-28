package com.reportatucalle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {
    private String email;
    private String password;
    private String nombre;
    private String tipo; // ciudadano, supervisor, admin
    private String telefono; // opcional
}
