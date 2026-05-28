package product_store.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "exchange-service",
    url = "${exchange.service.url:http://exchange-service:8080}"
)
public interface ExchangeClient {

    @GetMapping("/exchanges/{from}/{to}")
    ExchangeResponse getRate(
        @PathVariable String from,
        @PathVariable String to
    );

    record ExchangeResponse(
        Float sell,
        Float buy,
        String date,
        String idAccount
    ) {}
}