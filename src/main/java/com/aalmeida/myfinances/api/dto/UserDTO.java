package com.aalmeida.myfinances.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {
	private String email;
	private String name;
	private String password;
}
