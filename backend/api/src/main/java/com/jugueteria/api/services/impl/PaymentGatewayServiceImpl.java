package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.request.PaymentRequest;
import com.jugueteria.api.dto.response.PaymentResponse;
import com.jugueteria.api.services.PaymentGatewayService;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {
    
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // --- AQUÍ IRÍA LA LÓGICA REAL DE INTEGRACIÓN CON STRIPE, MERCADOPAGO, ETC. ---
        // Se usaría RestTemplate o WebClient para llamar a la API externa.
        
        System.out.println("Procesando pago de " + request.getAmount() + " con token " + request.getCardToken());

        // Simulación de una respuesta de la pasarela de pagos
        boolean paymentSuccess = Math.random() > 0.1; // 90% de probabilidad de éxito

        if (paymentSuccess) {
            return new PaymentResponse(true, "approved", "txn_" + UUID.randomUUID().toString());
        } else {
            return new PaymentResponse(false, "declined", "txn_" + UUID.randomUUID().toString());
        }
    }
}