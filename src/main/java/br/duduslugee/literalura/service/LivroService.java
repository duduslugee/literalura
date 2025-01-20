package br.duduslugee.literalura.service;

import br.duduslugee.literalura.model.*;
import br.duduslugee.literalura.repository.AutorRepository;
import br.duduslugee.literalura.repository.LivroRepository;
import br.duduslugee.literalura.client.GutendexClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@Service
public class LivroService {

    private Scanner leitura = new Scanner(System.in);
    private GutendexClient gutendexClient = new GutendexClient();  // Cliente da API Gutendex
    private ConverteDados conversor = new ConverteDados();
    private int contador;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public LivroService() {}

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 9) {
            var menu = """
                    ***Literalura Livros ***
                    
                    <------------MENU------------> 
                                       
                    1 - Buscar livros por título.
                    2 - Buscar livros por autores.
                    3 - Listar livros.
                    4 - Listar autores.
                    5 - Listar autores vivos em determinado ano.
                    6 - Buscar quantidade de livro por idioma.
                    7 - Buscar top 10 livros mais baixados.
                    
                    9 - Sair.
                    """;
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1 -> buscarLivros();
                case 2 -> buscarAutores();
                case 3 -> listarLivros();
                case 4 -> listarAutores();
                case 5 -> pesquisarDadosDeAutor();
                case 6 -> quantidadeDeLivrosPorIdioma();
                case 7 -> top10LivrosMaisBaixados();
                case 9 -> {
                    System.out.println("Saindo da pesquisa!");
                    System.exit(0);
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void buscarLivros() {
        System.out.println("Digite o nome do livro para busca: ");
        var nomeLivro = leitura.nextLine();
        try {
            String json = gutendexClient.buscarLivroPorTitulo(nomeLivro);
            DadosResultado dadosResultado = conversor.obterDados(json, DadosResultado.class);
            exibirInformacoesAutor(dadosResultado);

            for (DadosLivro dadosLivro : dadosResultado.livro()) {
                Livro livro = new Livro(dadosLivro);
                for (DadosAutor dadosAutor : dadosLivro.autores()) {
                    Autor autor = new Autor(dadosAutor);
                    if (dadosAutor.anoNascimento() == null) {
                        autor.setAnoNascimento(0);
                    }
                    if (dadosAutor.anoFalecimento() == null) {
                        autor.setAnoFalecimento(0);
                    }
                    livro.getAutor().add(autor);
                }
                livroRepository.save(livro);  // Salva livro na base de dados
            }
        } catch (IOException e) {
            System.out.println("Erro ao buscar livro: " + e.getMessage());
        }
    }

    private void exibirInformacoesAutor(DadosResultado dadosResultado) {
        DadosLivro livro = dadosResultado.livro().get(0); // Obtendo o primeiro livro

        System.out.println("Nome do livro: " + livro.titulo());
        System.out.println("Nome do autor: " + livro.autores().get(0).nomeAutor());
        System.out.println("Autor nascido no ano: " + livro.autores().get(0).anoNascimento());
        System.out.println("Autor falecido no ano: " + livro.autores().get(0).anoFalecimento());
    }

    private void buscarAutores() {
        System.out.println("Digite o nome do autor desejado: ");
        String nomeAutor = leitura.nextLine();
        List<Livro> livrosEncontrados = livroRepository.findByAutorNomeContainingIgnoreCase(nomeAutor);
        if (livrosEncontrados.isEmpty()) {
            System.out.println("Nenhum livro encontrado para o autor " + nomeAutor);
        } else {
            System.out.println("Livros encontrados para o autor " + nomeAutor + ":");
            livrosEncontrados.forEach(livro -> {
                System.out.println("Título: " + livro.getTitulo());
                System.out.println("Idioma: " + livro.getIdioma());
                System.out.println("Total de Downloads: " + livro.getTotalDownloads());
                System.out.println("-----------");
            });
        }
    }

    private void listarLivros() {
        List<Livro> livros = livroRepository.findAll();
        if (!livros.isEmpty()) {
            livros.forEach(livro -> System.out.println("O nome do livro é: " + livro.getTitulo()));
        } else {
            System.out.println("Nenhum livro encontrado! ");
        }
    }

    private void listarAutores() {
        List<Autor> autoresEncontrados = autorRepository.findAll();
        autoresEncontrados.forEach(autor -> {
            System.out.println("Autor encontrado: " + autor.getNome() + " | ★ Nascido no ano: "
                    + autor.getAnoNascimento() + " | ✞ Falecido no ano: " + autor.getAnoFalecimento());
        });
    }

    private void pesquisarDadosDeAutor() {
        System.out.println("Digite o ano para busca: ");
        var anoBuscado = leitura.nextInt();
        List<Autor> autoresEncontrados = autorRepository.findAll();
        autoresEncontrados.stream().forEach(data -> {
            if (anoBuscado >= data.getAnoNascimento() && anoBuscado <= data.getAnoFalecimento()) {
                System.out.println("O Autor: " + data.getNome() + " ★ estava vivo!");
            }
        });
    }

    private void quantidadeDeLivrosPorIdioma() {
        System.out.println("Digite o idioma para consulta: ");
        var idiomaSelecionado = leitura.nextLine();
        contador = 0;
        List<Livro> livrosEncontrados = livroRepository.findAll();
        livrosEncontrados.stream().forEach(contagem -> {
            if (contagem.getIdioma().contains(idiomaSelecionado)) {
                contador++;
            }
        });
        System.out.println("A quantidade de livros nesse idioma é: " + contador);
    }

    private void top10LivrosMaisBaixados() {
        List<Livro> topLivros = livroRepository.findTop10ByOrderByDownloadsDesc();
        topLivros.forEach(top -> {
            System.out.println(" ");
            System.out.println("*----------*");
            System.out.println("O livro: " + top.getTitulo());
            System.out.println("Do autor " + top.getAutor());
            System.out.println("Obteve: " + top.getTotalDownloads() + " downloads!");
            System.out.println("*----------*");
            System.out.println(" ");
        });
    }
}
