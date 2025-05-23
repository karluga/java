package com.may.informatic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.may.informatic.entity.Student;
import com.may.informatic.service.HomeService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

	@Autowired
	HomeService service;
	
	@GetMapping("/home")
	public String getHome(Model m) {
		
		m.addAttribute("selectedstudent", new Student());
		m.addAttribute("listofstudents", service.getAllStudents());
		
		return "home";
	}

	@GetMapping("/deleteStudent/{id}")
	public String deleteStudent(@PathVariable int id, Model m) {
		
		service.deleteStudent(id);
		m.addAttribute("listofstudents", service.getAllStudents());
		
		return "redirect:/home";
	}

	@GetMapping("/updateStudent/{id}")
	public String updateStudent(@PathVariable int id, Model m) {
		
		m.addAttribute("selectedstudent", service.selectStudent(id));
		m.addAttribute("listofstudents", service.getAllStudents());
		
		return "home";
	}
	
	@PostMapping("/saveStudent")
	public String saveStudent(@ModelAttribute Student st, Model m) {
		
		service.addStudent(st);
		m.addAttribute("listofstudents", service.getAllStudents());
		
		return "redirect:/home";
	}

	@GetMapping("/login")
	public String getLoginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, Model m) {
		if (service.validateUser(username, password)) {
			return "redirect:/home";
		}
		m.addAttribute("error", "Invalid username or password");
		return "login";
	}
	
}
