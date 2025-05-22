package com.spring.informatic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.informatic.service.LoginService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class HomeController {

	@Autowired
	LoginService service; //bean
		
	@GetMapping("/home")
	public String getHome(Model model) {
		model.addAttribute("data", "This is our Java Course");
		model.addAttribute("data2", "This is our Python Course");
		return "home";
	}

	@GetMapping("/index")
	public String getIndex() {
		return "index";
	}
	
	@PostMapping("/submit")
	public String postLogin(@RequestParam String username, @RequestParam String password, Model model) {
		
		model.addAttribute("username", username);
		model.addAttribute("password", password);		
		
		boolean check = service.checkLogin(username, password);
		
		if(!check) {
			model.addAttribute("error", "The credentials are not correct. Please try again !");
			return "home";
		}
		return "submit";
	}
	
}
