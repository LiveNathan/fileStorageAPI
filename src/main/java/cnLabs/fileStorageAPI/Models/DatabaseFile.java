package cnLabs.fileStorageAPI.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;

@Entity
@Table(name = "database_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String downloadUrl;
    private Long size;

    @Lob
    private byte[] data;

    public String setDownloadUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(String.valueOf(this.id))
                .toUriString();
    }

}
