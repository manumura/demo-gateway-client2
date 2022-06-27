package com.example.demo.controller;

import com.example.demo.dto.TestPayload;
import com.example.demo.dto.Topic;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

  @GetMapping("/config/topics")
  public ResponseEntity<List<Topic>> getTopics() {
    final List<Topic> topics = Arrays.asList(new Topic(1, "SHAPE_CODE"));
    System.out.println("client2 topics " + topics);
    return ResponseEntity.ok(topics);
  }

  @GetMapping("/config/topics/{topicName}")
  public ResponseEntity<String> getTopic(@PathVariable String topicName) {
    System.out.println("client2 Test OK " + topicName);
    return ResponseEntity.ok("client2 test OK " + topicName);
  }

  @GetMapping("/config/types/{typeId}")
  public ResponseEntity<String> getConfigType(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Integer typeId) {
    System.out.println("client2 Test OK " + typeId);
    return ResponseEntity.ok("client2 test OK " + typeId + " + token " + token);
  }

  @GetMapping("/config/test/exception")
  public ResponseEntity<String> getException(@RequestHeader(value = "Authorization", required = false) String token) {
    System.out.println("client2 Test exception " + token);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Test exception");
  }

  @PostMapping("/config/types/{typeId}")
  public ResponseEntity<String> getConfigType(@RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable Integer typeId,
      @RequestBody TestPayload payload) {
    System.out.println("POST client2 Test OK " + typeId + " " + payload);
    return ResponseEntity.ok("client2 test OK " + typeId + " + token " + token + " payload " + payload);
  }

  @GetMapping("/test")
  public ResponseEntity<String> test(@RequestHeader(value = "Authorization", required = false) String token,
                                     @RequestParam(value = "param", required = false) String param) {
    System.out.println("client2 Test token " + token + " - param " + param);
    return ResponseEntity.ok("client2 test OK token " + token + " - param " + param);
  }
}
