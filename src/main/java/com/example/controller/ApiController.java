package com.example.controller;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface ApiController {
	ResponseEntity<Map<String, Object>> welcome();
	ResponseEntity<Map<String, Object>> echo(Map<String, Object> request);
	ResponseEntity<Map<String, String>> info();
	ResponseEntity<Map<String, Object>> transform(Map<String, Object> data);
}
