package com.aalmeida.myfinances.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.exceptions.ErrorAuthenticate;
import com.aalmeida.myfinances.model.entity.User;
import com.aalmeida.myfinances.model.repository.UserRepository;
import com.aalmeida.myfinances.service.UserService;


@Service
public class UserServiceImpl implements UserService {

	private UserRepository repository;
	
	//@Autowired
	public UserServiceImpl(UserRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public User authenticate(String email, String password) {
		Optional<User> user = repository.findByEmail(email);
		
		if(!user.isPresent()) {
			throw new ErrorAuthenticate("User not found for the given email");
		}
		
		if(!user.get().getPassword().equals(password)) {
			throw new ErrorAuthenticate("Invalid password");
		}
		
		return user.get();
	}

	@Override
	@Transactional
	public User saveUser(User user) {
		emailValidate(user.getEmail());
		return repository.save(user);
	}

	@Override
	public void emailValidate(String email) {
		boolean exist = repository.existsByEmail(email);
		
		if (exist) {
			throw new BusinessRuleException("There is already a registered user with this email");
		}
	}

	@Override
	public Optional<User> getById(long id) {
		
		return repository.findById(id);
	}
	
	
}
