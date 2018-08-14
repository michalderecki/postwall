package pl.michalderecki.postwall.repositories;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pl.michalderecki.postwall.entity.User;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByName(String name);
	
	@Query("select u from User u join fetch u.followedUsers where u.name = :userName")
	public Optional<User> findByNameWithFollowed(@Param("userName") String name);
}
