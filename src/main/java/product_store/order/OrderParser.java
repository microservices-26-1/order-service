package product_store.order;

import org.springframework.stereotype.Component;

import product_store.product.ProductDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class OrderParser {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Converte Order → OrderOut com conversão de moeda
    public OrderOut toOut(Order order, Float conversionRate) {
        List<OrderItemOut> itemOuts = order.getItems().stream()
            .map(item -> toItemOut(item, conversionRate))
            .toList();

        return OrderOut.builder()
            .id(order.getId())
            .date(order.getDate().format(FMT))
            .items(itemOuts)
            .total(round(order.getTotal() * conversionRate))
            .build();
    }

    // Sem conversão (USD padrão)
    public OrderOut toOut(Order order) {
        return toOut(order, 1.0f);
    }

    // Listagem simples sem itens
    public OrderOut toOutSimple(Order order) {
        return OrderOut.builder()
            .id(order.getId())
            .date(order.getDate().format(FMT))
            .total(order.getTotal())
            .build();
    }

    private OrderItemOut toItemOut(Item item, Float conversionRate) {
        return OrderItemOut.builder()
            .id(item.getId())
            .product(ProductDTO.builder().id(item.getIdProduct()).build())
            .quantity(item.getQuantity())
            .total(round(item.getTotal() * conversionRate))
            .build();
    }

    private Float round(Float value) {
        return Math.round(value * 100.0f) / 100.0f;
    }
}

