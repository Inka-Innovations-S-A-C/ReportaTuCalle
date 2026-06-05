package com.reportatucalle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* 
  Punto de entrada principal de la aplicación ReportaTuCalle. Esta clase se encarga de iniciar la aplicación Spring Boot.
  Arquitectura: Monolito Modular que a futuro puede ser refactorizado a microservicios.
  Actualmente, todas las funcionalidades están integradas en una sola aplicación,
  pero se han diseñado módulos separados para facilitar la transición a microservicios en el futuro.
*/


@SpringBootApplication
public class ReportaTuCalleApp {
    public static void main(String[] args) {
        SpringApplication.run(ReportaTuCalleApp.class, args);
    }
}
