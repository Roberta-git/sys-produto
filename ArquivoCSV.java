package org.sysproduto.utils;

import org.sysproduto.model.Categoria;
import org.sysproduto.model.Produto;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArquivoCSV {

    private static final String CAMINHO_ARQUIVO = "produtos.csv";

    // Salva toda a lista de produtos no arquivo CSV separado por ponto-e-vírgula (;)
    public static void salvarProdutos(List<Produto> produtos) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CAMINHO_ARQUIVO), StandardCharsets.UTF_8)) {
            // Cabeçalho do CSV
            writer.write("codigo;nome;descricao;dataFabricacao;dataValidade;precoCompra;precoVenda;quantidadeEstoque;idCategoria;nomeCategoria;descricaoCategoria;setorCategoria");
            writer.newLine();

            for (Produto p : produtos) {
                writer.write(p.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo CSV: " + e.getMessage());
        }
    }

    // Carrega os produtos do arquivo CSV ao iniciar o programa
    public static List<Produto> carregarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        Path path = Paths.get(CAMINHO_ARQUIVO);

        if (!Files.exists(path)) {
            return produtos; // Se o arquivo ainda não existir, retorna lista vazia
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linha = reader.readLine(); // Pula a primeira linha (cabeçalho)

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] campos = linha.split(";");
                if (campos.length >= 12) {
                    String codigo = campos[0];
                    String nome = campos[1];
                    String descricao = campos[2];
                    LocalDate dataFabricacao = LocalDate.parse(campos[3]);
                    LocalDate dataValidade = LocalDate.parse(campos[4]);
                    BigDecimal precoCompra = new BigDecimal(campos[5]);
                    BigDecimal precoVenda = new BigDecimal(campos[6]);
                    int quantidadeEstoque = Integer.parseInt(campos[7]);

                    int idCat = Integer.parseInt(campos[8]);
                    String nomeCat = campos[9];
                    String descCat = campos[10];
                    String setorCat = campos[11];

                    Categoria categoria = new Categoria(idCat, nomeCat, descCat, setorCat);
                    Produto produto = new Produto(codigo, nome, descricao, dataFabricacao, dataValidade,
                            precoCompra, precoVenda, quantidadeEstoque, categoria);

                    produtos.add(produto);
                }
            }
        } catch ( Exception e) {
            System.err.println("Erro ao carregar o arquivo CSV: " + e.getMessage());
        }

        return produtos;
    }
}