package com.example.WebChat.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FileController {

    @GetMapping("/notes")
    public ResponseEntity<?> listNotes(@RequestParam String course) {
       
        String safeCourse = course.replaceAll("[^a-zA-Z0-9]", "");
        // Directory: uploads/Notes/<safeCourse>  e.g. "uploads/Notes/Bsc"
        File folder = new File("uploads/Notes/" + safeCourse);
        List<Map<String, String>> fileList = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            for (File f : folder.listFiles()) {
                if (!f.isDirectory()) {
                    fileList.add(
                      Map.of("filename", f.getName())
                    );
                }
            }
        }
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/papers")
    public ResponseEntity<?> listPapers(@RequestParam String course) {
        // Directory: uploads/Paper/<safeCourse>
        String safeCourse = course.replaceAll("[^a-zA-Z0-9]", "");
        File folder = new File("uploads/Paper/" + safeCourse);
        List<Map<String, String>> fileList = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            for (File f : folder.listFiles()) {
                if (!f.isDirectory()) {
                    fileList.add(
                      Map.of("filename", f.getName())
                    );
                }
            }
        }
        return ResponseEntity.ok(fileList);
    }
}
