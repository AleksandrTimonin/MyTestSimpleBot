package timonin.aa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import timonin.aa.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
