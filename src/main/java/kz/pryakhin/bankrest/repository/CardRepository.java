package kz.pryakhin.bankrest.repository;

import jakarta.transaction.Transactional;
import kz.pryakhin.bankrest.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CardRepository extends JpaRepository<Card, Long> {

	Optional<Card> findByOwnerIdAndId(Long ownerId, Long cardId);

	List<Card> findByOwnerId(Long id);

	Page<Card> findByOwnerId(Long id, Pageable pageable);

	Page<Card> findByMaskedSuffixContaining(String search, Pageable pageable);

	Page<Card> findByOwnerIdAndMaskedSuffixContaining(Long ownerId, String search, Pageable pageable);
}
