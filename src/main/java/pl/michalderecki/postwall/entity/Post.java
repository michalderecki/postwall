package pl.michalderecki.postwall.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Michal
 *
 */
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name="posts")
public class Post implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Column(name="message", length = 140)
	private String message;
	
	private LocalDateTime creationDate;
	
	@ManyToOne
	@JoinColumn(name ="user_id")
	private User user;

	public Post(String message, LocalDateTime creationDate, User user) {
		super();
		this.message = message;
		this.creationDate = creationDate;
		this.user = user;
	}
	
}
