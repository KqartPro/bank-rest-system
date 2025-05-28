package kz.pryakhin.bankrest.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import kz.pryakhin.bankrest.dto.card.CardDto;
import kz.pryakhin.bankrest.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class CardController extends ApiController {
	private final CardService cardService;

	// Admin Actions


	@GetMapping("/users/{userId}/cards")
	@PreAuthorize("hasRole('ADMIN')")
	public List<CardDto> getUserCards(
			@PathVariable Long userId,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Size(min = 1, max = 100) int size,
			@RequestParam(required = false) String search
	) {
		return cardService.getUserCards(userId, page, size, search);
	}


	@GetMapping("/cards")
	@PreAuthorize("hasRole('ADMIN')")
	public List<CardDto> getCards(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Size(min = 1, max = 100) int size,
			@RequestParam(required = false) String search
	) {
		return cardService.getCards(page, size, search);
	}


	@GetMapping("/cards/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public CardDto getCard(@PathVariable Long id) {
		return cardService.getCard(id);
	}


	@PostMapping("/users/{userId}/cards")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CardDto> createCard(@PathVariable Long userId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(userId));
	}


	@PutMapping("/cards/{id}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	public CardDto activateCard(@PathVariable Long id) {
		return cardService.activateCard(id);
	}


	@PutMapping("/cards/{id}/block")
	@PreAuthorize("hasRole('ADMIN')")
	public CardDto blockCard(@PathVariable Long id) {
		return cardService.blockCard(id);
	}


	@DeleteMapping("/cards/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
		cardService.deleteCard(id);
		return ResponseEntity.noContent().build();
	}

	// User Actions


	@GetMapping("/cards/my")
	public List<CardDto> getMyCards(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Size(min = 1, max = 100) int size,
			@RequestParam(required = false) String search,
			Principal principal) {
		return cardService.getMyCards(page, size, search, principal);
	}


	@GetMapping("/cards/my/{id}")
	public CardDto getMyCard(@PathVariable Long id, Principal principal) {
		return cardService.getMyCard(id, principal);
	}


	@GetMapping("/cards/my/{id}/balance")
	public BigDecimal getMyCardBalance(@PathVariable Long id, Principal principal) {
		return cardService.getMyCardBalance(id, principal);
	}


	@PutMapping("/cards/my/{id}/request-block")
	public CardDto blockMyCardRequest(@PathVariable Long id, Principal principal) {
		return cardService.blockMyCardRequest(id);
	}


}
