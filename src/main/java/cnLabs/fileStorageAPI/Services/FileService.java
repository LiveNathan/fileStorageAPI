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

    public DatabaseFile getFileById(Long id) {
        if (fileRepository.existsById(id)) {
            return fileRepository.getReferenceById(id);
        } else {
            return null;
        }
    }

    public DatabaseFile getFileByName(String name) {
        if (fileRepository.existsByFileName(name)) {
            return fileRepository.findByFileName(name);
        } else {
            return null;
        }
    }

    public URI saveFile(MultipartFile file) throws IOException {  // How will this IOException get handled?
        DatabaseFile savedFile = fileRepository.save(multipart2database(file));  // Save file
        return createDownloadUrl(savedFile);
    }

    public URI updateFile(Long id, MultipartFile file) throws IOException {
        DatabaseFile savedFile = fileRepository.save(DatabaseFile.builder().fileName(cleanFileName(file)).fileType(file.getContentType()).size(file.getSize()).data(file.getBytes()).id(id).build());
        return createDownloadUrl(savedFile);
    }

    public URI updateFileName(Long id, String name) {
        DatabaseFile databaseFile = fileRepository.getReferenceById(id);  // Get the file.
        databaseFile.setFileName(StringUtils.cleanPath(Objects.requireNonNull(name)));  // Clean and set file name.
        DatabaseFile savedFile = fileRepository.save(databaseFile);  // Save file with new name.
        return createDownloadUrl(savedFile);
    }

    private DatabaseFile multipart2database(MultipartFile file) throws IOException {  // How will this IOException get handled?
        return DatabaseFile.builder().fileName(cleanFileName(file)).fileType(file.getContentType()).size(file.getSize()).data(file.getBytes()).build();
    }

    public boolean fileExists(Long id) {
        return fileRepository.existsById(id);
    }

    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }

    public String cleanFileName(MultipartFile file) {
        return StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    }

    public URI createDownloadUrl(DatabaseFile file) {
        file.setDownloadUrl();  // Create the download URL using the returned id
        file = fileRepository.save(file);  // Save again
        URI uri;
        try {  // Is this the right way to handle this exception?
            uri = new URI(file.getDownloadUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }
}
