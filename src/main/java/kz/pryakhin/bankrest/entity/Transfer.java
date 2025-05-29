package kz.pryakhin.bankrest.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")

@Data
public class Transfer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(nullable = false)
	private BigDecimal amount;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private LocalDateTime timestamp;


	@ManyToOne(optional = false)
	private Card sender;

	@ManyToOne(optional = false)
	private Card recipient;


}