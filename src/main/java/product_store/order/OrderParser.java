package product_store.order;

import org.springframework.stereotype.Component;
import product_store.product.ProductDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class OrderParser {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public OrderOut toOut(Order order, BigDecimal conversionRate) {
        List<OrderItemOut> itemOuts = order.getItems().stream()
            .map(item -> toItemOut(item, conversionRate))
            .toList();

        return OrderOut.builder()
            .id(order.getId())
            .date(order.getDate().format(FMT))
            .items(itemOuts)
            .total(round(order.getTotal().multiply(conversionRate)))
            .build();
    }

    public OrderOut toOut(Order order) {
        return toOut(order, BigDecimal.ONE);
    }

    public OrderOut toOutSimple(Order order) {
        return OrderOut.builder()
            .id(order.getId())
            .date(order.getDate().format(FMT))
            .total(round(order.getTotal()))
            .build();
    }

    private OrderItemOut toItemOut(Item item, BigDecimal conversionRate) {
        return OrderItemOut.builder()
            .id(item.getId())
            .product(ProductDTO.builder().id(item.getIdProduct()).build())
            .quantity(item.getQuantity())
            .total(round(item.getTotal().multiply(conversionRate)))
            .build();
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}