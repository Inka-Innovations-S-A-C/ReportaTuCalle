package com.reportatucalle.modules.category.domain.entity;

/**
 * Define qué motor matemático de Ciencia de la Computación 
 * se ejecutará para resolver los reportes de una categoría.
 */
public enum AlgorithmType {
    ROUTING, //Usa TSP/VRP (Por ejemplo para Baches o basura)
    FLOW, //Usa flujo maximo (Por ejemplo para fugas de agua)
    CONNECTIVITY, //Usa un arbol de expansion minima (Para semaforos o Postes)
    NONE //No requiere un algoritmo, pero sí que se muestra en el mapa
}
