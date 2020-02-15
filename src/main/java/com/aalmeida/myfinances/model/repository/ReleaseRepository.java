package com.aalmeida.myfinances.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aalmeida.myfinances.model.entity.Release;

public interface ReleaseRepository extends JpaRepository<Release, Long>{

}
