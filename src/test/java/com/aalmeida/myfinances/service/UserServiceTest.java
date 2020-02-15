package com.aalmeida.myfinances.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.model.entity.User;
import com.aalmeida.myfinances.model.repository.UserRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {
	
	@Autowired
	UserService service;
	
	@Autowired
	UserRepository repository;
	
	@Test
	public void shouldValidateEmail(){
		//cenario
		
		UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
		
		repository.deleteAll();
		
		//acao
		service.emailValidate("email@email.com");
	}
	
	@Test
	public void mustShowErrorWhenValidatingEmailWhenThereIsRegistered() {
		//cenario
		User user = User.builder().name("user").email("email@email.com").build();
		repository.save(user);
		
		//acao
		Assertions.assertThrows(BusinessRuleException.class, () -> service.emailValidate("email@email.com"));
	}
}
