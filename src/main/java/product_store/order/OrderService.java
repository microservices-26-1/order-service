package product_store.order;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import product_store.product.ProductController;
import product_store.product.ProductDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductController productController; // ← era ProductClient, agora usa o módulo product diretamente
    private final ExchangeClient exchangeClient;       // ← fica por enquanto, vira ExchangeController quando o colega terminar
    private final OrderParser orderParser;

    public OrderOut create(String idAccount, OrderIn orderIn) {
        Order order = Order.builder()
            .id(UUID.randomUUID().toString())
            .idAccount(idAccount)
            .date(LocalDateTime.now())
            .build();

        List<Item> items = new ArrayList<>();
        float orderTotal = 0f;

        for (OrderItemIn itemIn : orderIn.items()) {
            // ProductController.findById retorna ResponseEntity<ProductDTO>, por isso o .getBody()
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

            float itemTotal = product.price() * itemIn.quantity();
            orderTotal += itemTotal;

            items.add(Item.builder()
                .id(UUID.randomUUID().toString())
                .order(order)
                .idProduct(product.id())
                .quantity(itemIn.quantity())
                .total(Math.round(itemTotal * 100f) / 100f)
                .build());
        }

        order.setTotal(Math.round(orderTotal * 100f) / 100f);
        order.setItems(items);

        return orderParser.toOut(orderRepository.save(order));
    }

    public List<OrderOut> findAll(String idAccount) {
        return orderRepository.findByIdAccount(idAccount)
            .stream()
            .map(orderParser::toOutSimple)
            .toList();
    }

    @SuppressWarnings("deprecation")
    public OrderOut findById(String id, String idAccount, String currency) {
        Order order = orderRepository.findByIdAndIdAccount(id, idAccount)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Order not found"
            ));

        if (currency == null || currency.equalsIgnoreCase("USD")) {
            return orderParser.toOut(order, 1.0f);
        }

        try {
            ExchangeClient.ExchangeResponse rate = exchangeClient.getRate("USD", currency.toUpperCase());
            return orderParser.toOut(order, rate.sell());
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "Currency not supported: " + currency);
        }
    }
}