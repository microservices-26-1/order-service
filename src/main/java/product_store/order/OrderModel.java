package product_store.order;

import java.math.BigDecimal;

// Usado internamente para retornar a lista sem os itens
public record OrderModel(
    String id,
    String date,
    BigDecimal total
) {}