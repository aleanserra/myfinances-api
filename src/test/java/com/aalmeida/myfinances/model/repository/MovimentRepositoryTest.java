package com.aalmeida.myfinances.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.aalmeida.myfinances.model.entity.Moviment;
import com.aalmeida.myfinances.model.enums.MovimentStatus;
import com.aalmeida.myfinances.model.enums.MovimentType;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class MovimentRepositoryTest{

    @Autowired
    MovimentRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmMovimento(){
        Moviment moviment = createMoviment();

        moviment = repository.save(moviment);

        assertThat(moviment.getId()).isNotNull();
    }

    @Test
    public void deveDeletarUmMovimento(){
        Moviment moviment = createAndPersistMoviment();

        moviment = entityManager.find(Moviment.class, moviment.getId());
        
        repository.delete(moviment);

        Moviment movimentNonexistent = entityManager.find(Moviment.class, moviment.getId());

        assertThat(movimentNonexistent).isNull();
    }
    @Test
    public void deveAtualizarUmMovimento(){
        Moviment moviment = createAndPersistMoviment();

        moviment.setYear(2018);
        moviment.setDescription("Teste update");
        moviment.setStatus(MovimentStatus.CANCELED);
        
        repository.save(moviment);

        Moviment movimentUpdate = entityManager.find(Moviment.class, moviment.getId());

        assertThat(movimentUpdate.getYear()).isEqualTo(2018);
        assertThat(movimentUpdate.getDescription()).isEqualTo("Teste update");
        assertThat(movimentUpdate.getStatus()).isEqualTo(MovimentStatus.CANCELED);
    }

    @Test
    public void deveBuscarUmMovimentoPorId(){
        Moviment moviment = createAndPersistMoviment();

        Optional<Moviment> movimentFound = repository.findById(moviment.getId());

        assertThat(movimentFound.isPresent()).isTrue();
    }

    private Moviment createAndPersistMoviment(){
        Moviment moviment = createMoviment();
        entityManager.persist(moviment);
        return moviment;
    }

    private Moviment createMoviment(){
        return Moviment.builder()
                            .year(2020)
                            .month(1)
                            .description("any moviment")
                            .value(BigDecimal.valueOf(10))
                            .type(MovimentType.INCOME)
                            .status(MovimentStatus.PENDING)
                            .movimentDate(LocalDate.now())
                            .build();
    }
}