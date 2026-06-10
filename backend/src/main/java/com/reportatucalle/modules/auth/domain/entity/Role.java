package com.reportatucalle.modules.auth.domain.entity;

/**
 * Enumeración de los roles disponibles en la plataforma ReportaTuCalle.
 * Define la jerarquía de permisos para Spring Security.
 */
public enum Role {
    CITIZEN,    // Ciudadano normal: crea y ve sus propios reportes
    SUPERVISOR, // Funcionario: ve el mapa de clustering y cambia estados
    ADMIN       // Administrador del sistema: gestiona categorías y configuración global
}