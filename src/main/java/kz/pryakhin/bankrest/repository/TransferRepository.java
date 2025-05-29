package kz.pryakhin.bankrest.repository;

import jakarta.transaction.Transactional;
import kz.pryakhin.bankrest.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface TransferRepository extends JpaRepository<Transfer, Long> {
	
}
