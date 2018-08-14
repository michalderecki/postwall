/**
 * 
 */
package pl.michalderecki.postwall.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.michalderecki.postwall.entity.User;
import pl.michalderecki.postwall.repositories.UserRepository;
import pl.michalderecki.postwall.response.Status;
import pl.michalderecki.postwall.response.StatusCode;

/**
 * @author Michal
 *
 */
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public User getUser(String userName, boolean withFollowed) {
		Optional<User> userFromDB = withFollowed ? userRepository.findByNameWithFollowed(userName) : userRepository.findByName(userName);
		if(userFromDB.isPresent()) {
			return userFromDB.get();
		} else {
			User user = new User(userName);
			return userRepository.save(user);
		}
	}
	
	public User getUser(String userName) {
		return getUser(userName, false);
	}
	
	public Status follow(String userName, String followedUserName) {
		User user = getUser(userName, true);
		User followedUser = getUser(followedUserName);
		if(checkIfAlreadyFollow(user, followedUser)) {
			return new Status(StatusCode.VALIDATION_FAILED, String.format("user: %s is already following user: %s", user.toString(), followedUser.toString()));
		} else {
			user.getFollowedUsers().add(followedUser);
			userRepository.save(user);
			return new Status(StatusCode.OK, String.format("user: %s is now following user: %s", user.toString(), followedUser.toString()));
		}
	}
	
	private boolean checkIfAlreadyFollow(User user, User followedUser) {
		return user.getFollowedUsers().contains(followedUser);
	}
}
