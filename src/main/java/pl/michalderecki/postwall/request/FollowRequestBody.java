package pl.michalderecki.postwall.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowRequestBody implements Serializable {
	@NotNull
	private String userName;
	
	@NotNull
	private String followedUserName;
}
