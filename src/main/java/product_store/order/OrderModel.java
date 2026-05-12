package product_store.order;

// Usado internamente para retornar a lista sem os itens
public record OrderModel(
    String id,
    String date,
    Float total
) {}