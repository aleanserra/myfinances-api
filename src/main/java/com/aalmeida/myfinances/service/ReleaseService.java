package com.aalmeida.myfinances.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.aalmeida.myfinances.model.entity.Release;
import com.aalmeida.myfinances.model.enums.ReleaseStatus;

public interface ReleaseService {
	
	Release save(Release release);
	
	Release update(Release release);
	
	void delete(Release release);
	
	List<Release> search(Release releaseFilter);
	
	void updateStatus(Release release, ReleaseStatus status);
	
	void validate(Release release);
	
	Optional<Release> getById(Long id);

	BigDecimal obtainBalanceByUser(Long id);
}
