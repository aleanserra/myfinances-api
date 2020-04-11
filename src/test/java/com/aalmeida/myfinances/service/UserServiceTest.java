package com.aalmeida.myfinances.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.exceptions.ErrorAuthenticate;
import com.aalmeida.myfinances.model.entity.User;
import com.aalmeida.myfinances.model.repository.UserRepository;
import com.aalmeida.myfinances.service.impl.UserServiceImpl;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {
	
	@SpyBean
	UserServiceImpl service;
	
	@MockBean
	UserRepository repository;
	
	@Test
	public void shouldSaveUser() {
		//cenario
		Mockito.doNothing().when(service).emailValidate(Mockito.anyString());
		User user = User.builder().id(1l).name("name").email("email@email.com").password("password").build();
		
		Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);
		
		//acao
		User userSaved = service.saveUser(new User());
		
		//verificacao
		Assertions.assertThat(userSaved).isNotNull();
		Assertions.assertThat(userSaved.getId()).isEqualTo(1l);
		Assertions.assertThat(userSaved.getName()).isEqualTo("name");
		Assertions.assertThat(userSaved.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(userSaved.getPassword()).isEqualTo("password");
	}
	@Test
	public void mustNotSaveAUserWithEmailRegistred() {
		//cenario
		String email = "email@email.com";
		User user = User.builder().email("email@email.com").build();
		Mockito.doThrow(BusinessRuleException.class).when(service).emailValidate(email);
		
		//acao
		org.junit.jupiter.api.Assertions.assertThrows(BusinessRuleException.class, () -> service.saveUser(user));
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).save(user);
		
	}
			
	@Test
	public void shouldAuthenticateAUserWithSucsess() {
		String email = "email@email.com";
		String password = "password";
		
		User user = User.builder().email(email).password(password).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(user));
		
		//acao
		User result = service.authenticate(email, password);
		
		//verificacao
		Assertions.assertThat(result).isNotNull();
	} 
	
	@Test
	public void shouldShowErrorWhenUserDontFoundByEmail() {
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao
		Throwable exception = Assertions.catchThrowable( () -> service.authenticate("email@email.com","password"));
		
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErrorAuthenticate.class).hasMessage("User not found for the given email");
	}
	
	@Test
	public void shouldShowErrorWhenInvalidPassword() {
		
		//cenario
		String password = "password";
		
		User user = User.builder().email("email@email.com").password(password).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
		
		//acao
		Throwable exception = Assertions.catchThrowable( () -> service.authenticate("email@email.com", "123"));
		
		Assertions.assertThat(exception).isInstanceOf(ErrorAuthenticate.class).hasMessage("Invalid password");
		
	}
	
	@Test
	public void shouldValidateEmail(){
		//cenario
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao
		service.emailValidate("email@email.com");
	}
	
	@Test
	public void shouldShowErrorWhenValidatingEmailWhenThereIsRegistered() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao
		//junit 5
		org.junit.jupiter.api.Assertions.assertThrows(BusinessRuleException.class, () -> service.emailValidate("email@email.com"));
	}
}
