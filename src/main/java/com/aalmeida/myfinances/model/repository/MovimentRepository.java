package com.aalmeida.myfinances.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

import com.aalmeida.myfinances.model.entity.Moviment;
import com.aalmeida.myfinances.model.enums.MovimentStatus;
import com.aalmeida.myfinances.model.enums.MovimentType;

public interface MovimentRepository extends JpaRepository<Moviment, Long> {

    @Query(value = "select sum(r.value) from Moviment r join r.user u where u.id = :idUser and r.type =:type and r.status = :status group by u")
    BigDecimal obtainBalanceByMovimentTypeAndUserAndStatus(@Param("idUser") Long idUser,
            @Param("type") MovimentType type, @Param("status") MovimentStatus status);
}
