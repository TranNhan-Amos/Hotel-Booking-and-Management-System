package sd19303no1.hotel_booking_and_management_system.Controller.PageController.Client;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class RoomImageController {

    private static final String ROOM_IMAGE_UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/img/rooms";

    @GetMapping("/img/rooms/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveRoomImage(@PathVariable String filename) {
        System.out.println("=== SERVING ROOM IMAGE ===");
        System.out.println("Requested filename: " + filename);
        System.out.println("Upload directory: " + ROOM_IMAGE_UPLOAD_DIR);
        
        try {
            Path filePath = Paths.get(ROOM_IMAGE_UPLOAD_DIR, filename);
            File file = filePath.toFile();
            
            if (file.exists() && file.isFile()) {
                System.out.println("File found: " + file.getAbsolutePath());
                System.out.println("File size: " + file.length() + " bytes");
                
                Resource resource = new FileSystemResource(file);
                
                // Determine content type based on file extension
                String contentType = "image/jpeg"; // default
                if (filename.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.toLowerCase().endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (filename.toLowerCase().endsWith(".webp")) {
                    contentType = "image/webp";
                }
                
                System.out.println("Serving with content type: " + contentType);
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                    .body(resource);
            } else {
                System.out.println("File not found: " + file.getAbsolutePath());
                // Return default image if file doesn't exist
                Path defaultPath = Paths.get(ROOM_IMAGE_UPLOAD_DIR, "default-room.jpg");
                File defaultFile = defaultPath.toFile();
                
                if (defaultFile.exists()) {
                    System.out.println("Serving default image: " + defaultFile.getAbsolutePath());
                    Resource resource = new FileSystemResource(defaultFile);
                    return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                        .body(resource);
                } else {
                    System.out.println("Default image not found either");
                    // Return 404 if no default image
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (Exception e) {
            System.err.println("Error serving image " + filename + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
} 