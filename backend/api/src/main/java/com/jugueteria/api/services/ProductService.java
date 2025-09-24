package com.jugueteria.api.services;
import com.jugueteria.api.dto.request.ProductRequest;
import com.jugueteria.api.dto.response.ProductResponse;
import java.util.List;
public interface ProductService {
    List<ProductResponse> findAll();
    ProductResponse findById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void deleteById(Long id);
}
