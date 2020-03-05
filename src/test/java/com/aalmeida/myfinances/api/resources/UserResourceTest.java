package com.aalmeida.myfinances.api.resources;

import com.aalmeida.myfinances.api.dto.UserDTO;
import com.aalmeida.myfinances.api.resource.UserResource;
import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.exceptions.ErrorAuthenticate;
import com.aalmeida.myfinances.model.entity.User;
import com.aalmeida.myfinances.service.MovimentService;
import com.aalmeida.myfinances.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserResource.class)
@AutoConfigureMockMvc
public class UserResourceTest {

     static final String API = "/api/users";
     static final MediaType JSON = MediaType.APPLICATION_JSON;


    @Autowired
    MockMvc mvc;

    @MockBean
    UserService service;
    
    @MockBean
    MovimentService movimentService;

    
    @Test
    public void shouldAutenticateAUser() throws Exception{

        //given
        String email = "user@email.com";
        String password = "123";
        
        UserDTO dto = UserDTO.builder().email(email).password(password).build();
        User user = User.builder().id(1l).email(email).password(password).build();

        Mockito.when(service.authenticate(email,password)).thenReturn(user);

        String json = new ObjectMapper().writeValueAsString(dto);

        //when and then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                    .post(API.concat("/authenticate"))
                                                    .accept(JSON)
                                                    .contentType(JSON)
                                                    .content(json);

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()) )
            .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()) )
            .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()) )            
        ;
        
    }
    @Test
    public void shouldReturnBadRequestWhenGetErrorAuthenticate() throws Exception{

        //given
        String email = "user@email.com";
        String password = "123";
        
        UserDTO dto = UserDTO.builder().email(email).password(password).build();

        Mockito.when(service.authenticate(email,password)).thenThrow(ErrorAuthenticate.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        //when and then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                    .post(API.concat("/authenticate"))
                                                    .accept(JSON)
                                                    .contentType(JSON)
                                                    .content(json);

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
        ;
        
    }

    @Test
    public void shouldCreateANewUser() throws Exception{

        //given
        String email = "user@email.com";
        String password = "123";
        
        UserDTO dto = UserDTO.builder().email(email).password(password).build();
        User user = User.builder().id(1l).email(email).password(password).build();

        Mockito.when(service.saveUser(Mockito.any(User.class))).thenReturn(user);

        String json = new ObjectMapper().writeValueAsString(dto);

        //when and then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                    .post(API)
                                                    .accept(JSON)
                                                    .contentType(JSON)
                                                    .content(json);

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()) )
            .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()) )
            .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()) )            
        ;
        
    }
    @Test
    public void shouldReturnBadRequestWhenCreateAInvalidUser() throws Exception{

        //given
        String email = "user@email.com";
        String password = "123";
        
        UserDTO dto = UserDTO.builder().email(email).password(password).build();

        Mockito.when(service.saveUser(Mockito.any(User.class))).thenThrow(BusinessRuleException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        //when and then
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                    .post(API)
                                                    .accept(JSON)
                                                    .contentType(JSON)
                                                    .content(json);

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}