// package product_store.order;

// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Base64;
// import java.util.List;
// import java.util.stream.StreamSupport;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.web.server.ResponseStatusException;

// @Service
// public class OrderService {

//     @Autowired
//     private OrderRepository accountRepository;

//     public Order create(Order account) {

//         if (account.password() == null || account.password().trim().length() == 0) {
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is empty");
//         }

//         account.passwordSha256(calcHash(account.password()));

//         return accountRepository.save(
//             new OrderModel(account)
//         ).to();
//     }

//     public void delete(String id) {
//         accountRepository.deleteById(id);
//     }

//     public Order findById(String id) {
//         return accountRepository.findById(id).orElse(null).to();
//     }

//     public List<Order> findByAll() {
//         return StreamSupport.stream(
//             accountRepository.findAll().spliterator(),
//             false // transform from stream to list
//         ).map(OrderModel::to) // parser from Model to Account
//         .toList();
//     }

//     public Order findByEmailAndPassword(String email, String password) {
//         String sha256 = calcHash(password);
//         return accountRepository.findByEmailAndPasswordSha256(email, sha256).orElse(null).to();
//     }

//     private String calcHash(String text) {
//         try {
//             MessageDigest md = MessageDigest.getInstance("SHA-256");
//             md.update(text.getBytes(StandardCharsets.UTF_8));
//             byte[] digest = md.digest();
//             return Base64.getEncoder().encodeToString(digest);
//         } catch (NoSuchAlgorithmException e) {
//             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//         }
//     }
    
// }

package product_store.order;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final ExchangeClient exchangeClient;
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
            // Valida produto na Product API — 400 se não existir
            product_store.product.ProductDTO product;
            try {
                product = productClient.findById(itemIn.idProduct());
            } catch (FeignException.NotFound e) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product not found: " + itemIn.idProduct()
                );
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

        Order saved = orderRepository.save(order);
        return orderParser.toOut(saved);
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

        // Sem currency ou USD: retorna sem conversão
        if (currency == null || currency.equalsIgnoreCase("USD")) {
            return orderParser.toOut(order, 1.0f);
        }

        // Chama Exchange API para converter de USD para a moeda pedida
        try {
            ExchangeClient.ExchangeResponse rate = exchangeClient.getRate("USD", currency.toUpperCase());
            return orderParser.toOut(order, rate.sell());
        } catch (FeignException e) {
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Currency not supported: " + currency
            );
        }
    }
}