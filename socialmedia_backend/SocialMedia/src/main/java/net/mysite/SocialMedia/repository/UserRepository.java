package net.mysite.SocialMedia.repository;

import net.mysite.SocialMedia.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE NOT EXISTS (SELECT uf FROM Friend uf WHERE (uf.user = :user AND uf.requestedUser = u OR uf.requestedUser = :user AND uf.user = u))")
    Set<User> findUserByNonFriends(User user);
    @Query("SELECT u FROM User u WHERE ((u.firstname || ' ' || u.lastname) = :searchTerm OR u.username = :searchTerm OR u.firstname = :searchTerm OR u.lastname = :searchTerm) AND u.id <> :userId")
    Set<User> findUserByNameOrEmail(String searchTerm, Long userId);
    @Query("SELECT u FROM User u WHERE u.Identification = :Identification")
    Optional<User> findByIdentification(String Identification);

}
