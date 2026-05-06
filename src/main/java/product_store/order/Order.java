package product_store.order;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder @Accessors(chain = true, fluent = true)
public class Order {

    private String id;
    private List<Item> items;
    
}