package com.ai.play.ai.controller;


import com.ai.play.ai.dto.ChatGptRequest;
import com.ai.play.ai.dto.ChatGptResponse;
import com.ai.play.ai.dto.Message;
import com.ai.play.ai.dto.Request;
import com.ai.play.ai.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AiController {
    @Autowired
    private AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Request request, @RequestHeader("Authorization") String userApiKey) {
        ChatGptRequest gptRequest = new ChatGptRequest();
        gptRequest.setModel("gpt-3.5-turbo");
        Message message = new Message();
        message.setRole("user");
        message.setContent(request.getRequest());
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        gptRequest.setMessages(messages);

        try {
            RestClient client = aiService.client(userApiKey);
            ChatGptResponse response = client.post().body(gptRequest).retrieve().body(ChatGptResponse.class);
            String content = response.getChoices().get(0).getMessage().getContent();
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error during chat API call", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
