package com.example.WebChat.Service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    private static final String CHATBOT_API_URL = "https://open-ai21.p.rapidapi.com/chatgpt";
    private static final String API_KEY = "fec7885a5amsha1821d445445622p116935jsn63dba823881b"; 

    public String getChatbotResponse(String userMessage) {
        try {
            HttpResponse<String> response = Unirest.post(CHATBOT_API_URL)
                    .header("x-rapidapi-key", API_KEY)
                    .header("x-rapidapi-host", "open-ai21.p.rapidapi.com")
                    .header("Content-Type", "application/json")
                    .body("{\"messages\":[{\"role\":\"user\",\"content\":\"" + userMessage + "\"}],\"web_access\":false}")
                    .asString(); // Get response as String

            if (response.getStatus() == 200) {
                // Directly return the response body (plain text)
                return response.getBody();
            } else {
                return "{\"error\": \"Error: Received status " + response.getStatus() + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error: Unable to fetch chatbot response.\"}";
        }
    }
}