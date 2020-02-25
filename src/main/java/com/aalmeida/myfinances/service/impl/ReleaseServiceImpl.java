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
import com.aalmeida.myfinances.model.entity.Release;
import com.aalmeida.myfinances.model.enums.ReleaseStatus;
import com.aalmeida.myfinances.model.enums.ReleaseType;
import com.aalmeida.myfinances.model.repository.ReleaseRepository;
import com.aalmeida.myfinances.service.ReleaseService;

@Service
public class ReleaseServiceImpl implements ReleaseService {
	
	private ReleaseRepository repository;
	
	public ReleaseServiceImpl (ReleaseRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Release save(Release release) {
		
		validate(release);
		release.setStatus(ReleaseStatus.PENDING);
		return repository.save(release);
	}

	@Override
	@Transactional
	public Release update(Release release) {
		
		Objects.requireNonNull(release.getId());
		validate(release);
		return repository.save(release);
	}

	@Override
	@Transactional
	public void delete(Release release) {
		
		Objects.requireNonNull(release.getId());
		repository.delete(release);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Release> search(Release releaseFilter) {
		
		Example example = Example.of(releaseFilter,
				ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING) );
		
		return repository.findAll(example);
	}

	@Override
	public void updateStatus(Release release, ReleaseStatus status) {
		
		release.setStatus(status);
		update(release);
	}

	@Override
	public void validate(Release release) {
		
		if(release.getDescription() == null || release.getDescription().trim().contentEquals("")) {
			throw new BusinessRuleException("Invalid description");
		}
		
		if(release.getMonth() == null || release.getMonth() < 1 || release.getMonth() > 12) {
			throw new BusinessRuleException("Invalid month");
		}
		
		if(release.getYear() == null || release.getYear().toString().length() != 4) {
			throw new BusinessRuleException("Invalid year");
		}
		
		if(release.getUser() == null || release.getUser().getId() == null) {
			throw new BusinessRuleException("Invalid user");
		}
		
		if(release.getValue() == null || release.getValue().compareTo(BigDecimal.ZERO) < 1) {
			throw new BusinessRuleException("Invalid value");
		}
		
		if(release.getType() == null) {
			throw new BusinessRuleException("Invalid type");
		}
	}

	@Override
	public Optional<Release> getById(Long id) {
		
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obtainBalanceByUser(Long id) {
		BigDecimal income = repository.obtainBalanceByReleaseTypeAndUser(id, ReleaseType.INCOME);
		BigDecimal outgo = repository.obtainBalanceByReleaseTypeAndUser(id, ReleaseType.OUTGO);

		if(income == null){
			income = BigDecimal.ZERO;
		}

		if(outgo == null){
			outgo = BigDecimal.ZERO;
		}

		return income.subtract(outgo);
	}
}
