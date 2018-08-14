/**
 * 
 */
package pl.michalderecki.postwall.rest;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.michalderecki.postwall.entity.Post;
import pl.michalderecki.postwall.request.CreatePostRequestBody;
import pl.michalderecki.postwall.request.FollowRequestBody;
import pl.michalderecki.postwall.response.Status;
import pl.michalderecki.postwall.response.StatusCode;
import pl.michalderecki.postwall.services.PostService;
import pl.michalderecki.postwall.services.UserService;

/**
 * @author Michal
 *
 */
@RestController
public class PostWallRestService {
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private UserService userService;

	@RequestMapping("/wall/{userName}")
	@ResponseBody
	public List<Post> getWallForUser(@PathVariable("userName") String userName) {
		return postService.getWallForUser(userName);
	}
	
	@RequestMapping(value = "/follow", method = RequestMethod.PUT)
	@ResponseBody
	public Status follow(@RequestBody(required = true) @Valid FollowRequestBody body) {
		try {
			return userService.follow(body.getUserName(), body.getFollowedUserName());
		} catch (Exception e) {
			return new Status(StatusCode.ERROR, e.getMessage());
		}
	}
	
	@RequestMapping("/timeline/{userName}")
	@ResponseBody
	public List<Post> getTimeline(@PathVariable("userName") String userName) {
		return postService.getByFollowed(userName);
	}
	
	@RequestMapping(value = "/createPost", method = RequestMethod.PUT)
	@ResponseBody
	public Status createPost(@RequestBody(required = true) @Valid CreatePostRequestBody body) {
		try {
			return postService.createPost(body.getMessage(), body.getUserName(), LocalDateTime.now());
		} catch (Exception e) {
			return new Status(StatusCode.ERROR, e.getMessage());
		}
	}
	
	
	
}
