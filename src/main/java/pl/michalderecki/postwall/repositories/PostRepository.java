package pl.michalderecki.postwall.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pl.michalderecki.postwall.entity.Post;

@Transactional
public interface PostRepository extends JpaRepository<Post, Long> {
	
	@Query("select p from Post p where p.user.name = :userName order by p.creationDate desc")
	public List<Post> findByUserNameDesc(@Param("userName") String userName);
	
	@Query("select p from Post p where p.user.id in (select f.id from User u join u.followedUsers f where u.name = :userName) order by p.creationDate desc")
	public List<Post> findByFollowed(@Param("userName") String userName);

}
