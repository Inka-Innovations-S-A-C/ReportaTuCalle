package com.reportatucalle.modules.category.domain.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * Entidad de Dominio: Category (100% Java Puro)
 * 
 * Esta clase es un POJO de dominio completamente agnóstico a la persistencia.
 * - SIN anotaciones JPA (@Entity, @Table, @Column, etc.)
 * - SIN anotaciones Jackson
 * - SIN referencias a Spring Data
 * - SOLO lógica de negocio pura
 * 
 * El constructor privado y la falta de Setters protegen la integridad del objeto.
 * Usa Lombok (@Builder) solo para construcción, manteniendo inmutabilidad.
 */
@Getter
@Builder
public class Category {
    
    // Identidad
    private final Long id;
    
    // Atributos de Dominio
    private final String name;
    private final String description;
    private final String markerColor;
    
    // Enum de Algoritmo: Define qué motor computacional ejecutar
    // para resolver reportes de esta categoría
    private final AlgorithmType algorithmType;
    
    // Metadatos
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    // ==================== MÉTODOS DE LÓGICA DE NEGOCIO ====================
    
    /**
     * Determina si esta categoría requiere un algoritmo de enrutamiento.
     * Usado para problemas como TSP/VRP (baches, basura, etc.)
     */
    public boolean requiresRouting() {
        return this.algorithmType == AlgorithmType.ROUTING;
    }
    
    /**
     * Determina si esta categoría requiere un algoritmo de flujo máximo.
     * Usado para problemas de flujo (fugas de agua, etc.)
     */
    public boolean requiresFlow() {
        return this.algorithmType == AlgorithmType.FLOW;
    }
    
    /**
     * Determina si esta categoría requiere un árbol de expansión mínima.
     * Usado para problemas de conectividad (semáforos, postes, etc.)
     */
    public boolean requiresConnectivity() {
        return this.algorithmType == AlgorithmType.CONNECTIVITY;
    }
    
    /**
     * Determina si esta categoría no requiere algoritmo específico.
     * Solo se visualiza en el mapa sin procesamiento algorítmico.
     */
    public boolean requiresNoAlgorithm() {
        return this.algorithmType == AlgorithmType.NONE;
    }
    
    /**
     * Determina si la categoría está disponible para usar.
     */
    public boolean isAvailable() {
        return this.isActive != null && this.isActive;
    }
}