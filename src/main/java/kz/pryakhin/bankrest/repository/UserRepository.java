package kz.pryakhin.bankrest.repository;

import jakarta.transaction.Transactional;
import kz.pryakhin.bankrest.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Page<User> findByEmailContainingIgnoreCase(String search, Pageable pageable);
}
