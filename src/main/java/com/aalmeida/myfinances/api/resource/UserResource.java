package com.aalmeida.myfinances.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

import com.aalmeida.myfinances.api.dto.UserDTO;
import com.aalmeida.myfinances.exceptions.ErrorAuthenticate;
import com.aalmeida.myfinances.model.entity.User;
import com.aalmeida.myfinances.service.ReleaseService;
import com.aalmeida.myfinances.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserResource {
	
	private final UserService service;
	private final ReleaseService releaseService;
	
	@PostMapping("/authenticate")
	public ResponseEntity authenticate(@RequestBody UserDTO dto) {
		try {
			User userAuthenticated = service.authenticate(dto.getEmail(), dto.getPassword());
			return ResponseEntity.ok(userAuthenticated);
		} catch (ErrorAuthenticate e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity save( @RequestBody UserDTO dto) {
		
		User user = User.builder()
				.name(dto.getName())
				.email(dto.getEmail())
				.password(dto.getPassword())
				.build();
	
		try {
			User userSaved = service.saveUser(user);
			return new ResponseEntity(userSaved, HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
@GetMapping("{id}/balance")
	public ResponseEntity obtainBalance(@PathVariable("id") Long id){

		Optional<User> user = service.getById(id);
		if(!user.isPresent()){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		BigDecimal balance = releaseService.obtainBalanceByUser(id);

		return ResponseEntity.ok(balance);
	}
}
