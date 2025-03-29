package com.example.WebChat.Controller;

import com.example.WebChat.Entity.AuthenticationDoc;
import com.example.WebChat.Service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAndStore(
            @RequestParam String collegeEmail,
            @RequestParam String course,
            @RequestParam(value = "idPhoto", required = false) MultipartFile file,
            HttpSession session
    ) {
        try {
            String email = (String) session.getAttribute("email");
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("message", "No user in session"));
            }

            AuthenticationDoc authDoc = new AuthenticationDoc();
            authDoc.setStudentEmail(email);
            authDoc.setCollegeEmail(collegeEmail);
            authDoc.setCourse(course);

            String photoPath = null;
            if (file != null && !file.isEmpty()) {
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) {
                    originalFilename = "unknown.png";
                }
                String fileExtension = "";
                int i = originalFilename.lastIndexOf('.');
                if (i > 0) {
                    fileExtension = originalFilename.substring(i);
                }
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                uniqueFilename = Path.of(uniqueFilename).getFileName().toString();

                Path destination = Path.of("uploads", uniqueFilename);
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                photoPath = "/uploads/" + uniqueFilename;
                authDoc.setIdPhotoPath(photoPath);
            }

            authenticationService.save(authDoc);

            return ResponseEntity.ok(Map.of(
                    "message", "Authentication details saved successfully",
                    "photoPath", photoPath == null ? "" : photoPath
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Could not store the file"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("message", "Error storing data"));
        }
    }
}
