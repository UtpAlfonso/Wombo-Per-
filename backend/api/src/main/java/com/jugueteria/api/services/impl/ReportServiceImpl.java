package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.response.SalesReportResponse;
import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.repository.PedidoRepository;
import com.jugueteria.api.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PedidoRepository pedidoRepository;

    @Override
    public SalesReportResponse getSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // En un proyecto real, esto se optimizaría con una consulta JPQL o Criteria API
        // para no traer todos los pedidos a memoria.
        List<Pedido> pedidosEnRango = pedidoRepository.findAll().stream()
                .filter(p -> p.getEstado().equals("PROCESANDO") || p.getEstado().equals("ENVIADO") || p.getEstado().equals("ENTREGADO"))
                .filter(p -> !p.getFechaPedido().isBefore(startDateTime) && !p.getFechaPedido().isAfter(endDateTime))
                .toList();

        long numeroPedidos = pedidosEnRango.size();
        BigDecimal totalVentas = pedidosEnRango.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SalesReportResponse response = new SalesReportResponse();
        response.setFechaInicio(startDate);
        response.setFechaFin(endDate);
        response.setNumeroPedidos(numeroPedidos);
        response.setTotalVentas(totalVentas);

        return response;
    }
}