package cnLabs.fileStorageAPI.Controllers;

import cnLabs.fileStorageAPI.Models.DatabaseFile;
import cnLabs.fileStorageAPI.Services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file-storage-api")
public class FileController {
    @Autowired
    FileService fileService;

    // GET file by id
    @GetMapping("{id}")
    public ResponseEntity<?> downloadFileById(@PathVariable Long id) {
        DatabaseFile databaseFile = fileService.getFileById(id);
        if (databaseFile != null) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(databaseFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", databaseFile.getFileName()))
                    .body(new ByteArrayResource(databaseFile.getData()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("I couldn't find that file with id " + id);
        }
    }

    // GET file by name
    @GetMapping("/name/{name}")
    public ResponseEntity<?> downloadFileById(@PathVariable String name) {
        DatabaseFile databaseFile = fileService.getFileByName(name);
        if (databaseFile != null) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(databaseFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", databaseFile.getFileName()))
                    .body(new ByteArrayResource(databaseFile.getData()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("I couldn't find the file named " + name);
        }
    }

    // POST file
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file) {
        try {
            return ResponseEntity.created(fileService.saveFile(file)).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT file by id
    @PutMapping("{id}")
    public ResponseEntity<?> replaceFile(@PathVariable Long id, @RequestBody MultipartFile file) {
        if (fileService.fileExists(id)) {
            try {
                return ResponseEntity.created(fileService.updateFile(id, file)).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return ResponseEntity.badRequest().body(new NoSuchFieldException("Invalid file id."));  // This error does not seem to return correctly.
        }
    }

    // PATCH file name by id
    @PatchMapping("{id}")
    public ResponseEntity<?> updateFileName(@PathVariable Long id, @RequestParam String name) {
        if (fileService.fileExists(id)) {
            return ResponseEntity.created(fileService.updateFileName(id, name)).build();
        } else {
            return ResponseEntity.badRequest().body(new NoSuchFieldException("Invalid file id."));
        }
    }

    // DELETE file by id
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteFileById(@PathVariable Long id) {
        if (fileService.fileExists(id)) {
            fileService.deleteFile(id);
            return ResponseEntity.ok("File with id " + id + " was deleted.");
        } else {
            return ResponseEntity.badRequest().body(new NoSuchFieldException("Invalid file id."));
        }
    }
}
