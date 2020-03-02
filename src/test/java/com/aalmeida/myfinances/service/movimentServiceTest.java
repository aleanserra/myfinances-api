package com.aalmeida.myfinances.service;

import com.aalmeida.myfinances.model.entity.Moviment;
import com.aalmeida.myfinances.model.enums.MovimentStatus;
import com.aalmeida.myfinances.model.repository.MovimentRepository;
import com.aalmeida.myfinances.model.repository.MovimentRepositoryTest;
import com.aalmeida.myfinances.service.impl.MovimentServiceImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * movimentServiceTest
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class movimentServiceTest {

    @SpyBean
    MovimentServiceImpl service;

    @MockBean
    MovimentRepository repository;

    @Test
    public void shoulSaveAMoviment(){
        
        //given
        Moviment movimentToSave = MovimentRepositoryTest.createMoviment();

        Mockito.doNothing().when(service).validate(movimentToSave);
        
        Moviment movimentSaved = MovimentRepositoryTest.createMoviment();
        movimentSaved.setId(1l);
        movimentSaved.setStatus(MovimentStatus.RELEASED);
        Mockito.when(repository.save(movimentToSave)).thenReturn(movimentSaved);
        
        //when
        Moviment moviment = service.save(movimentToSave);
        
        //then
        Assertions.assertThat(moviment.getId()).isEqualTo(movimentSaved.getId());
        Assertions.assertThat(moviment.getStatus()).isEqualTo(MovimentStatus.RELEASED);
    }

    public void shouldNotSaveAMovimentWhenErrorValidate(){
        
    }
}