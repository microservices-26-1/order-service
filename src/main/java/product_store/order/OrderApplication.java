package product_store.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import product_store.product.ProductController;

@SpringBootApplication
@EnableFeignClients(clients = {
    ProductController.class
    // ExchangeController.class  → adicionar quando o colega terminar o módulo exchange
})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}