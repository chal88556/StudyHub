package com.example.WebChat.Controller;

import com.example.WebChat.Entity.Student;
import com.example.WebChat.Service.EmailService;
import com.example.WebChat.Service.StudentService;
import com.example.WebChat.Service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private EmailService emailService;

    private static final String UPLOAD_DIR = "uploads/idphotos/";
    private static final String PROFILE_PIC_UPLOAD_DIR = "uploads/profilepics/";

    // 1) Sign Up
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Student student,
                                    BindingResult bindingResult,
                                    HttpSession session) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    bindingResult.getAllErrors().stream()
                            .map(error -> error.getDefaultMessage())
                            .collect(Collectors.toList())
            );
        }

        Student existing = studentService.findStudentByEmail(student.getEmail());
        if (existing != null) {
            return ResponseEntity.status(409).body(Map.of("message", "Email already registered"));
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(student.getPassword());
        student.setPassword(hashedPassword);

        // Not verified initially
        student.setVerified(false);
        student.setHasLoggedInOnce(false);

        Student savedStudent = studentService.saveStudent(student);

        session.setAttribute("email", savedStudent.getEmail());
        session.setAttribute("name", savedStudent.getStudentname());

        return ResponseEntity.ok(Map.of(
                "message", "Signup successful",
                "email", savedStudent.getEmail(),
                "name", savedStudent.getStudentname()
        ));
    }

    // 2) Complete Profile
    @PostMapping("/completeProfile")
    public ResponseEntity<?> completeProfile(
            @RequestParam("courseId") String courseId,
            @RequestParam("course") String courseName,
            @RequestParam("verificationMethod") String verificationMethod,
            @RequestParam(value = "collegeEmail", required = false) String collegeEmail,
            @RequestParam(value = "otp", required = false) String otp,
            @RequestParam(value = "idPhoto", required = false) MultipartFile idPhoto,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "No user in session"));
        }

        Student student = studentService.findStudentByEmail(email);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Student not found"));
        }

        student.setCourseId(courseId);
        student.setCourse(courseName);

        // Assign rollNo if not assigned
        if (student.getRollNo() == null || student.getRollNo().isEmpty()) {
            String generatedRollNo = sequenceService.generateRollNoForCourse(courseName);
            student.setRollNo(generatedRollNo);
        }

        if ("emailOtp".equalsIgnoreCase(verificationMethod)) {
            if (collegeEmail == null || collegeEmail.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email and OTP are required for email verification."));
            }

            // Verify OTP (using EmailService)
            if (!emailService.verifyOtp(collegeEmail, otp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid OTP."));
            }

            student.setEmail(collegeEmail);
            student.setVerified(true); // Mark student as verified
        } else if ("idPhoto".equalsIgnoreCase(verificationMethod)) {
            if (idPhoto == null || idPhoto.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "ID Photo is required"));
            }

            try {
                // Create the uploads directory if it doesn't exist
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = idPhoto.getOriginalFilename();
                if (originalFilename == null) {
                    originalFilename = "unknown.png"; // Or handle it in a way that suits your application
                }
                String fileExtension = "";
                int i = originalFilename.lastIndexOf('.');
                if (i > 0) {
                    fileExtension = originalFilename.substring(i);
                }
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                // Resolve the path correctly
                Path destination = Paths.get(UPLOAD_DIR).resolve(uniqueFilename);

                // Save the file
                Files.copy(idPhoto.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Update the student object with the new path
                student.setId( UPLOAD_DIR + uniqueFilename); // Prepend "/" to indicate root-relative path

                // Mark student as not verified (pending admin verification)
                student.setVerified(false);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("message", "Could not save the ID photo."));
            }
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid verification method."));
        }

        studentService.saveStudent(student);

        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully. Please login.",
                "courseId", courseId,
                "course", courseName,
                "rollNo", student.getRollNo(),
                "verified", student.isVerified(),
                "photoPath", student.getId() != null ? student.getId() : ""
        ));
    }

    // 3) Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData,
                                   HttpSession session) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        Student existingStudent = studentService.findStudentByEmail(email);
        if (existingStudent == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        boolean matches = passwordEncoder.matches(password, existingStudent.getPassword());
        if (!matches) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        if (!existingStudent.isHasLoggedInOnce()) {
            existingStudent.setHasLoggedInOnce(true);
            studentService.saveStudent(existingStudent);

            session.setAttribute("email", existingStudent.getEmail());
            session.setAttribute("name", existingStudent.getStudentname());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful (first time)",
                    "email", existingStudent.getEmail(),
                    "name", existingStudent.getStudentname()
            ));
        } else {
            if (existingStudent.isVerified()) {
                session.setAttribute("email", existingStudent.getEmail());
                session.setAttribute("name", existingStudent.getStudentname());
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "email", existingStudent.getEmail(),
                        "name", existingStudent.getStudentname(),
                        "courseId", existingStudent.getCourseId(),
                        "course", existingStudent.getCourse(),
                        "rollNo", existingStudent.getRollNo()
                ));
            } else {
                return ResponseEntity.status(403).body(Map.of("message", "Verification pending. Please wait."));
            }
        }
    }

    // 4) Helper
    @GetMapping("/getAll")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/current")
    public ResponseEntity<?> current(HttpSession session) {
        String name = (String) session.getAttribute("name");
        String email = (String) session.getAttribute("email");

        if (name != null && email != null) {
            Student student = studentService.findStudentByEmail(email);
            if (student != null) {
                // Use HashMap to allow null values
                Map<String, Object> response = new HashMap<>();
                response.put("name", student.getStudentname());
                response.put("email", student.getEmail());
                response.put("courseId", student.getCourseId());
                response.put("course", student.getCourse());
                response.put("rollNo", student.getRollNo());
                response.put("bio", student.getBio());
                response.put("profilePicture", student.getProfilePicture()); // Null values are fine here

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(Map.of("message", "Student not found."));
            }
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Not logged in"));
        }
    }

    // 5) Update Profile (for logged-in users)
    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam("fullName") String fullName,
            @RequestParam("bio") String bio,
            HttpSession session) {

        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not logged in."));
        }

        Student student = studentService.findStudentByEmail(email);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Student not found."));
        }

        // Update the student's name and bio
        student.setStudentname(fullName);
        student.setBio(bio);

        // Handle profile picture update (if a new picture is provided)
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                // Create directory for profile pictures if it doesn't exist
                Path profilePicUploadPath = Paths.get(PROFILE_PIC_UPLOAD_DIR);
                if (!Files.exists(profilePicUploadPath)) {
                    Files.createDirectories(profilePicUploadPath);
                }

                String originalFilename = StringUtils.cleanPath(profilePicture.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(originalFilename);
                String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

                // Resolve the path for the new profile picture
                Path destination = profilePicUploadPath.resolve(uniqueFilename);

                // Save the new profile picture
                Files.copy(profilePicture.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Delete old profile picture if it exists and is not the default placeholder
                if (student.getProfilePicture() != null && !student.getProfilePicture().isEmpty()
                        && !student.getProfilePicture().equals("/uploads/profilepics/default.jpg")) {
                    Path oldFilePath = Paths.get(".", student.getProfilePicture()).normalize();
                    Files.deleteIfExists(oldFilePath);
                }

                // Update student object with the new profile picture path
                student.setProfilePicture("/" + PROFILE_PIC_UPLOAD_DIR + uniqueFilename);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Could not save the profile picture."));
            }
        }

        // Save the updated student object
        studentService.saveStudent(student);

        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "photoPath", student.getProfilePicture() != null ? student.getProfilePicture() : ""
        ));
    }
}