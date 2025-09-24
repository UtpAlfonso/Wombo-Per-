package com.jugueteria.api.services;
import com.jugueteria.api.dto.request.OrderRequest;
import com.jugueteria.api.dto.response.OrderResponse;
import com.jugueteria.api.entity.Usuario;
import java.util.List;
public interface OrderService {
    OrderResponse createOrder(Usuario usuario, OrderRequest request);
    List<OrderResponse> findAll();
    List<OrderResponse> findByUsuario(Usuario usuario);
    OrderResponse findByIdForUser(Long orderId, Usuario usuario);
    OrderResponse updateStatus(Long orderId, String status);
}