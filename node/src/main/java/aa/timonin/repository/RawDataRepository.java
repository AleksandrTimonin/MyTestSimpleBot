package aa.timonin.repository;

import aa.timonin.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepository extends JpaRepository<RawData,Long> {
}
