package com.reportatucalle.modules.category.application.dto;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;

/**
 * DTO de entrada para crear o actualizar categorías.
 * 
 * Responsabilidad de la capa de Aplicación:
 * - Recibir datos del cliente (JSON)
 * - Validar formato básico (@Valid)
 * - Transformar a entidades de dominio para procesamiento
 */
public record CreateCategoryRequest(
        String name,
        String description,
        String markerColor,
        AlgorithmType algorithmType
) {}
