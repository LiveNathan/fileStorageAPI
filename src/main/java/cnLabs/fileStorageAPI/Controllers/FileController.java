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

@RestController
@RequestMapping("/file-storage-api")
public class FileController {
    @Autowired
    FileService fileService;

    // GET file
    @GetMapping("{id}")
    public ResponseEntity<?> downloadFileById(@PathVariable Long id) {
        DatabaseFile databaseFile = fileService.getFile(id);
        if (databaseFile != null){
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
        try {
            return ResponseEntity.created(fileService.saveFile(file)).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT file by id

    // PATCH file by id

    // DELETE file by id
}
