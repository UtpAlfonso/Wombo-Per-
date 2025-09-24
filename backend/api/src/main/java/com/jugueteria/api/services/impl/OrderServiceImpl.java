package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.request.OrderRequest;
import com.jugueteria.api.dto.request.PaymentRequest;
import com.jugueteria.api.dto.response.OrderResponse;
import com.jugueteria.api.dto.response.PaymentResponse;
import com.jugueteria.api.entity.*;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.*;
import com.jugueteria.api.services.OrderService;
import com.jugueteria.api.services.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final PedidoRepository pedidoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(Usuario usuario, OrderRequest request) {
        List<CarritoItem> cartItems = carritoItemRepository.findByUsuario(usuario);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        BigDecimal total = calculateTotal(cartItems);

        Pedido pedido = new Pedido();
        pedido.setCliente(usuario);
        pedido.setEstado("PENDIENTE_PAGO");
        pedido.setTotal(total);
        pedido.setDireccionEnvio(request.getDireccionEnvio());
        
        List<DetallePedido> detalles = cartItems.stream().map(cartItem -> {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(cartItem.getProducto());
            detalle.setCantidad(cartItem.getCantidad());
            detalle.setPrecioUnitario(cartItem.getProducto().getPrecio());
            return detalle;
        }).collect(Collectors.toList());
        pedido.setDetalles(detalles);
        
        Pedido savedPedido = pedidoRepository.save(pedido);

        PaymentRequest paymentRequest = PaymentRequest.builder()
        .amount(total)
        .cardToken(request.getPaymentToken())
        .build();

PaymentResponse paymentResponse = paymentGatewayService.processPayment(paymentRequest);

        if (paymentResponse.isSuccess()) {
            savedPedido.setEstado("PROCESANDO");
            updateStock(cartItems);
            
            Pago pago = Pago.builder()
                .pedido(savedPedido)
                .idTransaccionExterna(paymentResponse.getTransactionId())
                .estado(paymentResponse.getStatus())
                .monto(total)
                .build();
            pagoRepository.save(pago);
            
            carritoItemRepository.deleteByUsuario(usuario);
        } else {
            savedPedido.setEstado("PAGO_FALLIDO");
        }
        
        Pedido finalPedido = pedidoRepository.save(savedPedido);
        return modelMapper.map(finalPedido, OrderResponse.class);
    }

    @Override
    public List<OrderResponse> findAll() {
        return pedidoRepository.findAll().stream()
                .map(p -> modelMapper.map(p, OrderResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> findByUsuario(Usuario usuario) {
        return pedidoRepository.findByCliente(usuario).stream()
                .map(p -> modelMapper.map(p, OrderResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse findByIdForUser(Long orderId, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));
        
        boolean isAdminOrWorker = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_WORKER"));
        
        if (!isAdminOrWorker && !pedido.getCliente().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No tienes permiso para ver este pedido.");
        }
        
        return modelMapper.map(pedido, OrderResponse.class);
    }
    
    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));
        pedido.setEstado(status);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        return modelMapper.map(updatedPedido, OrderResponse.class);
    }
    
    private void updateStock(List<CarritoItem> items) {
        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            int newStock = producto.getStock() - item.getCantidad();
            if (newStock < 0) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            producto.setStock(newStock);
            productoRepository.save(producto);
        }
    }

    private BigDecimal calculateTotal(List<CarritoItem> items) {
        return items.stream()
                .map(item -> item.getProducto().getPrecio().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}