package com.aalmeida.myfinances.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

import com.aalmeida.myfinances.model.entity.Release;
import com.aalmeida.myfinances.model.enums.ReleaseType;

public interface ReleaseRepository extends JpaRepository<Release, Long>{

    @Query(value = "select sum(r.value) from Release r join r.user u where u.id = :idUser and r.type =:type group by u")
    BigDecimal obtainBalanceByReleaseTypeAndUser(@Param("idUser") Long idUser, @Param("type") ReleaseType type);
}
