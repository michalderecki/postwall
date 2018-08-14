package pl.michalderecki.postwall.services;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.michalderecki.postwall.entity.Post;
import pl.michalderecki.postwall.entity.User;
import pl.michalderecki.postwall.repositories.PostRepository;
import pl.michalderecki.postwall.response.Status;
import pl.michalderecki.postwall.response.StatusCode;

@Service
public class PostService {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserService userService;

	public Status createPost(String message, String userName, LocalDateTime creationDate) {
		User user = userService.getUser(userName);
		Post post = new Post(message, creationDate, user);
		Status status = validatePost(post);
		if(status.getCode() == StatusCode.OK) {
			postRepository.save(post);
			status.setStatusMessage(String.format("Successfully saved post with id: %d by user: (id: %d, name: %s)", post.getId(), user.getId(), user.getName()));
		}
		return status;
	}
	
	public List<Post> getWallForUser(String userName) {
		return postRepository.findByUserNameDesc(userName);
	}
	
	public List<Post> getByFollowed(String userName) {
		return postRepository.findByFollowed(userName);
	}
	
	private Status validatePost(Post post) {
		if(post != null && StringUtils.isNotBlank(post.getMessage()) && post.getMessage().length() <= 140) {
			return new Status(StatusCode.OK);
		} else {
			return new Status(StatusCode.VALIDATION_FAILED, "Post message is empty or longer than 140 characters!");
		}
	}
	
}
