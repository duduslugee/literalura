package br.duduslugee.literalura.repository;

import br.duduslugee.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByAnoNascimentoBeforeAndAnoFalecimentoAfter(int anoNascimento, int anoFalecimento);
    Autor findByNome(String nome);
}
