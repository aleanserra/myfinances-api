package com.aalmeida.myfinances.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.aalmeida.myfinances.model.entity.Moviment;
import com.aalmeida.myfinances.model.enums.MovimentStatus;

public interface MovimentService {
	
	Moviment save(Moviment moviment);
	
	Moviment update(Moviment moviment);
	
	void delete(Moviment moviment);
	
	List<Moviment> search(Moviment movimentFilter);
	
	void updateStatus(Moviment moviment, MovimentStatus status);
	
	void validate(Moviment moviment);
	
	Optional<Moviment> getById(Long id);

	BigDecimal obtainBalanceByUser(Long id);
}
