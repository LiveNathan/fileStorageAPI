package cnLabs.fileStorageAPI.Repos;

import cnLabs.fileStorageAPI.Models.DatabaseFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<DatabaseFile, Long> {
    boolean existsById(Long id);

    boolean existsByFileName(String name);

    DatabaseFile findByFileName(String name);
}
