package com.example.WebChat.Controller;

import com.example.WebChat.Entity.ChatMessage;
import com.example.WebChat.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

   
    @GetMapping("/courses/{courseId}/messages")
    public ResponseEntity<?> getMessagesByCourse(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<ChatMessage> msgList = chatService.getMessagesByCourseWithPagination(courseId, page, size);
        return ResponseEntity.ok(msgList);
    }

    //  2) SEND MESSAGE (Ensures Correct Timestamp)
    @PostMapping("/courses/{courseId}/sendMessage")
    public ResponseEntity<?> sendMessage(
            @PathVariable String courseId,
            @RequestParam("sender") String sender,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            ChatMessage newMsg = new ChatMessage();
            newMsg.setCourseId(courseId);
            newMsg.setSender(sender);
            newMsg.setTimestamp(new java.util.Date());  // Save correct timestamp
            newMsg.setStatus("sent");

            if (content != null) {
                newMsg.setContent(content.trim());
            } else {
                newMsg.setContent("");
            }

            //  Handle Image Upload
            if (imageFile != null && !imageFile.isEmpty()) {
                File uploadDir = new File("uploads/MessageImages");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String originalFilename = imageFile.getOriginalFilename();
                if (originalFilename == null) {
                    originalFilename = "unknown.png";
                }
                String fileExtension = "";
                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex > 0) {
                    fileExtension = originalFilename.substring(dotIndex);
                }
                String uniqueName = courseId + "_" + sender + "_" + UUID.randomUUID() + fileExtension;
                uniqueName = Path.of(uniqueName).getFileName().toString();

                Path destination = Path.of("uploads", "MessageImages", uniqueName);
                Files.copy(imageFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                newMsg.setImage("/uploads/MessageImages/" + uniqueName);
            }

            //  Save to DB
            ChatMessage saved = chatService.saveMessage(newMsg);

            //  Automatic Real-Time Update
            messagingTemplate.convertAndSend("/topic/courses/" + courseId, saved);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Could not store file: " + e.getMessage()));
        }
    }
    // 3) REACT TO A MESSAGE
    @PostMapping("/courses/{courseId}/messages/{messageId}/react")
    public ResponseEntity<?> reactToMessage(
            @PathVariable String courseId,
            @PathVariable String messageId,
            @RequestBody Map<String, String> reactionData
    ) {
        String user = reactionData.get("user");
        String emoji = reactionData.get("emoji");

        ChatMessage msg = chatService.findMessageById(messageId);
        if (msg == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message not found"));
        }
        // Add/Update reaction
        msg.getReactions().put(user, emoji);
        ChatMessage updated = chatService.saveMessage(msg);

        // **AUTOMATIC BROADCAST** the updated message
        messagingTemplate.convertAndSend("/topic/courses/" + courseId, updated);

        return ResponseEntity.ok(updated);
    }

    // 4) UPDATE STATUS (optional)
    @PostMapping("/courses/{courseId}/messages/{messageId}/status")
    public ResponseEntity<?> updateMessageStatus(
            @PathVariable String courseId,
            @PathVariable String messageId,
            @RequestBody Map<String, String> statusMap
    ) {
        String status = statusMap.get("status");
        ChatMessage msg = chatService.findMessageById(messageId);
        if (msg == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message not found"));
        }
        msg.setStatus(status);
        ChatMessage updated = chatService.saveMessage(msg);

        // Could broadcast if you want real-time status updates
        messagingTemplate.convertAndSend("/topic/courses/" + courseId, updated);

        return ResponseEntity.ok(updated);
    }


   // 5) DELETE MESSAGE (Removes from DB)
   @DeleteMapping("/courses/{courseId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(
        @PathVariable String courseId,
        @PathVariable String messageId,
        @RequestParam("sender") String sender // Keep @RequestParam
    ) {
       // 1. Find the message by ID
       ChatMessage msg = chatService.findMessageById(messageId);
       if (msg == null) {
           return ResponseEntity.status(404).body(Map.of("error", "Message not found"));
       }

       // 2. Check if the message belongs to the specified course
       if (!msg.getCourseId().equals(courseId)) {
           return ResponseEntity.status(403).body(Map.of("error", "Message does not belong to the specified course"));
       }

       // 3. Check if the user making the request is the sender of the message
       if (!msg.getSender().equals(sender)) {
           return ResponseEntity.status(403).body(Map.of("error", "You can only delete your own messages"));
       }

       // 4. Check if the message is within the 5-minute limit
       long fiveMinutesInMillis = 5 * 60 * 1000; // 5 minutes in milliseconds
       long messageTimestamp = msg.getTimestamp().getTime();
       long currentTimestamp = System.currentTimeMillis();

       if (currentTimestamp - messageTimestamp > fiveMinutesInMillis) {
           return ResponseEntity.status(403).body(Map.of("error", "You can only delete messages within 5 minutes of sending"));
       }

       // 5. Delete the message from the database
       chatService.deleteMessageById(messageId);

       // 6. Notify other users that the message is deleted (using the original message ID)
       messagingTemplate.convertAndSend("/topic/courses/" + courseId, Map.of(
               "messageId", messageId, // Send the original message ID
               "status", "deleted"
       ));

       return ResponseEntity.ok(Map.of("message", "Message deleted successfully"));
   }
}
