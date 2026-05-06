package product_store.order;

import java.util.List;

public class OrderParser {

    public static AccountOut to(Order a) {
        return a == null ? null :
            AccountOut.builder()
                .id(a.id())
                .name(a.name())
                .email(a.email())
                .build();
    }
    
    public static List<AccountOut> to(List<Order> l) {
        return l.stream().map(OrderParser::to).toList();
    }

    public static Order to(AccountIn in) {
        return in == null ? null :
            Order.builder()
                .name(in.name())
                .email(in.email())
                .password(in.password())
                .build();
    }

}