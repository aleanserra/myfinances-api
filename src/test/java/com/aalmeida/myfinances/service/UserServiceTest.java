package com.aalmeida.myfinances.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.model.repository.UserRepository;
import com.aalmeida.myfinances.service.impl.UserServiceImpl;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {
	
	UserService service;
	UserRepository repository;
	
	@BeforeEach
	public void setUp() {
		repository = Mockito.mock(UserRepository.class);
		service = new UserServiceImpl(repository);
	}
			
	@Test
	public void shouldValidateEmail(){
		//cenario
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao
		service.emailValidate("email@email.com");
	}
	
	@Test
	public void mustShowErrorWhenValidatingEmailWhenThereIsRegistered() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao
		//junit 5
		Assertions.assertThrows(BusinessRuleException.class, () -> service.emailValidate("email@email.com"));
	}
}
