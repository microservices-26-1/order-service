package product_store.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "accounts")
@Setter @Accessors(chain = true, fluent = true)
@NoArgsConstructor @AllArgsConstructor
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password_sha256")
    private String passwordSha256;

    public OrderModel(Order a) {
        this.id = a.id();
        this.name = a.name();
        this.email = a.email();
        this.passwordSha256 = a.passwordSha256();
    }

    public Order to() {
        return Order.builder()
            .id(this.id)
            .name(this.name)
            .email(this.email)
            .passwordSha256(this.passwordSha256)
            .build();
    }
    
}