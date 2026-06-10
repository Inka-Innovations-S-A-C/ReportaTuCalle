package com.reportatucalle.modules.report.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        @NotNull(message = "El tipo de incidente es obligatorio")
        Long categoryId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede exceder los 150 caracteres")
        String title,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotNull(message = "La latitud es obligatoria")
        Double latitude,

        @NotNull(message = "La longitud es obligatoria")
        Double longitude,

        // NUEVO: La URL del archivo alojado en la nube (AWS, Cloudinary, etc.)
        @Size(max = 500, message = "La URL de la imagen es demasiado larga")
        String imageUrl
) {}