package com.aalmeida.myfinances.service;

import java.util.Optional;

import com.aalmeida.myfinances.model.entity.User;

public interface UserService {
	
	User authenticate(String email, String password);
	
	User saveUser(User user);
	
	void emailValidate(String email);
	
	Optional<User> getById(long id);
}
