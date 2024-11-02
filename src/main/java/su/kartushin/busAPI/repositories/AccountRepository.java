package su.kartushin.busAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.kartushin.busAPI.objects.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
