package product_store.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderOut> create(
        @RequestBody OrderIn orderIn,
        @RequestHeader("id-account") String idAccount
    ) {
        OrderOut result = orderService.create(idAccount, orderIn);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<OrderOut>> findAll(
        @RequestHeader("id-account") String idAccount
    ) {
        return ResponseEntity.ok(orderService.findAll(idAccount));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderOut> findById(
        @PathVariable String id,
        @RequestHeader("id-account") String idAccount,
        @RequestParam(required = false) String currency
    ) {
        return ResponseEntity.ok(orderService.findById(id, idAccount, currency));
    }
}