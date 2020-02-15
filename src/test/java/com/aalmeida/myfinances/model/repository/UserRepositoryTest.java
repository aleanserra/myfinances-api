package com.aalmeida.myfinances.model.repository;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.aalmeida.myfinances.model.entity.User;

//@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void shouldCheckForAnEmail() {
		
		//cenário
		
		User user = createUser();
		entityManager.persist(user);
		
		//repository.save(user);
		
		//ação / execução
		
		boolean result = repository.existsByEmail("user@email.com");
		
		//verificação
		
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void mustReturnFalseWhenThereIsNoUserRegisteredWithTheEmail() {
		
		//cenário
		
		//repository.deleteAll();
		
		//ação
		
		boolean result = repository.existsByEmail("user@email.com");
		
		//verificação
		
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void shouldPersistAUserInDB() {
		//cenario
		User user = createUser();
		
		//ação
		User userSaved = repository.save(user);
		
		//verificação
		Assertions.assertThat(userSaved.getId()).isNotNull();
	}
	
	@Test
	public void shouldSearchAUserByEmail() {
		//cenario
		User user = createUser();
		entityManager.persist(user);
				
		//verificação
		Optional<User> result = repository.findByEmail("user@email.com");
		
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void shouldReturnNullAUserByEmailWhenNotExistInDB() {
		//cenario
	
				
		//verificação
		Optional<User> result = repository.findByEmail("user@email.com");
		
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	public static User createUser() {
		return 	User
				.builder()
				.name("user")
				.email("user@email.com")
				.password("password")
				.build();

	}
}












