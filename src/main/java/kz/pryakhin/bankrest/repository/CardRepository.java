package kz.pryakhin.bankrest.repository;

import jakarta.transaction.Transactional;
import kz.pryakhin.bankrest.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface CardRepository extends JpaRepository<Card, Long> {

	Page<Card> findByOwnerIdAndEncryptedNumberContaining(Long ownerId, String search, Pageable pageable);

	Page<Card> findByEncryptedNumberContaining(String search, Pageable pageable);

	Optional<Card> findByOwnerIdAndId(Long ownerId, Long cardId);
}
