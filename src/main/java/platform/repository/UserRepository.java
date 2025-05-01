package platform.repository;

import entities.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByDocument(String document);
    void deleteByDocument(String document);
    User findByEmail(String email);
}
