package kz.pryakhin.bankrest.controller;

import jakarta.validation.Valid;
import kz.pryakhin.bankrest.dto.card.TransferCreateDto;
import kz.pryakhin.bankrest.dto.card.TransferDto;
import kz.pryakhin.bankrest.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferController {
	private final TransferService transferService;


	@PostMapping("/own-cards")
	@PreAuthorize("hasRole('USER')")
	public TransferDto transferBetweenOwnCards(@RequestBody @Valid TransferCreateDto transferCreateDto, Principal principal) {
		return transferService.transferBetweenOwnCards(transferCreateDto, principal);
	}


	@PostMapping()
	@PreAuthorize("hasRole('ADMIN')")
	public TransferDto transferBetweenCards(@RequestBody @Valid TransferCreateDto transferCreateDto) {
		return transferService.transferBetweenCards(transferCreateDto);
	}
}
