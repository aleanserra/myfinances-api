package com.aalmeida.myfinances.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.aalmeida.myfinances.model.enums.MovimentStatus;
import com.aalmeida.myfinances.model.enums.MovimentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name="moviment", schema ="finances")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moviment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="month")
	private Integer month;
	
	@Column(name="year")
	private Integer year;
	
	@Column(name="description")
	private String description;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(name="value")
	private BigDecimal value;
	
	@Column(name="reg_moviment_date")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate movimentDate;
	
	@Column(name = "type")
	@Enumerated(value = EnumType.STRING)
	private MovimentType type;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private MovimentStatus status;
	
}
