package pl.michalderecki.postwall;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import pl.michalderecki.postwall.entity.Post;
import pl.michalderecki.postwall.repositories.PostRepository;
import pl.michalderecki.postwall.response.StatusCode;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostwallApplicationTests {

	@Autowired
    private MockMvc mvc;
	
	@Autowired
	private PostRepository postRepository;
	
	@Test
	public void shouldCreatePost() throws Exception {
		createPost("testUser", "Jakaś taka wiadomość", StatusCode.OK, "Successfully saved post with id: 2 by user: (id: 1, name: testUser)");
		assertEquals(1, postRepository.count());
	}
	
	@Test
	public void shouldNotCreatePostValidationError() throws Exception {
		createPost("testUser", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 
				StatusCode.VALIDATION_FAILED, "Post message is empty or longer than 140 characters!");
	}
	
	@Test
	public void shouldFallowUser() throws Exception {
		follow("following", "testUser", StatusCode.OK, "user: [id=3, name=following] is now following user: [id=1, name=testUser]");
	}
	
	@Test
	public void shouldNotFallowUserValidationFailed() throws Exception {
		follow("following", "testUser", StatusCode.VALIDATION_FAILED, "user: [id=3, name=following] is already following user: [id=1, name=testUser]");
	}
	
	@Test
	public void shouldHaveEmptyWall() throws Exception {
		mvc.perform(get("/wall/emptyWallUser")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(content().string("[]"));
	}
	
	@Test
	public void shouldHaveNotEmptyWall() throws Exception {
		mvc.perform(get("/wall/testUser")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$[0].message", is("Jakaś taka wiadomość")))
			      .andExpect(jsonPath("$[0].user.name", is("testUser")));
		
		createPost("testUser", "test message", StatusCode.OK, "Successfully saved post with id: 7 by user: (id: 1, name: testUser)");
		
		mvc.perform(get("/wall/testUser")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$[0].message", is("test message")))
			      .andExpect(jsonPath("$[0].user.name", is("testUser")))
			      .andExpect(jsonPath("$[1].message", is("Jakaś taka wiadomość")))
			      .andExpect(jsonPath("$[1].user.name", is("testUser")));
		List<Post> posts = postRepository.findByUserNameDesc("testUser");
		assertEquals(2, posts.size());
		assertTrue(posts.get(0).getCreationDate().isAfter(posts.get(1).getCreationDate()));
		
	}
	
	@Test
	public void shouldHaveEmptyTimeline() throws Exception {
		mvc.perform(get("/timeline/emptyTimelineUser")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(content().string("[]"));
	}
	
	@Test
	public void shouldHaveNotEmptyTimeline() throws Exception {
		mvc.perform(get("/timeline/following")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$[0].message", is("Jakaś taka wiadomość")))
			      .andExpect(jsonPath("$[0].user.name", is("testUser")));
		
		follow("following", "testUser2", StatusCode.OK, "user: [id=3, name=following] is now following user: [id=4, name=testUser2]");
		createPost("testUser2", "test messageA", StatusCode.OK, "Successfully saved post with id: 5 by user: (id: 4, name: testUser2)");
		createPost("testUser2", "test messageB", StatusCode.OK, "Successfully saved post with id: 6 by user: (id: 4, name: testUser2)");
		
		mvc.perform(get("/timeline/following")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$[0].message", is("test messageB")))
			      .andExpect(jsonPath("$[0].user.name", is("testUser2")))
			      .andExpect(jsonPath("$[1].message", is("test messageA")))
			      .andExpect(jsonPath("$[1].user.name", is("testUser2")))
			      .andExpect(jsonPath("$[2].message", is("Jakaś taka wiadomość")))
			      .andExpect(jsonPath("$[2].user.name", is("testUser")));
		
		List<Post> posts = postRepository.findByFollowed("following");
		assertEquals(3, posts.size());
		assertTrue(posts.get(0).getCreationDate().isAfter(posts.get(1).getCreationDate()));
		assertTrue(posts.get(1).getCreationDate().isAfter(posts.get(2).getCreationDate()));
		
	}
	
	private void createPost(String userName, String message, StatusCode status, String statusMessage) throws Exception {
		StringBuilder requestBody = new StringBuilder("{\"userName\": \"");
		requestBody.append(userName);
		requestBody.append("\",\"message\": \"");
		requestBody.append(message);
		requestBody.append("\"}");
		
		StringBuilder responseBody = new StringBuilder("{\"code\":\"");
		responseBody.append(status.toString());
		responseBody.append("\",\"statusMessage\":\"");
		responseBody.append(statusMessage);
		responseBody.append("\"}");
		
		System.out.println(responseBody.toString());
		
		mvc.perform(put("/createPost")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
			      .content(requestBody.toString()))
			      .andExpect(status().isOk())
			      .andExpect(content().string(responseBody.toString()));
			   
	}
	
	private void follow(String userName, String followedUser, StatusCode status, String statusMessage) throws Exception {
		StringBuilder requestBody = new StringBuilder("{\"userName\": \"");
		requestBody.append(userName);
		requestBody.append("\",\"followedUserName\": \"");
		requestBody.append(followedUser);
		requestBody.append("\"}");
		
		StringBuilder responseBody = new StringBuilder("{\"code\":\"");
		responseBody.append(status.toString());
		responseBody.append("\",\"statusMessage\":\"");
		responseBody.append(statusMessage);
		responseBody.append("\"}");
		
		System.out.println(responseBody.toString());
		
		mvc.perform(put("/follow")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
			      .content(requestBody.toString()))
			      .andExpect(status().isOk())
			      .andExpect(content().string(responseBody.toString()));
			   
	}

}
