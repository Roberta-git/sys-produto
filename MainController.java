package org.sysproduto.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.sysproduto.model.Categoria;
import org.sysproduto.model.Produto;
import org.sysproduto.utils.ArquivoCSV;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    // Campos de texto e datas
    @FXML public TextField txtCodigo;
    @FXML public TextField txtNome;
    @FXML public TextField txtDescricao;
    @FXML public DatePicker dpDataFabricacao;
    @FXML public DatePicker dpDataValidade;
    @FXML public TextField txtPrecoCompra;
    @FXML public TextField txtPrecoVenda;
    @FXML public TextField txtQuantidadeEstoque;

    @FXML public TextField txtIdCategoria;
    @FXML public TextField txtNomeCategoria;
    @FXML public TextField txtDescricaoCategoria;
    @FXML public TextField txtSetorCategoria;

    // Tabela e Colunas
    @FXML public TableView<Produto> tabelaProdutos;
    @FXML public TableColumn<Produto, String> colCodigo;
    @FXML public TableColumn<Produto, String> colNome;
    @FXML public TableColumn<Produto, String> colCategoria;
    @FXML public TableColumn<Produto, String> colSetor;
    @FXML public TableColumn<Produto, BigDecimal> colPrecoVenda;
    @FXML public TableColumn<Produto, Integer> colEstoque;
    @FXML public TableColumn<Produto, LocalDate> colValidade;

    @FXML public TextArea txtAreaRelatorio;

    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabela();

        // Carrega os dados do arquivo CSV ao iniciar o sistema
        listaProdutos.addAll(ArquivoCSV.carregarProdutos());
        tabelaProdutos.setItems(listaProdutos);

        tabelaProdutos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                preencherFormulario(selecionado);
            }
        });
    }

    private void configurarTabela() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoria.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategoria().getNome()));
        colSetor.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategoria().getSetor()));
        colPrecoVenda.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        colValidade.setCellValueFactory(new PropertyValueFactory<>("dataValidade"));
    }



    @FXML
    protected void onCadastrarClick() {
        try {
            Produto novoProduto = lerEValidarDadosFormulario();

            // Verifica se já existe um produto com o mesmo código
            Optional<Produto> existente = listaProdutos.stream()
                    .filter(p -> p.getCodigo().equalsIgnoreCase(novoProduto.getCodigo()))
                    .findFirst();

            if (existente.isPresent()) {
                // Atualiza o produto caso o código já exista
                int index = listaProdutos.indexOf(existente.get());
                listaProdutos.set(index, novoProduto);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
            } else {
                // Cadastra novo produto
                listaProdutos.add(novoProduto);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!");
            }

            // Salva no arquivo CSV imediatamente após a operação
            ArquivoCSV.salvarProdutos(listaProdutos);
            tabelaProdutos.refresh();
            onLimparClick();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Erro de Validação", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro inesperado ao cadastrar: " + e.getMessage());
        }
    }

    @FXML
    protected void onConsultarClick() {
        String codigoBusca = txtCodigo.getText().trim();
        if (codigoBusca.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Digite um código no campo 'Código' para consultar.");
            return;
        }

        Optional<Produto> encontrado = listaProdutos.stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigoBusca))
                .findFirst();

        if (encontrado.isPresent()) {
            Produto p = encontrado.get();
            preencherFormulario(p);
            tabelaProdutos.getSelectionModel().select(p);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Produto Encontrado",
                    "Produto localizado: " + p.getNome() + " | Estoque: " + p.getQuantidadeEstoque());
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Não Encontrado",
                    "Nenhum produto encontrado com o código: " + codigoBusca);
        }
    }

    @FXML
    protected void onExcluirClick() {
        String codigo = txtCodigo.getText().trim();
        Produto selecionadoTabela = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (selecionadoTabela == null && codigo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção",
                    "Selecione um produto na tabela ou informe o código para excluir.");
            return;
        }

        String codigoParaExcluir = (selecionadoTabela != null) ? selecionadoTabela.getCodigo() : codigo;

        boolean removido = listaProdutos.removeIf(p -> p.getCodigo().equalsIgnoreCase(codigoParaExcluir));

        if (removido) {
            ArquivoCSV.salvarProdutos(listaProdutos);
            onLimparClick();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto excluído com sucesso!");
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Não Encontrado",
                    "Nenhum produto encontrado com o código: " + codigoParaExcluir);
        }
    }

    @FXML
    protected void onLimparClick() {
        txtCodigo.clear();
        txtNome.clear();
        txtDescricao.clear();
        dpDataFabricacao.setValue(null);
        dpDataValidade.setValue(null);
        txtPrecoCompra.clear();
        txtPrecoVenda.clear();
        txtQuantidadeEstoque.clear();
        txtIdCategoria.clear();
        txtNomeCategoria.clear();
        txtDescricaoCategoria.clear();
        txtSetorCategoria.clear();
        tabelaProdutos.getSelectionModel().clearSelection();
    }



    private Produto lerEValidarDadosFormulario() {
        // 1. Código: formato definido (8 caracteres alfanuméricos)
        String codigo = txtCodigo.getText().trim();
        if (!codigo.matches("^[a-zA-Z0-9]{8}$")) {
            throw new IllegalArgumentException("O Código deve conter exatamente 8 caracteres alfanuméricos (Ex: PROD0001).");
        }

        // 2. Nome: não vazio, tamanho mínimo de 3 caracteres
        String nome = txtNome.getText().trim();
        if (nome.length() < 3) {
            throw new IllegalArgumentException("O Nome do produto não pode ser vazio e deve ter no mínimo 3 caracteres.");
        }

        String descricao = txtDescricao.getText().trim();
        if (descricao.isEmpty()) {
            descricao = "Sem descrição";
        }

        // 3. Data de fabricação: não pode ser posterior à data atual
        LocalDate dataFab = dpDataFabricacao.getValue();
        if (dataFab == null) {
            throw new IllegalArgumentException("Selecione a Data de Fabricação.");
        }
        if (dataFab.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A Data de Fabricação não pode ser posterior à data atual.");
        }

        // 4. Data de validade: não pode ser anterior à data de fabricação
        LocalDate dataVal = dpDataValidade.getValue();
        if (dataVal == null) {
            throw new IllegalArgumentException("Selecione a Data de Validade.");
        }
        if (dataVal.isBefore(dataFab)) {
            throw new IllegalArgumentException("A Data de Validade não pode ser anterior à Data de Fabricação.");
        }

        // 5. Preços: valores positivos e preço de venda > preço de compra
        BigDecimal precoCompra;
        BigDecimal precoVenda;
        try {
            precoCompra = new BigDecimal(txtPrecoCompra.getText().trim().replace(",", "."));
            precoVenda = new BigDecimal(txtPrecoVenda.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe valores numéricos válidos para Preço de Compra e Preço de Venda.");
        }

        if (precoCompra.compareTo(BigDecimal.ZERO) <= 0 || precoVenda.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Os preços de compra e venda devem ser valores positivos (maiores que zero).");
        }
        if (precoVenda.compareTo(precoCompra) <= 0) {
            throw new IllegalArgumentException("O Preço de Venda deve ser maior que o Preço de Compra.");
        }

        // 6. Quantidade em estoque: não negativa (>= 0)
        int estoque;
        try {
            estoque = Integer.parseInt(txtQuantidadeEstoque.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("A Quantidade em Estoque deve ser um número inteiro válido.");
        }
        if (estoque < 0) {
            throw new IllegalArgumentException("A Quantidade em Estoque não pode ser negativa.");
        }

        // 7. Validação da Categoria
        int idCategoria;
        try {
            idCategoria = Integer.parseInt(txtIdCategoria.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O ID da Categoria deve ser um número inteiro válido.");
        }

        String nomeCategoria = txtNomeCategoria.getText().trim();
        String descCategoria = txtDescricaoCategoria.getText().trim();
        String setorCategoria = txtSetorCategoria.getText().trim();

        if (nomeCategoria.isEmpty() || setorCategoria.isEmpty()) {
            throw new IllegalArgumentException("Preencha o Nome e o Setor da Categoria.");
        }
        if (descCategoria.isEmpty()) {
            descCategoria = "Sem descrição";
        }

        Categoria categoria = new Categoria(idCategoria, nomeCategoria, descCategoria, setorCategoria);
        return new Produto(codigo, nome, descricao, dataFab, dataVal, precoCompra, precoVenda, estoque, categoria);
    }

    private void preencherFormulario(Produto p) {
        txtCodigo.setText(p.getCodigo());
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        dpDataFabricacao.setValue(p.getDataFabricacao());
        dpDataValidade.setValue(p.getDataValidade());
        txtPrecoCompra.setText(p.getPrecoCompra().toString());
        txtPrecoVenda.setText(p.getPrecoVenda().toString());
        txtQuantidadeEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));

        if (p.getCategoria() != null) {
            txtIdCategoria.setText(String.valueOf(p.getCategoria().getId()));
            txtNomeCategoria.setText(p.getCategoria().getNome());
            txtDescricaoCategoria.setText(p.getCategoria().getDescricao());
            txtSetorCategoria.setText(p.getCategoria().getSetor());
        }
    }



    //  Filtrar produtos próximos ao vencimento (próximos 60 dias) usando filter e map
    @FXML
    protected void onRelatorioVencimentoClick() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(60);

        List<String> resultado = listaProdutos.stream()
                .filter(p -> !p.getDataValidade().isBefore(hoje) && !p.getDataValidade().isAfter(limite))
                .map(p -> {
                    long dias = ChronoUnit.DAYS.between(hoje, p.getDataValidade());
                    return String.format("- [%s] %s | Validade: %s (Vence em %d dias)",
                            p.getCodigo(), p.getNome(), p.getDataValidade(), dias);
                })
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("=== RELATÓRIO 1: PRODUTOS PRÓXIMOS AO VENCIMENTO (PRÓXIMOS 60 DIAS) ===\n\n");
        if (resultado.isEmpty()) {
            sb.append("Nenhum produto vencendo nos próximos 60 dias.");
        } else {
            resultado.forEach(linha -> sb.append(linha).append("\n"));
        }
        txtAreaRelatorio.setText(sb.toString());
    }

    //  Filtrar produtos com estoque baixo (< 10 unidades) usando filter, map e reduce
    @FXML
    protected void onRelatorioEstoqueBaixoClick() {
        List<Produto> estoqueBaixo = listaProdutos.stream()
                .filter(p -> p.getQuantidadeEstoque() < 10)
                .collect(Collectors.toList());

        // Uso de map e reduce para somar o total de itens dos produtos com estoque crítico
        int somaUnidadesCriticas = estoqueBaixo.stream()
                .map(Produto::getQuantidadeEstoque)
                .reduce(0, Integer::sum);

        StringBuilder sb = new StringBuilder("=== RELATÓRIO 2: PRODUTOS COM ESTOQUE BAIXO (MENOS DE 10 UNIDADES) ===\n\n");
        if (estoqueBaixo.isEmpty()) {
            sb.append("Nenhum produto com estoque baixo.");
        } else {
            estoqueBaixo.forEach(p -> sb.append(String.format("- [%s] %s | Estoque atual: %d un. | Setor: %s\n",
                    p.getCodigo(), p.getNome(), p.getQuantidadeEstoque(), p.getCategoria().getSetor())));
            sb.append("\nTotal de produtos em alerta: ").append(estoqueBaixo.size());
            sb.append("\nSoma de unidades críticas (via map/reduce): ").append(somaUnidadesCriticas).append(" un.");
        }
        txtAreaRelatorio.setText(sb.toString());
    }

    // Calcular margem de lucro média por categoria usando groupingBy, map e reduce
    @FXML
    protected void onRelatorioMargemCategoriaClick() {
        StringBuilder sb = new StringBuilder("=== RELATÓRIO 3: MARGEM DE LUCRO MÉDIA POR CATEGORIA ===\n\n");

        if (listaProdutos.isEmpty()) {
            sb.append("Nenhum produto cadastrado.");
            txtAreaRelatorio.setText(sb.toString());
            return;
        }

        Map<String, List<Produto>> porCategoria = listaProdutos.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria().getNome()));

        porCategoria.forEach((nomeCategoria, produtosDaCategoria) -> {
            // Uso explícito de map e reduce para somar as margens de lucro da categoria
            BigDecimal somaMargens = produtosDaCategoria.stream()
                    .map(Produto::calcularMargemLucro)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal media = somaMargens.divide(
                    new BigDecimal(produtosDaCategoria.size()), 2, RoundingMode.HALF_UP);

            sb.append(String.format("Categoria: %-20s | Qtd. Produtos: %d | Margem de Lucro Média: %s%%\n",
                    nomeCategoria, produtosDaCategoria.size(), media.toPlainString()));
        });

        txtAreaRelatorio.setText(sb.toString());
    }

    //  Listar produtos agrupados por setor de categoria usando groupingBy
    @FXML
    protected void onRelatorioAgrupadoSetorClick() {
        StringBuilder sb = new StringBuilder("=== RELATÓRIO 4: PRODUTOS AGRUPADOS POR SETOR DE CATEGORIA ===\n\n");

        if (listaProdutos.isEmpty()) {
            sb.append("Nenhum produto cadastrado.");
            txtAreaRelatorio.setText(sb.toString());
            return;
        }

        Map<String, List<Produto>> porSetor = listaProdutos.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria().getSetor()));

        porSetor.forEach((setor, produtosDoSetor) -> {
            sb.append("SETOR: ").append(setor.toUpperCase()).append("\n");
            produtosDoSetor.forEach(p -> sb.append(String.format("   • [%s] %s (%s) - R$ %s | Estoque: %d\n",
                    p.getCodigo(), p.getNome(), p.getCategoria().getNome(),
                    p.getPrecoVenda().toPlainString(), p.getQuantidadeEstoque())));
            sb.append("\n");
        });

        txtAreaRelatorio.setText(sb.toString());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}