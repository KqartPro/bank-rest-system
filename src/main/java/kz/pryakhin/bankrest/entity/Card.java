package kz.pryakhin.bankrest.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")

@Data
public class Card {


	public Card() {
		this.expirationDate = LocalDateTime.now().plusYears(1);
		this.status = CardStatus.ACTIVE;
		this.balance = BigDecimal.ZERO;
	}


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
}