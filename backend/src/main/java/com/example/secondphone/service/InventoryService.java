package com.example.secondphone.service;

import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.dto.*;
import com.example.secondphone.entity.*;
import com.example.secondphone.exception.BusinessException;
import com.example.secondphone.repository.*;

@Service
public class InventoryService {
    private final InventoryMovementRepository movements;
    private final ProductCatalogService catalog;
    private final ProductRepository products;
    public InventoryService(InventoryMovementRepository movements, ProductCatalogService catalog, ProductRepository products) {
        this.movements = movements; this.catalog = catalog; this.products = products;
    }
    @Transactional
    public InventoryMovementResponse adjust(Long productId, InventoryAdjustmentRequest request, Authentication authentication) {
        Product product = catalog.getEntity(productId); int before = product.getStock() == null ? 0 : product.getStock();
        int raw = request.quantity();
        int change = switch (request.type()) {
            case IN -> Math.abs(raw);
            case OUT -> -Math.abs(raw);
            case ADJUSTMENT -> raw;
        };
        if (change == 0) throw new BusinessException("庫存異動數量不可為 0");
        int after = before + change;
        if (after < 0) throw new BusinessException("庫存不足，異動後不可小於 0");
        if (after < (product.getReservedStock() == null ? 0 : product.getReservedStock())) throw new BusinessException("庫存不可低於保留庫存");
        product.setStock(after);
        if (after == 0 && ProductStatus.ACTIVE.name().equals(product.getStatus())) product.setStatus(ProductStatus.OUT_OF_STOCK.name());
        if (after > 0 && ProductStatus.OUT_OF_STOCK.name().equals(product.getStatus())) product.setStatus(ProductStatus.ACTIVE.name());
        products.save(product);
        InventoryMovement movement = new InventoryMovement(); movement.setProduct(product); movement.setType(request.type());
        movement.setChangeQuantity(change); movement.setBeforeStock(before); movement.setAfterStock(after); movement.setReason(request.reason().trim());
        movement.setOperatorUsername(authentication == null ? "system" : authentication.getName());
        return map(movements.save(movement));
    }
    @Transactional(readOnly = true)
    public PagedResponse<InventoryMovementResponse> findAll(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InventoryMovement> result = productId == null ? movements.findAll(pageable) : movements.findByProductId(productId, pageable);
        return PagedResponse.from(result.map(this::map));
    }
    private InventoryMovementResponse map(InventoryMovement m) {
        Product p = m.getProduct(); return new InventoryMovementResponse(m.getId(), p.getId(), p.getProductCode(), p.getProductName(), m.getType(),
                m.getChangeQuantity(), m.getBeforeStock(), m.getAfterStock(), m.getReason(), m.getOperatorUsername(), m.getCreatedAt());
    }
}
