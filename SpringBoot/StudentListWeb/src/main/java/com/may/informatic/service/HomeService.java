package com.may.informatic.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.may.informatic.entity.Student;
import com.may.informatic.entity.User;
import com.may.informatic.repository.StudentRepository;
import com.may.informatic.repository.UserRepository;

@Service
public class HomeService {

	@Autowired
	StudentRepository strep;

	@Autowired
	UserRepository userRepo;
	
	public List<Student> getAllStudents() {
		return strep.findAll();
	}
	
	public Student addStudent(Student st) {
		return strep.save(st);
	}
	
	public void deleteStudent(int id) {
		strep.deleteById(id);
	}
	
	public Student selectStudent(int id) {
		return strep.findById(id).orElse(null);
	}

	public boolean validateUser(String username, String password) {
		User user = userRepo.findUserByUsername(username);
		return user != null && user.getPassword().equals(password);
	}
}
