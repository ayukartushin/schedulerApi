package su.kartushin.busAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.kartushin.busAPI.objects.VPNProxy;

@Repository
public interface VPNProxyRepository extends JpaRepository<VPNProxy, Long> {
}
