package product_store.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderResource implements OrderController {

    @Autowired
    private OrderService orderService;

    @Override
    public ResponseEntity<Void> create(OrderIn in, String idAccount) {
        // TODO: 1. criar uma order, baseado no codigo do usuario
        // chamar o orderservice
    
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<List<OrderOut>> healthCheck() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'healthCheck'");
    }

    @Override
    public ResponseEntity<List<OrderOut>> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public ResponseEntity<List<OrderOut>> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    

    // @Override
    // public ResponseEntity<Void> create(AccountIn in) {
    //     final Account a = accountService.create(
    //         AccountParser.to(in)
    //     );
    //     // returns a JSON in the HATEAOS standard.
    //     return ResponseEntity.created(
    //         ServletUriComponentsBuilder
    //             .fromCurrentRequest()
    //             .path("/{id}")
    //             .buildAndExpand(a.id())
    //             .toUri()
    //     ).build();
    // }

    // @Override
    // public ResponseEntity<Void> delete(String id) {
    //     accountService.delete(id);
    //     return ResponseEntity.noContent().build();
    // }

    // @Override
    // public ResponseEntity<Void> healthCheck() {
    //     return ResponseEntity.ok().build();
    // }

    // @Override
    // public ResponseEntity<List<AccountOut>> findAll() {
    //     return ResponseEntity.ok(
    //         AccountParser.to(
    //             accountService.findByAll()
    //         )
    //     );
    // }

    // @Override
    // public ResponseEntity<AccountOut> findById(String id) {
    //     Account out = accountService.findById(id);
    //     return out == null ?
    //         ResponseEntity.notFound().build() :
    //         ResponseEntity.ok(
    //             AccountParser.to(out) // transform from account to ou
    //         );
    // }

    // @Override
    // public ResponseEntity<AccountOut> findByEmailAndPassword(AccountIn in) {
    //     final Account out = accountService.findByEmailAndPassword(
    //         in.email(),
    //         in.password()
    //     );
    //     return out == null ?
    //         ResponseEntity.notFound().build() :
    //         ResponseEntity.ok(AccountParser.to(out));
    // }
    
}