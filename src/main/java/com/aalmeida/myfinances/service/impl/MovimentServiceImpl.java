package com.aalmeida.myfinances.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.model.entity.Moviment;
import com.aalmeida.myfinances.model.enums.MovimentStatus;
import com.aalmeida.myfinances.model.enums.MovimentType;
import com.aalmeida.myfinances.model.repository.MovimentRepository;
import com.aalmeida.myfinances.service.MovimentService;

@Service
public class MovimentServiceImpl implements MovimentService {
	
	private MovimentRepository repository;
	
	public MovimentServiceImpl (MovimentRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Moviment save(Moviment moviment) {
		
		validate(moviment);
		moviment.setStatus(MovimentStatus.PENDING);
		return repository.save(moviment);
	}

	@Override
	@Transactional
	public Moviment update(Moviment moviment) {
		
		Objects.requireNonNull(moviment.getId());
		validate(moviment);
		return repository.save(moviment);
	}

	@Override
	@Transactional
	public void delete(Moviment moviment) {
		
		Objects.requireNonNull(moviment.getId());
		repository.delete(moviment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Moviment> search(Moviment movimentFilter) {
		
		Example example = Example.of(movimentFilter,
				ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING) );
		
		return repository.findAll(example);
	}

	@Override
	public void updateStatus(Moviment moviment, MovimentStatus status) {
		
		moviment.setStatus(status);
		update(moviment);
	}

	@Override
	public void validate(Moviment moviment) {
		
		if(moviment.getDescription() == null || moviment.getDescription().trim().contentEquals("")) {
			throw new BusinessRuleException("Invalid description");
		}
		
		if(moviment.getMonth() == null || moviment.getMonth() < 1 || moviment.getMonth() > 12) {
			throw new BusinessRuleException("Invalid month");
		}
		
		if(moviment.getYear() == null || moviment.getYear().toString().length() != 4) {
			throw new BusinessRuleException("Invalid year");
		}
		
		if(moviment.getUser() == null || moviment.getUser().getId() == null) {
			throw new BusinessRuleException("Invalid user");
		}
		
		if(moviment.getValue() == null || moviment.getValue().compareTo(BigDecimal.ZERO) < 1) {
			throw new BusinessRuleException("Invalid value");
		}
		
		if(moviment.getType() == null) {
			throw new BusinessRuleException("Invalid type");
		}
	}

	@Override
	public Optional<Moviment> getById(Long id) {
		
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obtainBalanceByUser(Long id) {
		BigDecimal income = repository.obtainBalanceByMovimentTypeAndUser(id, MovimentType.INCOME);
		BigDecimal outgo = repository.obtainBalanceByMovimentTypeAndUser(id, MovimentType.OUTGO);

		if(income == null){
			income = BigDecimal.ZERO;
		}

		if(outgo == null){
			outgo = BigDecimal.ZERO;
		}

		return income.subtract(outgo);
	}
}
