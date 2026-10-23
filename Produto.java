package org.sysproduto.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Produto {
    private String codigo;
    private String nome;
    private String descricao;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;
    private int quantidadeEstoque;
    private Categoria categoria; // Composição com a classe Categoria

    public Produto() {
    }

    public Produto(String codigo, String nome, String descricao, LocalDate dataFabricacao,
                   LocalDate dataValidade, BigDecimal precoCompra, BigDecimal precoVenda,
                   int quantidadeEstoque, Categoria categoria) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
    }

    // Método de negócio para calcular a margem de lucro em porcentagem (%)
    public BigDecimal calcularMargemLucro() {
        if (precoCompra == null || precoCompra.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // Fórmula: ((precoVenda - precoCompra) / precoCompra) * 100
        return precoVenda.subtract(precoCompra)
                .divide(precoCompra, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Converte os atributos do produto para uma linha CSV separada por ;
    public String toCSV() {
        return String.join(";",
                codigo,
                nome,
                descricao,
                dataFabricacao.toString(),
                dataValidade.toString(),
                precoCompra.toString(),
                precoVenda.toString(),
                String.valueOf(quantidadeEstoque),
                String.valueOf(categoria.getId()),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getSetor()
        );
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataFabricacao() {
        return dataFabricacao;
    }

    public void setDataFabricacao(LocalDate dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return codigo + " - " + nome + " (Estoque: " + quantidadeEstoque + ")";
    }
}