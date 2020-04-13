package com.aalmeida.myfinances.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.aalmeida.myfinances.exceptions.BusinessRuleException;
import com.aalmeida.myfinances.model.entity.Moviment;
import com.aalmeida.myfinances.model.entity.User;
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
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MovimentServiceTest {

    @SpyBean
    MovimentServiceImpl service;

    @MockBean
    MovimentRepository repository;

    @Test
    public void shoulSaveAMoviment() {

        // given
        Moviment movimentToSave = MovimentRepositoryTest.createMoviment();

        Mockito.doNothing().when(service).validate(movimentToSave);

        Moviment movimentSaved = MovimentRepositoryTest.createMoviment();
        movimentSaved.setId(1l);
        movimentSaved.setStatus(MovimentStatus.RELEASED);
        Mockito.when(repository.save(movimentToSave)).thenReturn(movimentSaved);

        // when
        Moviment moviment = service.save(movimentToSave);

        // then
        Assertions.assertThat(moviment.getId()).isEqualTo(movimentSaved.getId());
        Assertions.assertThat(moviment.getStatus()).isEqualTo(MovimentStatus.RELEASED);
    }

    @Test
    public void shouldNotSaveAMovimentWhenErrorValidate() {
        Moviment movimentToSave = MovimentRepositoryTest.createMoviment();
        Mockito.doThrow(BusinessRuleException.class).when(service).validate(movimentToSave);

        Assertions.catchThrowableOfType(() -> service.save(movimentToSave), BusinessRuleException.class);

        Mockito.verify(repository, Mockito.never()).save(movimentToSave);
    }

    @Test
    public void shouldUpdateAMoviment() {

        // given
        Moviment movimentSaved = MovimentRepositoryTest.createMoviment();
        movimentSaved.setId(1l);
        movimentSaved.setStatus(MovimentStatus.PENDING);

        Mockito.doNothing().when(service).validate(movimentSaved);

        Mockito.when(repository.save(movimentSaved)).thenReturn(movimentSaved);

        // when
        service.update(movimentSaved);

        // then
        Mockito.verify(repository, Mockito.times(1)).save(movimentSaved);
    }

    @Test
    public void shouldThrowErrorWhenUpdateAMovimentNotSaved() {
        Moviment movimentToSave = MovimentRepositoryTest.createMoviment();

        Assertions.catchThrowableOfType(() -> service.update(movimentToSave), NullPointerException.class);

        Mockito.verify(repository, Mockito.never()).save(movimentToSave);
    }

    @Test
    public void shouldDeleteAMoviment() {
        // given
        Moviment moviment = MovimentRepositoryTest.createMoviment();
        moviment.setId(1l);

        // when
        service.delete(moviment);

        // then
        Mockito.verify(repository).delete(moviment);

    }

    @Test
    public void shouldThrowErrorWhenTryToDeleteAMovimentNotSaved() {
        // given
        Moviment moviment = MovimentRepositoryTest.createMoviment();

        // when
        Assertions.catchThrowableOfType(() -> service.delete(moviment), NullPointerException.class);

        // thenn
        Mockito.verify(repository, Mockito.never()).delete(moviment);
    }

    @Test
    public void shouldFilterMoviments() {

        Moviment moviment = MovimentRepositoryTest.createMoviment();
        moviment.setId(1l);

        List<Moviment> list = Arrays.asList(moviment);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(list);

        List<Moviment> result = service.search(moviment);

        Assertions.assertThat(result).isNotEmpty().hasSize(1).contains(moviment);

    }

    @Test
    public void shouldUpdateMovimentStatus() {
        Moviment moviment = MovimentRepositoryTest.createMoviment();
        moviment.setId(1l);
        moviment.setStatus(MovimentStatus.PENDING);

        MovimentStatus newStatus = MovimentStatus.RELEASED;
        Mockito.doReturn(moviment).when(service).update(moviment);

        service.updateStatus(moviment, newStatus);

        Assertions.assertThat(moviment.getStatus()).isEqualTo(newStatus);
        Mockito.verify(service).update(moviment);
    }

    @Test
    public void shouldGetAMovimentById() {
        Long id = 1l;

        Moviment moviment = MovimentRepositoryTest.createMoviment();
        moviment.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(moviment));

        Optional<Moviment> result = service.getById(id);

        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void shouldReturnNullWhenMovimentNotExist() {
        Long id = 1l;

        Moviment moviment = MovimentRepositoryTest.createMoviment();
        moviment.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Moviment> result = service.getById(id);

        Assertions.assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void shouldThrowErrorWhenValidateAMoviment() {
        Moviment moviment = new Moviment();

        Throwable error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid description");

        moviment.setDescription("");

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid description");

        moviment.setDescription("Salary");

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid month");

        moviment.setYear(0);

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid month");

        moviment.setYear(13);

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid month");

        moviment.setMonth(1);

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid year");

        moviment.setYear(202);

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid year");

        moviment.setYear(2020);

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid user");

        moviment.setUser(new User());

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid user");

        moviment.getUser().setId(1l);

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid value");

        moviment.setValue(BigDecimal.ZERO);

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid value");

        moviment.setValue(BigDecimal.valueOf(1));

        error = Assertions.catchThrowable(() -> service.validate(moviment));
        Assertions.assertThat(error).isInstanceOf(BusinessRuleException.class).hasMessage("Invalid type");
    }
}
