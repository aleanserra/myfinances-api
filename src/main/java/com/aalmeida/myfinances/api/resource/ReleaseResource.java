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

import com.aalmeida.myfinances.api.dto.ReleaseDTO;
import com.aalmeida.myfinances.api.dto.UpdateStatusDTO;
import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.model.entity.Release;
import com.aalmeida.myfinances.model.entity.User;
import com.aalmeida.myfinances.model.enums.ReleaseStatus;
import com.aalmeida.myfinances.model.enums.ReleaseType;
import com.aalmeida.myfinances.service.ReleaseService;
import com.aalmeida.myfinances.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
public class ReleaseResource {
	
	private final ReleaseService service;
	private final UserService userService;
	
	@GetMapping
	public ResponseEntity search(
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "month", required = false) Integer month,
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam("user") Long idUser
			) {
		Release releaseFilter = new Release();
		releaseFilter.setDescription(description);
		releaseFilter.setMonth(month);
		releaseFilter.setYear(year);
		
		Optional<User> user = userService.getById(idUser);
		
		if(!user.isPresent()) {
			return ResponseEntity.badRequest().body("Can not search. User not found with given id");
		}else {
			releaseFilter.setUser(user.get());
		}
		
		List<Release> releases = service.search(releaseFilter);
		
		return ResponseEntity.ok(releases);
	}
	
	@PostMapping
	public ResponseEntity save(@RequestBody ReleaseDTO dto) {
		
		try {
		Release entity = convert(dto);
		service.save(entity);
		return new  ResponseEntity(entity, HttpStatus.CREATED) ;
		}catch (BusinessRuleException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ReleaseDTO dto) {
		return service.getById(id).map(entity -> {
			try {
				Release release = convert(dto);
				release.setId(entity.getId());
				service.update(release);
				return ResponseEntity.ok(release);
			}catch (BusinessRuleException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> 
			new ResponseEntity("Relese not found in DB.", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/update-status")
	public ResponseEntity updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusDTO dto){
		return service.getById(id).map(entity ->{
			ReleaseStatus selectedStatus = ReleaseStatus.valueOf(dto.getStatus());
			
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
		new ResponseEntity("Relese not found in DB.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity delete(@PathVariable("id") Long id) {
		return service.getById(id).map(entity ->{
			service.delete(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() ->
		new ResponseEntity("Relese not found in DB.", HttpStatus.BAD_REQUEST));
	}
	
	private Release convert(ReleaseDTO dto) {
		
		Release release = new Release();
		release.setId(dto.getId());
		release.setDescription(dto.getDescription());
		release.setYear(dto.getYear());
		release.setMonth(dto.getMonth());
		release.setValue(dto.getValue());
		
		User user = userService.getById(dto.getUser()).orElseThrow (() -> new BusinessRuleException("User not found with the given id."));
	
		release.setUser(user);
		
		if(dto.getType() != null) {
		
			release.setType(ReleaseType.valueOf(dto.getType()));
		}
		
		if(dto.getStatus() != null){
			
			release.setStatus(ReleaseStatus.valueOf(dto.getStatus()));
		}
		
		return release;
	}
}
