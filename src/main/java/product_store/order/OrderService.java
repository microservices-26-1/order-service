package product_store.order;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import product_store.product.ProductController;
import product_store.product.ProductDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductController productController;
    private final ExchangeClient exchangeClient;
    private final OrderParser orderParser;

    public OrderOut create(String idAccount, OrderIn orderIn) {
        Order order = Order.builder()
            .id(UUID.randomUUID().toString())
            .idAccount(idAccount)
            .date(LocalDateTime.now())
            .build();

        List<Item> items = new ArrayList<>();
        BigDecimal orderTotal = BigDecimal.ZERO;

        for (OrderItemIn itemIn : orderIn.items()) {
            ProductDTO product;
            try {
                ResponseEntity<ProductDTO> response = productController.findById(itemIn.idProduct());
                product = response.getBody();
                if (product == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Product not found: " + itemIn.idProduct());
                }
            } catch (FeignException.NotFound e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product not found: " + itemIn.idProduct());
            }

            BigDecimal price = BigDecimal.valueOf(product.price());
            BigDecimal itemTotal = price
                .multiply(BigDecimal.valueOf(itemIn.quantity()))
                .setScale(2, RoundingMode.HALF_UP);

            orderTotal = orderTotal.add(itemTotal);

            items.add(Item.builder()
                .id(UUID.randomUUID().toString())
                .order(order)
                .idProduct(product.id())
                .quantity(itemIn.quantity())
                .total(itemTotal)
                .build());
        }

        order.setTotal(orderTotal.setScale(2, RoundingMode.HALF_UP));
        order.setItems(items);

        return orderParser.toOut(orderRepository.save(order));
    }

    public List<OrderOut> findAll(String idAccount) {
        return orderRepository.findByIdAccount(idAccount)
            .stream()
            .map(orderParser::toOutSimple)
            .toList();
    }

    public OrderOut findById(String id, String idAccount, String currency) {
        Order order = orderRepository.findByIdAndIdAccount(id, idAccount)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Order not found"
            ));

        if (currency == null || currency.equalsIgnoreCase("USD")) {
            return orderParser.toOut(order, BigDecimal.ONE);
        }

        try {
            ExchangeClient.ExchangeResponse rate = exchangeClient.getRate("USD", currency.toUpperCase());
            return orderParser.toOut(order, BigDecimal.valueOf(rate.sell()));
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(422),
                "Currency not supported: " + currency);
        }
    }
}