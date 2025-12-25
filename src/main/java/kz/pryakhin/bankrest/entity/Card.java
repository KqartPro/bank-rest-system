package kz.pryakhin.bankrest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")

@Data
@AllArgsConstructor
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String encryptedNumber;

	private String maskedSuffix;

	private LocalDateTime expirationDate;

	@Enumerated(EnumType.STRING)
	private CardStatus status;

	private BigDecimal balance;

	@ManyToOne()
	@JoinColumn(name = "user_id")
	private User owner;


	public Card() {
		this.expirationDate = LocalDateTime.now().plusYears(1);
		this.status = CardStatus.ACTIVE;
		this.balance = BigDecimal.ZERO;
	}


	public Card(Long id, String maskedSuffix, BigDecimal balance, CardStatus status, User owner) {
		this.id = id;
		this.maskedSuffix = maskedSuffix;
		this.balance = balance;
		this.status = status;
		this.owner = owner;
		this.expirationDate = LocalDateTime.now().plusYears(1);
	}
}