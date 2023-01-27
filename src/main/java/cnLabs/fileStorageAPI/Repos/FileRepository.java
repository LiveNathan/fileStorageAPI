package cnLabs.fileStorageAPI.Repos;

import cnLabs.fileStorageAPI.Models.DatabaseFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<DatabaseFile, Long> {
}
