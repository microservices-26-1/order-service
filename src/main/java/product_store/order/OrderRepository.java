package product_store.order;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderModel, String>  {

}