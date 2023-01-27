package cnLabs.fileStorageAPI.Controllers;

import cnLabs.fileStorageAPI.Models.DatabaseFile;
import cnLabs.fileStorageAPI.Repos.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@RestController
@RequestMapping("/file-storage-api")
public class FileController {
    @Autowired
    FileRepository fileRepository;

    // GET file
    @GetMapping("{id}")
    public ResponseEntity<?> downloadFileById(@PathVariable Long id) {
        boolean existsById = fileRepository.existsById(id);
        if (existsById){
            DatabaseFile databaseFile = fileRepository.getReferenceById(id);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(databaseFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", databaseFile.getFileName()))
                    .body(new ByteArrayResource(databaseFile.getData()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("I couldn't find that file with id " + id);
        }
    }

    // POST file
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file){
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            final DatabaseFile databaseFile = DatabaseFile.builder().fileName(filename).fileType(file.getContentType()).size(file.getSize()).data(file.getBytes()).build();
            DatabaseFile savedFile = fileRepository.save(databaseFile);  // Save file
            savedFile.setDownloadUrl();  // Create the download URL using the returned id
            savedFile = fileRepository.save(savedFile);  // Save again
            return ResponseEntity.created(new URI(savedFile.getDownloadUrl())).build();
        } catch (IOException | URISyntaxException e) {
            // Wrap exception with custom message
            return ResponseEntity.badRequest().body(new IllegalStateException("Well, crap. I couldn't store " + filename + " for you. Please try again and I'll do my best.", e));
        }
    }

    // PUT file by id

    // PATCH file by id

    // DELETE file by id
}
