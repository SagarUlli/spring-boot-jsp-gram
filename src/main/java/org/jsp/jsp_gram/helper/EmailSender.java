package org.jsp.jsp_gram.helper;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailSender {
	private final JavaMailSender sender;

	private final TemplateEngine engine;

	public void sendOtp(String to, int otp, String name) {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("sagarulli5@gmail.com", "JSP Gram Application");
			helper.setTo(to);
			helper.setSubject("Verify Email through OTP");

			Context context = new Context();
			context.setVariable("name", name);
			context.setVariable("otp", otp);
			String body = engine.process("otp-template", context);
			helper.setText(body, true);
		} catch (Exception e) {
		}
		sender.send(message);
	}
}
