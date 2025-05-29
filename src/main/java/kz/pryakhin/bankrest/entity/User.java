package kz.pryakhin.bankrest.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")

@Data
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String surname;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Role> roles;

//	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<Card> cards = new ArrayList<>();


	// User Details


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}


	@Override
	public String getUsername() {
		return email;
	}


	@Override
	public String getPassword() {
		return password;
	}
}
