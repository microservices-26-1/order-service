package product_store.order;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_item", schema = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;

    @Column(name = "id_product", nullable = false)
    private String idProduct;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Float total;
}