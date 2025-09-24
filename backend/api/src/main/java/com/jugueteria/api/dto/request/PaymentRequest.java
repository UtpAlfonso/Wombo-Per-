package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "El monto a pagar es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;

    @NotBlank(message = "El token de la tarjeta generado por la pasarela de pago es obligatorio")
    private String cardToken;
    
    // Opcional: podrías incluir aquí otros datos que la pasarela requiera,
    // como la moneda, descripción, etc.
    private String currency = "PEN"; // Moneda por defecto
    private String description = "Pago en Tienda de Juguetes";
}