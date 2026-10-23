# Sistema de Gerenciamento de Produtos - POO II 

Sistema desenvolvido em Java com interface gráfica **JavaFX** para gerenciar o cadastro de produtos de uma loja ou supermercado, aplicando conceitos de Programação Orientada a Objetos, persistência em arquivo CSV e processamento de dados com a API Stream do Java.

* **Disciplina:** Programação Orientada a Objetos II
* **Professor:** Daniel Corrêa da Silva
* **Instituição:** Centro Universitário Alfredo Nasser 

---

## 🚀 Como Executar o Programa

1. **Pré-requisitos:** Ter o **Java JDK 17**  e a IDE **IntelliJ IDEA** instalados no computador.
2. **Abrir o Projeto:**
    * No IntelliJ IDEA, clique em **File > Open** e selecione a pasta `sys-produto`.
    * Espere alguns segundos para o **Maven** carregar as dependências do JavaFX automaticamente (que está no arquivo `pom.xml`).
3. **Iniciar o Sistema:**
    * Na barra lateral esquerda do projeto, procure o arquivo principal:
      `src/main/java/org/sysproduto/Main.java`
    * Clique em **Run (Play)** ao lado da classe `Main`.

---

## 📝 Relatório: Funcionalidades Implementadas e Decisões de Design

### 1. Funcionalidades Implementadas
* **Operações Básicas (CRUD Completo):**
    * **Cadastrar e Atualizar:** Insere um novo produto com todos os seus atributos e sua categoria. Caso o código já exista, o sistema atualiza os dados do produto existente.
    * **Consultar por Código:** Busca um produto pelo seu código único utilizando Streams, selecionando-o na tabela e preenchendo o formulário.
    * **Excluir:** Remove um produto selecionado na tabela ou indicado pelo código.
    * **Listar Todos:** Exibe permanentemente todos os produtos cadastrados em uma tabela interativa.
* **Validação de Dados de Entrada:**
    * **Código:** Validado pela expressão regular (`^[a-zA-Z0-9]{8}$`) para exigir exatamente 8 caracteres alfanuméricos.
    * **Nome:** Validação contra campos vazios, exigindo no mínimo 3 caracteres.
    * **Datas:** Verifica se a data de fabricação não é posterior à data atual e garante que a data de validade não seja anterior à data de fabricação.
    * **Preços:** Exige valores numéricos positivos e valida que o preço de venda seja maior que o preço de compra.
    * **Estoque:** Impede o cadastro de quantidades negativas.
* **Persistência em Arquivo CSV:**
    * Ao iniciar o programa, o método `initialize()` do controlador carrega automaticamente os registros salvos no arquivo `produtos.csv`.
    * Após cada cadastro, alteração ou exclusão, o arquivo `produtos.csv` é atualizado utilizando o separador ponto-e-vírgula (`;`).
* **Relatórios com a API Stream do Java:**
    1. **Próximos ao Vencimento (60 dias):** Utiliza `filter` e `map` para listar produtos que vencem entre a data de hoje e os próximos 60 dias, calculando os dias restantes.
    2. **Estoque Baixo (< 10 unidades):** Utiliza `filter` para encontrar produtos com menos de 10 unidades e aplica `map` e `reduce` para somar o total de unidades em estado crítico.
    3. **Margem de Lucro Média por Categoria:** Agrupa os produtos por categoria com `Collectors.groupingBy` e utiliza `map` e `reduce` para somar as margens percentuais e calcular a média de cada categoria.
    4. **Produtos Agrupados por Setor:** Utiliza `Collectors.groupingBy` para organizar e exibir os produtos separados pelo setor de sua categoria.

### 2. Decisões de Design e Arquitetura
* **Padrão MVC (Model-View-Controller):** O código foi organizado nos pacotes `model`, `view`, `controller` e `utils`. Facilitando a manutenção e leitura do código.
* **Composição em POO:** A classe `Produto` possui um atributo do tipo `Categoria`, aplicando o relacionamento de composição ("tem-um") e encapsulamento com métodos *getters* e *setters*.
* **Uso de `BigDecimal` e `LocalDate`:** Para evitar erros de arredondamento em preços tipo `double`, utilizou-se `BigDecimal` nos preços e cálculos de margem de lucro. Para datas e cálculo de prazos de validade, utilizou-se a API `java.time.LocalDate`.
* **Tratamento de Exceções:** Todas as entradas do usuário passam por blocos `try-catch`. Erros de validação ou formatação numérica lançam exceções tratadas que exibem janelas de alerta (`Alert`) claras para o usuário, impedindo que o programa trave.