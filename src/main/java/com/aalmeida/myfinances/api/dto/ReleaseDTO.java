package com.aalmeida.myfinances.api.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReleaseDTO {
	
	private Long id;
	private String description;
	private Integer month;
	private Integer year;
	private BigDecimal value;
	private Long user;
	private String type;
	private String status;
}
