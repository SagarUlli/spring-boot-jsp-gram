package org.jsp.jsp_gram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	@GetMapping("/")
	public String home() {
		return "JSPGram backend is running";
	}
}