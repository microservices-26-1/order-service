// package product_store.order;

// import java.util.List;

// import lombok.Builder;
// import lombok.Data;
// import lombok.experimental.Accessors;

// @Data
// @Builder @Accessors(chain = true, fluent = true)
// public class Order {

//     private String id;
//     private List<Item> items;
    
// }

package product_store.order;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private String id;

    @Column(name = "id_account", nullable = false)
    private String idAccount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Float total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Item> items;
}