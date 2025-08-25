package com.jugueteria.api.repository;

import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca todos los pedidos realizados por un cliente específico.
     *
     * @param cliente El usuario (con rol CLIENT) cuyos pedidos se quieren encontrar.
     * @return una Lista de Pedidos.
     */
    List<Pedido> findByCliente(Usuario cliente);
}