package br.duduslugee.literalura.repository;

import br.duduslugee.literalura.model.Livro;
import br.duduslugee.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findTop10ByOrderByDownloadsDesc();

    List<Livro> findByAutorNomeContainingIgnoreCase(String nome);
}
