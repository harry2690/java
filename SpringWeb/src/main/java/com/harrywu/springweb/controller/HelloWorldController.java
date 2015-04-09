package com.harrywu.springweb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
 
@Controller
public class HelloWorldController {
	
	@Value("${setting.text}")
	private String SettingText;

	@Value("${web.config.text}")
	private String ConfigText;
 
	@RequestMapping("/welcome")
	public ModelAndView helloWorld() {
 
		String message = "<br><div style='text-align:center;'>"
				+ "<h3>********** Hello World, Spring MVC Tutorial</h3>This message is coming from HelloWorldController.java **********</div><br><br>"+SettingText+"<br>"+ConfigText+"<br>";
		return new ModelAndView("welcome/index", "message", message);
	}
}
