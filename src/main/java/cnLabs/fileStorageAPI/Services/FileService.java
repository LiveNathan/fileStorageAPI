package cnLabs.fileStorageAPI.Services;

import cnLabs.fileStorageAPI.Models.DatabaseFile;
import cnLabs.fileStorageAPI.Repos.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Service
public class FileService {
    @Autowired
    FileRepository fileRepository;

    public DatabaseFile getFile(Long id) {
        if (fileRepository.existsById(id)) {
            return fileRepository.getReferenceById(id);
        } else {
            return null;
        }
    }

    public URI saveFile(MultipartFile file) throws IOException {  // How will this IOException get handled?
        DatabaseFile savedFile = fileRepository.save(multipart2database(file));  // Save file
        savedFile.setDownloadUrl();  // Create the download URL using the returned id
        savedFile = fileRepository.save(savedFile);  // Save again
        URI uri;
        try {  // Is this the right way to handle this exception?
            uri = new URI(savedFile.getDownloadUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    private DatabaseFile multipart2database(MultipartFile file) throws IOException {  // How will this IOException get handled?
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        return DatabaseFile.builder().fileName(filename).fileType(file.getContentType()).size(file.getSize()).data(file.getBytes()).build();
    }

    public boolean fileExists(Long id) {
        return fileRepository.existsById(id);
    }
}
