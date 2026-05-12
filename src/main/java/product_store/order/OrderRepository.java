package product_store.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByIdAccount(String idAccount);

    Optional<Order> findByIdAndIdAccount(String id, String idAccount);
}