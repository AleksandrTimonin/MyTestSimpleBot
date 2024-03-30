package aa.timonin.repository;

import aa.timonin.entity.AppDocument;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {
}
