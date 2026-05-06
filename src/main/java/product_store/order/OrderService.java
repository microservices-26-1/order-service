package product_store.order;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    @Autowired
    private OrderRepository accountRepository;

    public Order create(Order account) {

        if (account.password() == null || account.password().trim().length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is empty");
        }

        account.passwordSha256(calcHash(account.password()));

        return accountRepository.save(
            new OrderModel(account)
        ).to();
    }

    public void delete(String id) {
        accountRepository.deleteById(id);
    }

    public Order findById(String id) {
        return accountRepository.findById(id).orElse(null).to();
    }

    public List<Order> findByAll() {
        return StreamSupport.stream(
            accountRepository.findAll().spliterator(),
            false // transform from stream to list
        ).map(OrderModel::to) // parser from Model to Account
        .toList();
    }

    public Order findByEmailAndPassword(String email, String password) {
        String sha256 = calcHash(password);
        return accountRepository.findByEmailAndPasswordSha256(email, sha256).orElse(null).to();
    }

    private String calcHash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
}