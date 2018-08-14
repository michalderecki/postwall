/**
 * 
 */
package pl.michalderecki.postwall.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Michal
 *
 */
@AllArgsConstructor
@Getter
@Setter
public class Status {
	private StatusCode code;
	private String statusMessage;
	
	public Status(StatusCode code) {
		super();
		this.code = code;
	}
	
	
}
