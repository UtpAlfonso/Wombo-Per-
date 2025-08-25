package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;

    // Token de la pasarela de pago, no los datos de la tarjeta.
    @NotBlank(message = "El token de pago es obligatorio para procesar la orden")
    private String paymentToken;
}