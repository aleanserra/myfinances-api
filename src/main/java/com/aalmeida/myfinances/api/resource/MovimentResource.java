package com.aalmeida.myfinances.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aalmeida.myfinances.api.dto.MovimentDTO;
import com.aalmeida.myfinances.api.dto.UpdateStatusDTO;
import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.model.entity.Moviment;
import com.aalmeida.myfinances.model.entity.User;
import com.aalmeida.myfinances.model.enums.MovimentStatus;
import com.aalmeida.myfinances.model.enums.MovimentType;
import com.aalmeida.myfinances.service.MovimentService;
import com.aalmeida.myfinances.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/moviments")
@RequiredArgsConstructor
public class MovimentResource {
	
	private final MovimentService service;
	private final UserService userService;
	
	@GetMapping
	public ResponseEntity search(
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "month", required = false) Integer month,
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam("user") Long idUser
			) {
		Moviment movimentFilter = new Moviment();
		movimentFilter.setDescription(description);
		movimentFilter.setMonth(month);
		movimentFilter.setYear(year);
		
		Optional<User> user = userService.getById(idUser);
		
		if(!user.isPresent()) {
			return ResponseEntity.badRequest().body("Can not search. User not found with given id");
		}else {
			movimentFilter.setUser(user.get());
		}
		
		List<Moviment> moviments = service.search(movimentFilter);
		
		return ResponseEntity.ok(moviments);
	}
	
	@PostMapping
	public ResponseEntity save(@RequestBody MovimentDTO dto) {
		
		try {
		Moviment entity = convert(dto);
		entity = service.save(entity);
		return new  ResponseEntity(entity, HttpStatus.CREATED) ;
		}catch (BusinessRuleException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity update(@PathVariable("id") Long id, @RequestBody MovimentDTO dto) {
		return service.getById(id).map(entity -> {
			try {
				Moviment moviment = convert(dto);
				moviment.setId(entity.getId());
				service.update(moviment);
				return ResponseEntity.ok(moviment);
			}catch (BusinessRuleException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> 
			new ResponseEntity("Moviment not found in DB.", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/update-status")
	public ResponseEntity updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusDTO dto){
		return service.getById(id).map(entity ->{
			MovimentStatus selectedStatus = MovimentStatus.valueOf(dto.getStatus());
			
			if(selectedStatus == null) {
				return ResponseEntity.badRequest().body("Can not be updated. Invalid status.");
			}
			try {
				
				entity.setStatus(selectedStatus);
				service.update(entity);
				return ResponseEntity.ok(entity);
			}catch (BusinessRuleException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> 
		new ResponseEntity("Moviment not found in DB.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity delete(@PathVariable("id") Long id) {
		return service.getById(id).map(entity ->{
			service.delete(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() ->
		new ResponseEntity("Moviment not found in DB.", HttpStatus.BAD_REQUEST));
	}
	
	//Converte o DTO de movimento em entidade JPA
	private Moviment convert(MovimentDTO dto) {
		
		Moviment moviment = new Moviment();
		moviment.setId(dto.getId());
		moviment.setDescription(dto.getDescription());
		moviment.setYear(dto.getYear());
		moviment.setMonth(dto.getMonth());
		moviment.setValue(dto.getValue());
		
		User user = userService.getById(dto.getUser()).orElseThrow (() -> new BusinessRuleException("User not found with the given id."));
	
		moviment.setUser(user);
		
		if(dto.getType() != null) {
		
			moviment.setType(MovimentType.valueOf(dto.getType()));
		}
		
		if(dto.getStatus() != null){
			
			moviment.setStatus(MovimentStatus.valueOf(dto.getStatus()));
		}
		
		return moviment;
	}
}
